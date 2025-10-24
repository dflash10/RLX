package com.example.googleoidcdemo.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.googleoidcdemo.R
import com.example.googleoidcdemo.auth.SessionManager
import com.example.googleoidcdemo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    sessionManager: SessionManager?,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    
    // Get user session info
    val userSession = remember { sessionManager?.getUserInfo() }
    
    // State for form fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    
    // Validation states
    var firstNameError by remember { mutableStateOf("") }
    var lastNameError by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var preferences by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }
    
    // Validation functions
    fun validateFirstName(input: String): String {
        return when {
            input.isEmpty() -> ""
            input.length < 2 -> "First name must be at least 2 characters"
            input.length > 50 -> "First name must be less than 50 characters"
            !input.matches(Regex("^[a-zA-Z\\s]+$")) -> "First name can only contain letters and spaces"
            else -> ""
        }
    }
    
    fun validateLastName(input: String): String {
        return when {
            input.isEmpty() -> ""
            input.length < 2 -> "Last name must be at least 2 characters"
            input.length > 50 -> "Last name must be less than 50 characters"
            !input.matches(Regex("^[a-zA-Z\\s]+$")) -> "Last name can only contain letters and spaces"
            else -> ""
        }
    }
    
    fun validateAge(input: String): String {
        return when {
            input.isEmpty() -> ""
            !input.matches(Regex("^\\d+$")) -> "Age must be a number"
            else -> {
                val ageValue = input.toIntOrNull()
                when {
                    ageValue == null -> "Age must be a valid number"
                    ageValue < 1 -> "Age must be at least 1"
                    ageValue > 120 -> "Age must be less than 120"
                    else -> ""
                }
            }
        }
    }
    
    // Input filters
    fun filterNameInput(input: String): String {
        return input.filter { it.isLetter() || it.isWhitespace() }
    }
    
    fun filterAgeInput(input: String): String {
        return input.filter { it.isDigit() }
    }
    
    // Auto-fill names from session
    LaunchedEffect(sessionManager) {
        sessionManager?.getUserInfo()?.let { session ->
            firstName = session.firstName
            lastName = session.lastName
            email = session.email ?: ""
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCFE8D5), // Light green
                        Color(0xFFF2EBD9)  // Beige
                    )
                )
            )
    ) {
        // Drawer state - shared between profile icon and drawer
        var showDrawer by remember { mutableStateOf(false) }
        
        // Fixed Profile Icon - Top Left (not scrollable)
            if (userSession != null) {
            // Profile Avatar Button - Fixed to background
            IconButton(
                onClick = { showDrawer = true },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                        if (userSession.picture.isNotEmpty()) {
                            AsyncImage(
                                model = userSession.picture,
                        contentDescription = "User Profile",
                                modifier = Modifier
                            .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_rlx_logo),
                                contentDescription = "Default Avatar",
                                modifier = Modifier
                            .size(48.dp)
                                    .clip(CircleShape)
                                    .background(RLXBrown),
                                contentScale = ContentScale.Crop
                            )
                        }
            }
        }
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 80.dp) // Add top padding to avoid overlap with profile icon
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Text(
                text = "Tell a bit about yourself",
                color = RLXText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "We'll personalize your experience based on your details.",
                color = RLXTextGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Form Fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RLXWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Name Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { 
                                val filtered = filterNameInput(it)
                                firstName = filtered
                                firstNameError = validateFirstName(filtered)
                            },
                            label = { Text("First Name *") },
                            modifier = Modifier.weight(1f),
                            isError = firstNameError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (firstNameError.isNotEmpty()) Color.Red else Color(0xFFFFB47D),
                                focusedLabelColor = if (firstNameError.isNotEmpty()) Color.Red else Color(0xFFFFB47D),
                                errorBorderColor = Color.Red,
                                errorLabelColor = Color.Red
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        // First Name Error Message
                        if (firstNameError.isNotEmpty()) {
                            Text(
                                text = firstNameError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                        
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { 
                                val filtered = filterNameInput(it)
                                lastName = filtered
                                lastNameError = validateLastName(filtered)
                            },
                            label = { Text("Last Name *") },
                            modifier = Modifier.weight(1f),
                            isError = lastNameError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (lastNameError.isNotEmpty()) Color.Red else Color(0xFFFFB47D),
                                focusedLabelColor = if (lastNameError.isNotEmpty()) Color.Red else Color(0xFFFFB47D),
                                errorBorderColor = Color.Red,
                                errorLabelColor = Color.Red
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        // Last Name Error Message
                        if (lastNameError.isNotEmpty()) {
                            Text(
                                text = lastNameError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                    
                    // Age
                    OutlinedTextField(
                        value = age,
                        onValueChange = { 
                            val filtered = filterAgeInput(it)
                            age = filtered
                            ageError = validateAge(filtered)
                        },
                        label = { Text("Age *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = ageError.isNotEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (ageError.isNotEmpty()) Color.Red else Color(0xFFFFB47D),
                            focusedLabelColor = if (ageError.isNotEmpty()) Color.Red else Color(0xFFFFB47D),
                            errorBorderColor = Color.Red,
                            errorLabelColor = Color.Red
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Age Error Message
                    if (ageError.isNotEmpty()) {
                        Text(
                            text = ageError,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                    
                    // Gender Dropdown
                    var genderExpanded by remember { mutableStateOf(false) }
                    val genderOptions = listOf("Male", "Female", "Other")
                    
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB47D),
                                focusedLabelColor = Color(0xFFFFB47D)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        gender = option
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Height Slider
                    var height by remember { mutableStateOf(170f) }
                    Text(
                        text = "Height: ${height.toInt()}cm",
                        color = RLXText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Slider(
                        value = height,
                        onValueChange = { height = it },
                        valueRange = 0f..250f,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF3C9A50),
                            activeTrackColor = Color(0xFF3C9A50)
                        )
                    )
                    
                    // Weight Slider
                    var weight by remember { mutableStateOf(70f) }
                    Text(
                        text = "Weight: ${weight.toInt()}kg",
                        color = RLXText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Slider(
                        value = weight,
                        onValueChange = { weight = it },
                        valueRange = 0f..200f,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF3C9A50),
                            activeTrackColor = Color(0xFF3C9A50)
                        )
                    )
                    
                    // Blood Group Dropdown
                    var bloodGroupExpanded by remember { mutableStateOf(false) }
                    val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                    var bloodGroup by remember { mutableStateOf("") }
                    
                    ExposedDropdownMenuBox(
                        expanded = bloodGroupExpanded,
                        onExpandedChange = { bloodGroupExpanded = !bloodGroupExpanded }
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Blood Group") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB47D),
                                focusedLabelColor = Color(0xFFFFB47D)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = bloodGroupExpanded,
                            onDismissRequest = { bloodGroupExpanded = false }
                        ) {
                            bloodGroupOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        bloodGroup = option
                                        bloodGroupExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Alcohol Dropdown
                    var alcoholExpanded by remember { mutableStateOf(false) }
                    val alcoholOptions = listOf("Yes", "No")
                    var alcohol by remember { mutableStateOf("") }
                    
                    ExposedDropdownMenuBox(
                        expanded = alcoholExpanded,
                        onExpandedChange = { alcoholExpanded = !alcoholExpanded }
                    ) {
                        OutlinedTextField(
                            value = alcohol,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Alcohol") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = alcoholExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB47D),
                                focusedLabelColor = Color(0xFFFFB47D)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = alcoholExpanded,
                            onDismissRequest = { alcoholExpanded = false }
                        ) {
                            alcoholOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        alcohol = option
                                        alcoholExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Smoking Dropdown
                    var smokingExpanded by remember { mutableStateOf(false) }
                    val smokingOptions = listOf("Yes", "No")
                    var smoking by remember { mutableStateOf("") }
                    
                    ExposedDropdownMenuBox(
                        expanded = smokingExpanded,
                        onExpandedChange = { smokingExpanded = !smokingExpanded }
                    ) {
                        OutlinedTextField(
                            value = smoking,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Smoking") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = smokingExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB47D),
                                focusedLabelColor = Color(0xFFFFB47D)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = smokingExpanded,
                            onDismissRequest = { smokingExpanded = false }
                        ) {
                            smokingOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        smoking = option
                                        smokingExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Next Button
            Button(
                onClick = {
                    // Handle form submission
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3C9A50)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Footer
            Text(
                text = "© Powered by Rachaita Labs",
                color = RLXTextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Slide-out Drawer from Left - Rendered on top of content
        if (userSession != null && showDrawer) {
            // Opaque backdrop and Drawer Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)) // More opaque backdrop
                    .clickable { showDrawer = false }
            ) {
                // Drawer Content with Animation
                val slideOffset by animateFloatAsState(
                    targetValue = if (showDrawer) 1f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "drawer_slide"
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp)
                        .offset(x = (-280 + slideOffset * 280).dp) // Start from -280dp, slide to 0dp
                        .align(Alignment.TopStart),
                    colors = CardDefaults.cardColors(containerColor = Color.White), // Solid white background
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp), // Higher elevation for more opacity
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(24.dp)
                    ) {
                        // Header with Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Profile",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = RLXText
                            )
                            
                            IconButton(
                                onClick = { showDrawer = false }
                            ) {
                                Text(
                                    text = "✕",
                                    fontSize = 18.sp,
                                    color = RLXTextGray
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // User Profile Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(start = 32.dp)
                        ) {
                            // Profile Picture
                            if (userSession?.picture?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = userSession.picture,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_rlx_logo),
                                    contentDescription = "Default Avatar",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(RLXBrown),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // User Name
                            Text(
                                text = userSession?.name ?: "",
                                color = RLXText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // User Email
                            if (userSession?.email != null) {
                                Text(
                                    text = userSession.email,
                                    color = RLXTextGray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            // User Phone
                            if (userSession?.phone != null) {
                                Text(
                                    text = userSession.phone,
                                    color = RLXTextGray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Logout Button
                        Button(
                            onClick = {
                                onLogoutClick()
                                showDrawer = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RLXOrange,
                                contentColor = RLXWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Logout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}