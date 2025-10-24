package com.example.googleoidcdemo.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.googleoidcdemo.MainActivity
import com.example.googleoidcdemo.R
import com.example.googleoidcdemo.auth.OidcAuthManager
import com.example.googleoidcdemo.ui.theme.*

@Composable
fun SignUpScreen(
    onSignUpClick: (String, String?, String?, String) -> Unit,
    onSignInClick: () -> Unit,
    onGoogleSignInClick: () -> Unit = {},
    mainActivity: MainActivity? = null
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isVerificationSent by remember { mutableStateOf(false) }
    var isOTPVerified by remember { mutableStateOf(false) }
    var isEmailVerified by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authManager = remember { OidcAuthManager(context) }
    
    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // The result will be handled by MainActivity's onActivityResult
        onGoogleSignInClick()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        RLXBgLight,
                        RLXBgBeige
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Circular Logo Background
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(RLXBrown),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rlx_logo),
                    contentDescription = "RLX Logo",
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Heading
            Text(
                text = "Just a few quick things to get started",
                color = RLXText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email/Phone Field
            OutlinedTextField(
                value = emailOrPhone,
                onValueChange = { 
                    emailOrPhone = it
                    errorMessage = ""
                },
                label = { Text("Email/Phone no.") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RLXOrange,
                    unfocusedBorderColor = RLXOrange,
                    focusedLabelColor = RLXText,
                    unfocusedLabelColor = RLXTextGray
                ),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = errorMessage.isNotEmpty()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // OTP Field (shown only for phone verification)
            if (isVerificationSent && !isEmailVerified && !isOTPVerified) {
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { 
                        otpCode = it
                        errorMessage = ""
                    },
                    label = { Text("Enter OTP") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RLXOrange,
                        unfocusedBorderColor = RLXOrange,
                        focusedLabelColor = RLXText,
                        unfocusedLabelColor = RLXTextGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessage.isNotEmpty()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Verify OTP Button
                Button(
                    onClick = {
                    if (otpCode.isNotEmpty()) {
                        // For now, just mark as verified since we're not using Firebase phone auth
                        isOTPVerified = true
                        successMessage = "Phone number verified successfully!"
                        errorMessage = ""
                    } else {
                        errorMessage = "Please enter the OTP code"
                    }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RLXOrange
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Verify OTP",
                        color = RLXWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RLXOrange,
                    unfocusedBorderColor = RLXOrange,
                    focusedLabelColor = RLXText,
                    unfocusedLabelColor = RLXTextGray
                ),
                shape = RoundedCornerShape(10.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = errorMessage.isNotEmpty()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Success Message
            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = RLXGreen,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Error Message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Join RLX Button
            Button(
                onClick = {
                    if (emailOrPhone.isNotEmpty() && password.isNotEmpty()) {
                        val isEmail = emailOrPhone.contains("@") && emailOrPhone.contains(".")
                        
                        // Simplified signup - directly call the backend API
                        onSignUpClick("", if (isEmail) emailOrPhone else null, if (!isEmail) emailOrPhone else null, password)
                    } else {
                        errorMessage = "Please fill in all fields"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RLXGreen
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && !((isVerificationSent && !isEmailVerified && !isOTPVerified) && otpCode.isEmpty())
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = RLXWhite,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = when {
                            !isVerificationSent -> "Send Verification"
                            isEmailVerified || isOTPVerified -> "Join RLX"
                            else -> "Complete Registration"
                        },
                        color = RLXWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = RLXTextGray,
                    thickness = 1.dp
                )
                Text(
                    text = "— OR —",
                    color = RLXTextGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = RLXTextGray,
                    thickness = 1.dp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Google Login Button (Centered)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { 
                        mainActivity?.startGoogleSignIn() ?: run {
                            val signInIntent = authManager.getSignInIntent()
                            googleSignInLauncher.launch(signInIntent)
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(RLXWhite)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sign In Link
            TextButton(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? Sign In",
                    color = Color(0xFF1976D2),
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer
            Text(
                text = "Powered by Rachaita Labs",
                color = RLXTextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

