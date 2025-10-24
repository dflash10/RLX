package com.example.googleoidcdemo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.googleoidcdemo.R
import com.example.googleoidcdemo.ui.theme.RLXBrown
import com.example.googleoidcdemo.ui.theme.RLXWhite
import com.example.googleoidcdemo.utils.StringResource
import com.example.googleoidcdemo.utils.getStringResource

/**
 * WelcomeScreen with i18n support
 * This is an example of how to use the internationalization system
 */
@Composable
fun WelcomeScreenI18n(
    onStartNowClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B4513),
                        Color(0xFF6B4226)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_rlx_logo),
                contentDescription = getStringResource(StringResource.WELCOME_LOGO_DESCRIPTION),
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Welcome Text
            Text(
                text = getStringResource(StringResource.WELCOME_TITLE),
                color = RLXWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Start Now Button
            Button(
                onClick = onStartNowClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RLXBrown
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = getStringResource(StringResource.WELCOME_START_BUTTON),
                    color = RLXWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
