package com.example.googleoidcdemo.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ApiService {
    companion object {
        private const val TAG = "ApiService"
        private const val BASE_URL = "http://10.0.2.2:8080/api" // Use 10.0.2.2 for Android emulator
        // For physical device, use your computer's IP address: "http://192.168.1.XXX:8080/api"
        
        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    data class ApiResponse<T>(
        val success: Boolean,
        val message: String?,
        val data: T?,
        val error: String?
    )

    data class AuthResponse(
        val user: UserData,
        val tokens: TokenData
    )

    data class UserData(
        val id: String,
        val email: String?,
        val phone: String?,
        val name: String,
        val firstName: String,
        val lastName: String,
        val picture: String,
        val verifiedEmail: Boolean,
        val verifiedPhone: Boolean,
        val lastLogin: String,
        val loginCount: Int,
        val preferences: UserPreferences
    )

    data class UserPreferences(
        val theme: String,
        val notifications: Boolean
    )

    data class TokenData(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Int
    )

    data class RefreshTokenRequest(
        val refreshToken: String
    )

    data class GoogleCallbackRequest(
        val code: String,
        val state: String?
    )

    data class RegistrationRequest(
        val name: String,
        val email: String?,
        val phone: String?,
        val password: String
    )

    data class LoginRequest(
        val identifier: String,
        val password: String
    )

    suspend fun register(request: RegistrationRequest): ApiResponse<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("name", request.name)
                    if (request.email != null) put("email", request.email)
                    if (request.phone != null) put("phone", request.phone)
                    put("password", request.password)
                }.toString()

                val request = Request.Builder()
                    .url("$BASE_URL/auth/register")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d(TAG, "Registration response: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val data = json.getJSONObject("data")
                    val user = data.getJSONObject("user")
                    val tokens = data.getJSONObject("tokens")

                    val authResponse = AuthResponse(
                        user = UserData(
                            id = user.getString("id"),
                            email = user.optString("email", null).takeIf { it.isNotEmpty() },
                            phone = user.optString("phone", null).takeIf { it.isNotEmpty() },
                            name = user.getString("name"),
                            firstName = user.optString("firstName", ""),
                            lastName = user.optString("lastName", ""),
                            picture = user.optString("picture", ""),
                            verifiedEmail = user.getBoolean("verifiedEmail"),
                            verifiedPhone = user.getBoolean("verifiedPhone"),
                            lastLogin = user.getString("lastLogin"),
                            loginCount = user.getInt("loginCount"),
                            preferences = UserPreferences(
                                theme = user.getJSONObject("preferences").getString("theme"),
                                notifications = user.getJSONObject("preferences").getBoolean("notifications")
                            )
                        ),
                        tokens = TokenData(
                            accessToken = tokens.getString("accessToken"),
                            refreshToken = tokens.getString("refreshToken"),
                            expiresIn = tokens.getInt("expiresIn")
                        )
                    )

                    ApiResponse(
                        success = true,
                        message = json.optString("message"),
                        data = authResponse,
                        error = null
                    )
                } else {
                    val errorJson = JSONObject(responseBody)
                    ApiResponse(
                        success = false,
                        message = errorJson.optString("message"),
                        data = null,
                        error = errorJson.optString("error")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Registration error", e)
                ApiResponse(
                    success = false,
                    message = "Network error",
                    data = null,
                    error = e.message
                )
            }
        }
    }

    suspend fun login(request: LoginRequest): ApiResponse<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("identifier", request.identifier)
                    put("password", request.password)
                }.toString()

                val request = Request.Builder()
                    .url("$BASE_URL/auth/login")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d(TAG, "Login response: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val data = json.getJSONObject("data")
                    val user = data.getJSONObject("user")
                    val tokens = data.getJSONObject("tokens")

                    val authResponse = AuthResponse(
                        user = UserData(
                            id = user.getString("id"),
                            email = user.optString("email", null).takeIf { it.isNotEmpty() },
                            phone = user.optString("phone", null).takeIf { it.isNotEmpty() },
                            name = user.getString("name"),
                            firstName = user.optString("firstName", ""),
                            lastName = user.optString("lastName", ""),
                            picture = user.optString("picture", ""),
                            verifiedEmail = user.getBoolean("verifiedEmail"),
                            verifiedPhone = user.getBoolean("verifiedPhone"),
                            lastLogin = user.getString("lastLogin"),
                            loginCount = user.getInt("loginCount"),
                            preferences = UserPreferences(
                                theme = user.getJSONObject("preferences").getString("theme"),
                                notifications = user.getJSONObject("preferences").getBoolean("notifications")
                            )
                        ),
                        tokens = TokenData(
                            accessToken = tokens.getString("accessToken"),
                            refreshToken = tokens.getString("refreshToken"),
                            expiresIn = tokens.getInt("expiresIn")
                        )
                    )

                    ApiResponse(
                        success = true,
                        message = json.optString("message"),
                        data = authResponse,
                        error = null
                    )
                } else {
                    val errorJson = JSONObject(responseBody)
                    ApiResponse(
                        success = false,
                        message = errorJson.optString("message"),
                        data = null,
                        error = errorJson.optString("error")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                ApiResponse(
                    success = false,
                    message = "Network error",
                    data = null,
                    error = e.message
                )
            }
        }
    }

    suspend fun authenticateWithGoogle(code: String, state: String? = null): ApiResponse<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("code", code)
                    if (state != null) put("state", state)
                }.toString()

                val request = Request.Builder()
                    .url("$BASE_URL/auth/google/callback")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d(TAG, "Google auth response: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val data = json.getJSONObject("data")
                    val user = data.getJSONObject("user")
                    val tokens = data.getJSONObject("tokens")

                    val authResponse = AuthResponse(
                        user = UserData(
                            id = user.getString("id"),
                            email = user.optString("email", null).takeIf { it.isNotEmpty() },
                            phone = user.optString("phone", null).takeIf { it.isNotEmpty() },
                            name = user.getString("name"),
                            firstName = user.optString("firstName", ""),
                            lastName = user.optString("lastName", ""),
                            picture = user.optString("picture", ""),
                            verifiedEmail = user.getBoolean("verifiedEmail"),
                            verifiedPhone = user.getBoolean("verifiedPhone"),
                            lastLogin = user.getString("lastLogin"),
                            loginCount = user.getInt("loginCount"),
                            preferences = UserPreferences(
                                theme = user.getJSONObject("preferences").getString("theme"),
                                notifications = user.getJSONObject("preferences").getBoolean("notifications")
                            )
                        ),
                        tokens = TokenData(
                            accessToken = tokens.getString("accessToken"),
                            refreshToken = tokens.getString("refreshToken"),
                            expiresIn = tokens.getInt("expiresIn")
                        )
                    )

                    ApiResponse(
                        success = true,
                        message = json.optString("message"),
                        data = authResponse,
                        error = null
                    )
                } else {
                    val errorJson = JSONObject(responseBody)
                    ApiResponse(
                        success = false,
                        message = errorJson.optString("message"),
                        data = null,
                        error = errorJson.optString("error")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Google authentication error", e)
                ApiResponse(
                    success = false,
                    message = "Network error",
                    data = null,
                    error = e.message
                )
            }
        }
    }

    suspend fun refreshToken(refreshToken: String): ApiResponse<TokenData> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("refreshToken", refreshToken)
                }.toString()

                val request = Request.Builder()
                    .url("$BASE_URL/auth/refresh")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d(TAG, "Refresh token response: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val data = json.getJSONObject("data")
                    val tokens = data.getJSONObject("tokens")

                    val tokenData = TokenData(
                        accessToken = tokens.getString("accessToken"),
                        refreshToken = tokens.getString("refreshToken"),
                        expiresIn = tokens.getInt("expiresIn")
                    )

                    ApiResponse(
                        success = true,
                        message = json.optString("message"),
                        data = tokenData,
                        error = null
                    )
                } else {
                    val errorJson = JSONObject(responseBody)
                    ApiResponse(
                        success = false,
                        message = errorJson.optString("message"),
                        data = null,
                        error = errorJson.optString("error")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Token refresh error", e)
                ApiResponse(
                    success = false,
                    message = "Network error",
                    data = null,
                    error = e.message
                )
            }
        }
    }

    suspend fun getUserProfile(accessToken: String): ApiResponse<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL/auth/profile")
                    .addHeader("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d(TAG, "Get profile response: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val data = json.getJSONObject("data")
                    val user = data.getJSONObject("user")

                    val userData = UserData(
                        id = user.getString("id"),
                        email = user.optString("email", null).takeIf { it.isNotEmpty() },
                        phone = user.optString("phone", null).takeIf { it.isNotEmpty() },
                        name = user.getString("name"),
                        firstName = user.optString("firstName", ""),
                        lastName = user.optString("lastName", ""),
                        picture = user.optString("picture", ""),
                        verifiedEmail = user.getBoolean("verifiedEmail"),
                        verifiedPhone = user.getBoolean("verifiedPhone"),
                        lastLogin = user.getString("lastLogin"),
                        loginCount = user.getInt("loginCount"),
                        preferences = UserPreferences(
                            theme = user.getJSONObject("preferences").getString("theme"),
                            notifications = user.getJSONObject("preferences").getBoolean("notifications")
                        )
                    )

                    ApiResponse(
                        success = true,
                        message = json.optString("message"),
                        data = userData,
                        error = null
                    )
                } else {
                    val errorJson = JSONObject(responseBody)
                    ApiResponse(
                        success = false,
                        message = errorJson.optString("message"),
                        data = null,
                        error = errorJson.optString("error")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get profile error", e)
                ApiResponse(
                    success = false,
                    message = "Network error",
                    data = null,
                    error = e.message
                )
            }
        }
    }

    suspend fun logout(refreshToken: String): ApiResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("refreshToken", refreshToken)
                }.toString()

                val request = Request.Builder()
                    .url("$BASE_URL/auth/logout")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d(TAG, "Logout response: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    ApiResponse(
                        success = true,
                        message = json.optString("message"),
                        data = Unit,
                        error = null
                    )
                } else {
                    val errorJson = JSONObject(responseBody)
                    ApiResponse(
                        success = false,
                        message = errorJson.optString("message"),
                        data = null,
                        error = errorJson.optString("error")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Logout error", e)
                ApiResponse(
                    success = false,
                    message = "Network error",
                    data = null,
                    error = e.message
                )
            }
        }
    }

    suspend fun checkAuth(accessToken: String): ApiResponse<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL/auth/check")
                    .addHeader("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d(TAG, "Check auth response: $responseBody")

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val data = json.getJSONObject("data")
                    val user = data.getJSONObject("user")

                    val userData = UserData(
                        id = user.getString("id"),
                        email = user.optString("email", null).takeIf { it.isNotEmpty() },
                        phone = user.optString("phone", null).takeIf { it.isNotEmpty() },
                        name = user.getString("name"),
                        firstName = user.optString("firstName", ""),
                        lastName = user.optString("lastName", ""),
                        picture = user.optString("picture", ""),
                        verifiedEmail = user.getBoolean("verifiedEmail"),
                        verifiedPhone = user.getBoolean("verifiedPhone"),
                        lastLogin = user.getString("lastLogin"),
                        loginCount = user.getInt("loginCount"),
                        preferences = UserPreferences(
                            theme = user.getJSONObject("preferences").getString("theme"),
                            notifications = user.getJSONObject("preferences").getBoolean("notifications")
                        )
                    )

                    ApiResponse(
                        success = true,
                        message = json.optString("message"),
                        data = userData,
                        error = null
                    )
                } else {
                    val errorJson = JSONObject(responseBody)
                    ApiResponse(
                        success = false,
                        message = errorJson.optString("message"),
                        data = null,
                        error = errorJson.optString("error")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Check auth error", e)
                ApiResponse(
                    success = false,
                    message = "Network error",
                    data = null,
                    error = e.message
                )
            }
        }
    }
}
