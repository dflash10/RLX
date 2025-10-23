package com.example.googleoidcdemo.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.googleoidcdemo.MainActivity
import com.example.googleoidcdemo.auth.SessionManager
import com.example.googleoidcdemo.ui.screens.LoginScreen
import com.example.googleoidcdemo.ui.screens.SignUpScreen
import com.example.googleoidcdemo.ui.screens.UserDetailsScreen
import com.example.googleoidcdemo.ui.screens.WelcomeScreen
import kotlinx.coroutines.launch

@Composable
fun RHealthNavigation(navController: NavHostController, context: Context? = null, mainActivity: MainActivity? = null) {
    val sessionManager = remember { context?.let { SessionManager(it) } }
    var isLoggedIn by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Check authentication status on startup and listen for changes
    LaunchedEffect(sessionManager) {
        sessionManager?.let { manager ->
            try {
                val loggedIn = manager.isLoggedIn()
                if (loggedIn) {
                    val isValid = manager.validateSession()
                    isLoggedIn = isValid
                    if (!isValid) {
                        manager.clearSession()
                    }
                } else {
                    // User is not logged in
                    isLoggedIn = false
                }
            } catch (e: Exception) {
                manager.clearSession()
                isLoggedIn = false
            }
        } ?: run {
            // No session manager available
            isLoggedIn = false
        }
        isLoading = false
    }
    
    // Listen for session changes during app lifecycle
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000) // Check every second
            sessionManager?.let { manager ->
                val currentLoggedIn = manager.isLoggedIn()
                if (currentLoggedIn != isLoggedIn) {
                    isLoggedIn = currentLoggedIn
                    if (currentLoggedIn) {
                        // Navigate to UserDetails screen when user becomes logged in
                        navController.navigate(Screen.UserDetails.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    // Show loading screen while checking authentication
    if (isLoading) {
        return
    }

    val startDestination = if (isLoggedIn) Screen.UserDetails.route else Screen.Welcome.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onStartNowClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = { name, email, phone, password ->
                    mainActivity?.handleEmailPhoneRegistration(
                        name, email, phone, password,
                        onSuccess = {
                            isLoggedIn = true
                            navController.navigate(Screen.UserDetails.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        },
                        onError = { error ->
                            // Error handling will be done in the UI
                        }
                    )
                },
                onSignInClick = {
                    navController.navigate(Screen.Login.route)
                },
                onGoogleSignInClick = {
                    // Handle Google sign-in success
                    isLoggedIn = true
                    navController.navigate(Screen.UserDetails.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                mainActivity = mainActivity
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { identifier, password ->
                    mainActivity?.handleEmailPhoneLogin(
                        identifier, password,
                        onSuccess = {
                            isLoggedIn = true
                            navController.navigate(Screen.UserDetails.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        },
                        onError = { error ->
                            // Error handling will be done in the UI
                        }
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
        
        
        composable(Screen.UserDetails.route) {
            UserDetailsScreen(
                sessionManager = sessionManager,
                onLogoutClick = {
                    coroutineScope.launch {
                        sessionManager?.logout()
                        isLoggedIn = false
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

