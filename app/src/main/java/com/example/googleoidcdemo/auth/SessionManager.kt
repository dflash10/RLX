package com.example.googleoidcdemo.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.googleoidcdemo.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionManager(private val context: Context) {
    companion object {
        private const val TAG = "SessionManager"
        private const val PREFS_NAME = "rhealth_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_FIRST_NAME = "user_first_name"
        private const val KEY_USER_LAST_NAME = "user_last_name"
        private const val KEY_USER_PICTURE = "user_picture"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val apiService = ApiService()

    data class UserSession(
        val accessToken: String,
        val refreshToken: String,
        val userId: String,
        val email: String?,
        val phone: String?,
        val name: String,
        val firstName: String,
        val lastName: String,
        val picture: String,
        val isLoggedIn: Boolean,
        val tokenExpiry: Long
    )

    fun saveSession(authResponse: ApiService.AuthResponse) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, authResponse.tokens.accessToken)
        editor.putString(KEY_REFRESH_TOKEN, authResponse.tokens.refreshToken)
        editor.putString(KEY_USER_ID, authResponse.user.id)
        editor.putString(KEY_USER_EMAIL, authResponse.user.email)
        editor.putString(KEY_USER_PHONE, authResponse.user.phone)
        editor.putString(KEY_USER_NAME, authResponse.user.name)
        editor.putString(KEY_USER_FIRST_NAME, authResponse.user.firstName)
        editor.putString(KEY_USER_LAST_NAME, authResponse.user.lastName)
        editor.putString(KEY_USER_PICTURE, authResponse.user.picture)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (authResponse.tokens.expiresIn * 1000L))
        editor.apply()
        
        Log.d(TAG, "Session saved for user: ${authResponse.user.email ?: authResponse.user.phone}")
    }

    fun getCurrentSession(): UserSession? {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return null

        val accessToken = prefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null) ?: return null
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, null)
        val phone = prefs.getString(KEY_USER_PHONE, null)
        val name = prefs.getString(KEY_USER_NAME, null) ?: return null
        val firstName = prefs.getString(KEY_USER_FIRST_NAME, "") ?: ""
        val lastName = prefs.getString(KEY_USER_LAST_NAME, "") ?: ""
        val picture = prefs.getString(KEY_USER_PICTURE, "") ?: ""
        val tokenExpiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0L)

        return UserSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            phone = phone,
            name = name,
            firstName = firstName,
            lastName = lastName,
            picture = picture,
            isLoggedIn = isLoggedIn,
            tokenExpiry = tokenExpiry
        )
    }

    fun isLoggedIn(): Boolean {
        val session = getCurrentSession()
        return session != null && session.isLoggedIn && !isTokenExpired(session)
    }

    fun isTokenExpired(session: UserSession? = null): Boolean {
        val currentSession = session ?: getCurrentSession() ?: return true
        val currentTime = System.currentTimeMillis()
        val bufferTime = 5 * 60 * 1000L // 5 minutes buffer
        return currentTime >= (currentSession.tokenExpiry - bufferTime)
    }

    suspend fun refreshSessionIfNeeded(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val session = getCurrentSession() ?: return@withContext false
                
                if (!isTokenExpired(session)) {
                    Log.d(TAG, "Token is still valid")
                    return@withContext true
                }

                Log.d(TAG, "Token expired, attempting refresh")
                val refreshResponse = apiService.refreshToken(session.refreshToken)
                
                if (refreshResponse.success && refreshResponse.data != null) {
                    val newTokens = refreshResponse.data!!
                    val editor = prefs.edit()
                    editor.putString(KEY_ACCESS_TOKEN, newTokens.accessToken)
                    editor.putString(KEY_REFRESH_TOKEN, newTokens.refreshToken)
                    editor.putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (newTokens.expiresIn * 1000L))
                    editor.apply()
                    
                    Log.d(TAG, "Session refreshed successfully")
                    return@withContext true
                } else {
                    Log.e(TAG, "Failed to refresh session: ${refreshResponse.message}")
                    clearSession()
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing session", e)
                clearSession()
                return@withContext false
            }
        }
    }

    suspend fun validateSession(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val session = getCurrentSession() ?: return@withContext false
                
                // For Google OIDC, we just need to check if the session exists and is not expired
                // No need to validate with server since it's a native Google authentication
                if (session.isLoggedIn && !isTokenExpired(session)) {
                    Log.d(TAG, "Session validated successfully (Google OIDC)")
                    return@withContext true
                } else {
                    Log.e(TAG, "Session validation failed: session expired or invalid")
                    clearSession()
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error validating session", e)
                clearSession()
                return@withContext false
            }
        }
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
        Log.d(TAG, "Session cleared")
    }

    suspend fun logout(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val session = getCurrentSession()
                if (session != null) {
                    val logoutResponse = apiService.logout(session.refreshToken)
                    if (!logoutResponse.success) {
                        Log.w(TAG, "Server logout failed: ${logoutResponse.message}")
                    }
                }
                clearSession()
                Log.d(TAG, "Logged out successfully")
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
                clearSession() // Clear local session even if server logout fails
                return@withContext false
            }
        }
    }

    fun getAccessToken(): String? {
        val session = getCurrentSession()
        return if (session != null && !isTokenExpired(session)) {
            session.accessToken
        } else {
            null
        }
    }

    fun getUserInfo(): UserSession? {
        return getCurrentSession()
    }
}
