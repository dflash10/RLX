package com.example.googleoidcdemo.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object UserDetails : Screen("user_details")
}

