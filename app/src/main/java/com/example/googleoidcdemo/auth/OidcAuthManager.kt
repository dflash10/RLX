package com.example.googleoidcdemo.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.googleoidcdemo.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class OidcAuthManager(private val context: Context) {

    companion object {
        private const val TAG = "OidcAuthManager"
        private const val GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo"
        private const val GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo"
    }

    private var googleSignInClient: GoogleSignInClient
    private var httpClient: OkHttpClient

    init {
        // Configure Google Sign-In for OIDC
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.server_client_id)) // Server client ID for ID token
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        
        // Initialize HTTP client for API calls
        httpClient = HttpClientConfig(context).createHttpClient()
    }

    /**
     * Start the native Google Sign-In flow
     */
    fun getSignInIntent(): Intent {
        Log.d(TAG, "Starting native Google Sign-In flow")
        Log.d(TAG, "Using server client ID: ${context.getString(R.string.server_client_id)}")
        return googleSignInClient.signInIntent
    }

    /**
     * Handle the Google Sign-In result
     */
    suspend fun handleSignInResult(data: Intent?): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                
                if (account != null) {
                    Log.d(TAG, "Google Sign-In successful: ${account.email}")
                    
                    // Validate the ID token with Google's OIDC endpoint
                    val idToken = account.idToken
                    if (idToken != null) {
                        val validationResult = validateIdToken(idToken)
                        if (validationResult != null) {
                            Log.d(TAG, "ID token validated successfully")
                            
                            // Get user info from the validated token
                            val fullName = validationResult.getString("name") ?: account.displayName ?: ""
                            val nameParts = fullName.split(" ")
                            val firstName = nameParts.firstOrNull() ?: ""
                            val lastName = nameParts.drop(1).joinToString(" ")
                            
                            val userInfo = UserInfo(
                                id = validationResult.getString("sub") ?: account.id ?: "",
                                email = validationResult.getString("email") ?: account.email ?: "",
                                name = fullName,
                                firstName = firstName,
                                lastName = lastName,
                                picture = validationResult.getString("picture") ?: account.photoUrl?.toString() ?: "",
                                verifiedEmail = validationResult.getBoolean("email_verified")
                            )
                            
                            return@withContext AuthResult.Success(userInfo)
                        } else {
                            Log.e(TAG, "ID token validation failed")
                            return@withContext AuthResult.Error("ID token validation failed")
                        }
                    } else {
                        Log.e(TAG, "No ID token received from Google Sign-In")
                        return@withContext AuthResult.Error("No ID token received")
                    }
                } else {
                    Log.e(TAG, "Google Sign-In failed: No account returned")
                    return@withContext AuthResult.Error("No account returned from Google Sign-In")
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign-In failed with API exception", e)
                val errorMessage = when (e.statusCode) {
                    12501 -> "Sign-in was cancelled by user"
                    7 -> "Network error during sign-in"
                    10 -> "Developer error - check configuration"
                    else -> "Sign-in failed with error code: ${e.statusCode}"
                }
                return@withContext AuthResult.Error(errorMessage)
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during Google Sign-In", e)
                return@withContext AuthResult.Error("Unexpected error: ${e.message}")
            }
        }
    }

    /**
     * Validate ID token with Google's OIDC tokeninfo endpoint
     */
    private suspend fun validateIdToken(idToken: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$GOOGLE_TOKEN_INFO_URL?id_token=$idToken")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val tokenInfo = JSONObject(responseBody)
                        Log.d(TAG, "Token validation successful: ${tokenInfo.getString("email")}")
                        return@withContext tokenInfo
                    }
                } else {
                    Log.e(TAG, "Token validation failed: ${response.code} - ${response.message}")
                }
                return@withContext null
            } catch (e: Exception) {
                Log.e(TAG, "Error validating ID token", e)
                return@withContext null
            }
        }
    }

    /**
     * Check if user is currently signed in
     */
    fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }

    /**
     * Get current signed-in account
     */
    fun getCurrentAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    /**
     * Sign out the user
     */
    suspend fun signOut(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                googleSignInClient.signOut()
                Log.d(TAG, "Google Sign-In sign out successful")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Error during Google Sign-In sign out", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Revoke access and sign out completely
     */
    suspend fun revokeAccess(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                googleSignInClient.revokeAccess()
                Log.d(TAG, "Google Sign-In access revoked successfully")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Error during Google Sign-In access revocation", e)
                Result.failure(e)
            }
        }
    }
}

/**
 * Data classes for Google Sign-In responses
 */
data class UserInfo(
    val id: String,
    val email: String,
    val name: String,
    val firstName: String,
    val lastName: String,
    val picture: String,
    val verifiedEmail: Boolean
)

sealed class AuthResult {
    data class Success(val user: UserInfo) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

