package com.example.googleoidcdemo.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

class LocalizationManager(private val context: Context) {
    
    companion object {
        private const val DEFAULT_LOCALE = "en_EN"
        private const val LOCALE_FILE_PREFIX = "locale/"
        private const val LOCALE_FILE_SUFFIX = ".json"
    }
    
    private var currentLocale: String = DEFAULT_LOCALE
    private var localeData: Map<String, Any> = emptyMap()
    
    init {
        loadLocaleData(currentLocale)
    }
    
    /**
     * Set the app locale and load the corresponding locale data
     */
    fun setLocale(locale: String) {
        currentLocale = locale
        loadLocaleData(locale)
        updateAppLocale(locale)
    }
    
    /**
     * Get the current locale
     */
    fun getCurrentLocale(): String = currentLocale
    
    /**
     * Load locale data from assets
     */
    private fun loadLocaleData(locale: String) {
        try {
            val fileName = "$LOCALE_FILE_PREFIX$locale$LOCALE_FILE_SUFFIX"
            val inputStream = context.assets.open(fileName)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            
            // Parse JSON using simple approach
            localeData = parseJsonToMap(jsonString)
        } catch (e: Exception) {
            // Fallback to default locale
            if (locale != DEFAULT_LOCALE) {
                loadLocaleData(DEFAULT_LOCALE)
            }
        }
    }
    
    /**
     * Get localized string by key
     */
    fun getString(key: String, vararg args: Any): String {
        val value = getNestedValue(localeData, key)
        return when (value) {
            is String -> {
                if (args.isNotEmpty()) {
                    formatString(value, args)
                } else {
                    value
                }
            }
            else -> key // Fallback to key if not found
        }
    }
    
    /**
     * Get nested value from map using dot notation (e.g., "app.name")
     */
    private fun getNestedValue(map: Map<String, Any>, key: String): Any? {
        val keys = key.split(".")
        var current: Any? = map
        
        for (k in keys) {
            when (current) {
                is Map<*, *> -> current = current[k]
                else -> return null
            }
        }
        
        return current
    }
    
    /**
     * Format string with arguments
     */
    private fun formatString(template: String, args: Array<out Any>): String {
        var result = template
        args.forEachIndexed { index, arg ->
            result = result.replace("{${index}}", arg.toString())
        }
        return result
    }
    
    /**
     * Update app locale
     */
    private fun updateAppLocale(locale: String) {
        val localeParts = locale.split("_")
        val language = localeParts[0]
        val country = if (localeParts.size > 1) localeParts[1] else ""
        
        val newLocale = if (country.isNotEmpty()) {
            Locale(language, country)
        } else {
            Locale(language)
        }
        
        Locale.setDefault(newLocale)
        
        val resources = context.resources
        val config = Configuration(resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(newLocale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = newLocale
        }
        
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    
    /**
     * Simple JSON parser for basic JSON structure
     */
    private fun parseJsonToMap(jsonString: String): Map<String, Any> {
        return try {
            parseJsonObject(jsonString.trim())
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    /**
     * Simple JSON object parser
     */
    private fun parseJsonObject(json: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        var i = 0
        
        // Skip opening brace
        if (json[i] == '{') i++
        
        while (i < json.length && json[i] != '}') {
            // Skip whitespace
            while (i < json.length && json[i].isWhitespace()) i++
            
            if (i >= json.length || json[i] == '}') break
            
            // Parse key
            val key = parseJsonString(json, i)
            i = key.second
            
            // Skip colon
            while (i < json.length && json[i] != ':') i++
            i++ // Skip colon
            
            // Skip whitespace
            while (i < json.length && json[i].isWhitespace()) i++
            
            // Parse value
            val value = parseJsonValue(json, i)
            i = value.second
            
            result[key.first] = value.first
            
            // Skip comma
            while (i < json.length && (json[i].isWhitespace() || json[i] == ',')) i++
        }
        
        return result
    }
    
    /**
     * Parse JSON string value
     */
    private fun parseJsonString(json: String, start: Int): Pair<String, Int> {
        var i = start
        if (json[i] == '"') i++ // Skip opening quote
        
        val sb = StringBuilder()
        while (i < json.length && json[i] != '"') {
            if (json[i] == '\\' && i + 1 < json.length) {
                i++ // Skip backslash
                when (json[i]) {
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    'r' -> sb.append('\r')
                    '\\' -> sb.append('\\')
                    '"' -> sb.append('"')
                    else -> sb.append(json[i])
                }
            } else {
                sb.append(json[i])
            }
            i++
        }
        
        if (i < json.length && json[i] == '"') i++ // Skip closing quote
        
        return Pair(sb.toString(), i)
    }
    
    /**
     * Parse JSON value (string, number, boolean, object, array)
     */
    private fun parseJsonValue(json: String, start: Int): Pair<Any, Int> {
        var i = start
        
        // Skip whitespace
        while (i < json.length && json[i].isWhitespace()) i++
        
        return when (json[i]) {
            '"' -> {
                val result = parseJsonString(json, i)
                Pair(result.first, result.second)
            }
            '{' -> {
                val result = parseJsonObject(json.substring(i))
                Pair(result, i + findMatchingBrace(json, i))
            }
            '[' -> {
                val result = parseJsonArray(json.substring(i))
                Pair(result, i + findMatchingBracket(json, i))
            }
            't' -> {
                if (json.substring(i).startsWith("true")) {
                    Pair(true, i + 4)
                } else {
                    Pair("", i)
                }
            }
            'f' -> {
                if (json.substring(i).startsWith("false")) {
                    Pair(false, i + 5)
                } else {
                    Pair("", i)
                }
            }
            'n' -> {
                if (json.substring(i).startsWith("null")) {
                    Pair(null, i + 4)
                } else {
                    Pair("", i)
                }
            }
            else -> {
                // Number
                val sb = StringBuilder()
                while (i < json.length && (json[i].isDigit() || json[i] == '.' || json[i] == '-')) {
                    sb.append(json[i])
                    i++
                }
                val numStr = sb.toString()
                Pair(if (numStr.contains('.')) numStr.toDoubleOrNull() ?: 0.0 else numStr.toIntOrNull() ?: 0, i)
            }
        }
    }
    
    /**
     * Parse JSON array
     */
    private fun parseJsonArray(json: String): List<Any> {
        val result = mutableListOf<Any>()
        var i = 1 // Skip opening bracket
        
        while (i < json.length && json[i] != ']') {
            // Skip whitespace
            while (i < json.length && json[i].isWhitespace()) i++
            
            if (i >= json.length || json[i] == ']') break
            
            // Parse value
            val value = parseJsonValue(json, i)
            i = value.second
            
            result.add(value.first)
            
            // Skip comma
            while (i < json.length && (json[i].isWhitespace() || json[i] == ',')) i++
        }
        
        return result
    }
    
    /**
     * Find matching closing brace
     */
    private fun findMatchingBrace(json: String, start: Int): Int {
        var i = start
        var braceCount = 0
        
        while (i < json.length) {
            when (json[i]) {
                '{' -> braceCount++
                '}' -> {
                    braceCount--
                    if (braceCount == 0) return i - start + 1
                }
            }
            i++
        }
        
        return json.length - start
    }
    
    /**
     * Find matching closing bracket
     */
    private fun findMatchingBracket(json: String, start: Int): Int {
        var i = start
        var bracketCount = 0
        
        while (i < json.length) {
            when (json[i]) {
                '[' -> bracketCount++
                ']' -> {
                    bracketCount--
                    if (bracketCount == 0) return i - start + 1
                }
            }
            i++
        }
        
        return json.length - start
    }
}
