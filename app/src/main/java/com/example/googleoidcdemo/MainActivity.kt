package com.example.googleoidcdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.googleoidcdemo.auth.OidcAuthManager
import com.example.googleoidcdemo.auth.SessionManager
import com.example.googleoidcdemo.api.ApiService
import com.example.googleoidcdemo.navigation.RLXNavigation
import com.example.googleoidcdemo.ui.theme.RLXTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var sessionManager: SessionManager
    private val apiService = ApiService()
    private lateinit var authManager: OidcAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate started")
        
        // Initialize session manager and auth manager
        sessionManager = SessionManager(this)
        authManager = OidcAuthManager(this)
        
        // Check for existing session
        checkExistingSession()
        
        try {
            setContent {
                RLXTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RLXApp(this@MainActivity, this@MainActivity)
                    }
                }
            }
            Log.d(TAG, "MainActivity setContent completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in MainActivity onCreate", e)
            // Fallback to simple content if there's an error
            setContent {
                RLXTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ErrorFallbackScreen()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data)
        }
    }

    private fun checkExistingSession() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (sessionManager.isLoggedIn()) {
                    Log.d(TAG, "Found existing session, validating...")
                    val isValid = sessionManager.validateSession()
                    if (isValid) {
                        Log.d(TAG, "Session is valid, user is already logged in")
                        // User is already logged in, navigate to main app
                        // This will be handled by the navigation system
                    } else {
                        Log.d(TAG, "Session is invalid, clearing...")
                        sessionManager.clearSession()
                    }
                } else {
                    Log.d(TAG, "No existing session found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking existing session", e)
                sessionManager.clearSession()
            }
        }
    }

    fun startGoogleSignIn() {
        try {
            Log.d(TAG, "Starting Google Sign-In process")
            val signInIntent = authManager.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Google Sign-In", e)
        }
    }

    fun handleEmailPhoneLogin(identifier: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val request = ApiService.LoginRequest(identifier, password)
                val response = apiService.login(request)
                
                if (response.success && response.data != null) {
                    sessionManager.saveSession(response.data!!)
                    onSuccess()
                } else {
                    onError(response.message ?: "Login failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Email/Phone login error", e)
                onError("Network error: ${e.message}")
            }
        }
    }

    fun handleEmailPhoneRegistration(name: String, email: String?, phone: String?, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val request = ApiService.RegistrationRequest(name, email, phone, password)
                val response = apiService.register(request)
                
                if (response.success && response.data != null) {
                    sessionManager.saveSession(response.data!!)
                    onSuccess()
                } else {
                    onError(response.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Email/Phone registration error", e)
                onError("Network error: ${e.message}")
            }
        }
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        Log.d(TAG, "Handling Google Sign-In result")
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = authManager.handleSignInResult(data)
                
                when (result) {
                    is com.example.googleoidcdemo.auth.AuthResult.Success -> {
                        Log.d(TAG, "Google Sign-In successful: ${result.user.name}")
                        
                        // Create AuthResponse for backend compatibility
                        val authResponse = ApiService.AuthResponse(
                            user = ApiService.UserData(
                                id = result.user.id,
                                email = result.user.email,
                                phone = null,
                                name = result.user.name,
                                firstName = result.user.firstName,
                                lastName = result.user.lastName,
                                picture = result.user.picture,
                                verifiedEmail = result.user.verifiedEmail,
                                verifiedPhone = false,
                                lastLogin = System.currentTimeMillis().toString(),
                                loginCount = 1,
                                preferences = ApiService.UserPreferences(
                                    theme = "auto",
                                    notifications = true
                                )
                            ),
                            tokens = ApiService.TokenData(
                                accessToken = "native_google_token", // Placeholder for native flow
                                refreshToken = "native_google_refresh", // Placeholder for native flow
                                expiresIn = 3600 // 1 hour
                            )
                        )
                        
                        // Save session to local storage
                        sessionManager.saveSession(authResponse)
                        
                        Log.d(TAG, "User session saved, authentication complete")
                        // Trigger navigation to UserDetails screen
                        // The navigation system will detect the session change and navigate appropriately
                    }
                    is com.example.googleoidcdemo.auth.AuthResult.Error -> {
                        Log.e(TAG, "Google Sign-In failed: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling Google Sign-In result", e)
            }
        }
    }
}

@Composable
fun RLXApp(context: Context, mainActivity: MainActivity) {
    val navController = rememberNavController()
    RLXNavigation(navController = navController, context = context, mainActivity = mainActivity)
}

@Composable
fun ErrorFallbackScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "RLX App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "App is running successfully!",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
