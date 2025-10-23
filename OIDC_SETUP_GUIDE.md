# Google OIDC Integration Setup Guide

This guide will help you set up Google OpenID Connect (OIDC) authentication in your Android application.

## 🚀 Quick Start

### 1. Google Cloud Console Setup

#### Step 1: Create a Google Cloud Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Note your project ID (booming-bonito-458311-c6)

#### Step 2: Enable OAuth Consent Screen
1. Navigate to **APIs & Services** → **OAuth consent screen**
2. Choose **External** (for public apps) or **Internal** (for organization-only apps)
3. Fill in the required information:
   - **App name**: Your app name
   - **Support email**: Your email address
   - **Authorized domains**: Add your domain (if any)
   - **Scopes**: Add `openid`, `email`, `profile`
   - **Test users**: Add test users if your app isn't verified yet
4. Save and continue through all steps

#### Step 3: Create OAuth Client ID
1. Go to **APIs & Services** → **Credentials**
2. Click **Create Credentials** → **OAuth Client ID**
3. Select **Web application**
4. Add authorized redirect URIs:
   ```
   com.example.googleoidcdemo://oauth/callback
   ```
5. Click **Create**
6. Copy your **Client ID** and **Client Secret**

### 2. Android Configuration

#### Step 1: Update Configuration
Edit `app/src/main/res/values/strings.xml`:

```xml
<string name="server_client_id">YOUR_ACTUAL_CLIENT_ID.apps.googleusercontent.com</string>
<string name="client_secret">YOUR_ACTUAL_CLIENT_SECRET</string>
```

Replace `YOUR_ACTUAL_CLIENT_ID` and `YOUR_ACTUAL_CLIENT_SECRET` with the values from Google Cloud Console.

#### Step 2: Update Package Name (Optional)
If you want to use a different package name:
1. Update the package name in `app/build.gradle`:
   ```gradle
   defaultConfig {
       applicationId "com.yourcompany.yourapp"
       // ... other config
   }
   ```

2. Update the redirect URI in `OidcAuthManager.kt`:
   ```kotlin
   val redirectUri = URLEncoder.encode("com.yourcompany.yourapp://oauth/callback", "UTF-8")
   ```

3. Update the intent filter in `AndroidManifest.xml`:
   ```xml
   <data android:scheme="com.yourcompany.yourapp" />
   ```

4. Update the redirect URI in Google Cloud Console to match

### 3. Build and Test

1. **Sync Project**: Click "Sync Now" in Android Studio
2. **Build Project**: Build → Make Project
3. **Run on Device**: Connect a device and run the app
4. **Test Authentication**: Tap "Sign in with Google" and complete the flow

## 🔧 Architecture Overview

### OIDC Flow Implementation

The app implements the **Authorization Code Flow with PKCE** (Proof Key for Code Exchange):

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Android App   │    │   Web Browser   │    │  Google OAuth   │
│                 │    │                 │    │     Server      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │ 1. User taps "Sign In"│                       │
         ├──────────────────────→│                       │
         │                       │ 2. Redirect to Google │
         │                       ├──────────────────────→│
         │                       │                       │
         │                       │ 3. User signs in      │
         │                       │    & grants consent   │
         │                       │←──────────────────────┤
         │                       │                       │
         │ 4. Authorization code │                       │
         │    returned via       │                       │
         │    deep link          │                       │
         │←──────────────────────┤                       │
         │                       │                       │
         │ 5. Exchange code for  │                       │
         │    access & ID tokens │                       │
         ├──────────────────────→│                       │
         │                       │                       │
         │ 6. Validate ID token  │                       │
         │                       │                       │
         │ 7. Fetch user info    │                       │
         ├──────────────────────→│                       │
         │                       │                       │
         │ 8. Display profile    │                       │
         │                       │                       │
```

**Flow Steps:**
1. **Authorization Request**: User taps sign-in button
2. **Browser Redirect**: Opens Google's authorization page
3. **User Consent**: User signs in and grants permissions
4. **Authorization Code**: Google redirects back with authorization code
5. **Token Exchange**: App exchanges code for access token and ID token
6. **Token Validation**: Validates the ID token
7. **User Info**: Fetches user information
8. **Profile Display**: Shows user profile

### Key Components

- **`OidcAuthManager`**: Handles the entire OIDC flow
- **`MainActivity`**: Entry point with sign-in button
- **`ProfileActivity`**: Displays user information after authentication
- **`ErrorActivity`**: Shows error messages

### Security Features

- **PKCE**: Prevents authorization code interception attacks
- **State Parameter**: Prevents CSRF attacks
- **Nonce**: Prevents replay attacks
- **HTTPS Only**: All network communication uses HTTPS
- **Token Validation**: Validates ID tokens before trusting them

## 📱 User Experience

### Authentication Flow
1. User opens the app
2. Taps "Sign in with Google (OIDC)"
3. Browser opens to Google's sign-in page
4. User signs in and grants permissions
5. Browser redirects back to the app
6. App shows user profile with information

### Error Handling
- Network errors are caught and displayed
- Invalid tokens are rejected
- State parameter mismatches are detected
- User-friendly error messages are shown

## 🔒 Security Considerations

### Production Deployment
For production use, consider these additional security measures:

1. **Token Validation**: Implement full JWT signature validation using Google's public keys
2. **Certificate Pinning**: Pin Google's SSL certificates
3. **Token Storage**: Store tokens securely using Android Keystore
4. **Biometric Authentication**: Add biometric authentication for sensitive operations
5. **Rate Limiting**: Implement rate limiting for authentication attempts

### Code Security
- Never commit client secrets to version control
- Use environment variables or secure configuration management
- Implement proper logging without exposing sensitive data
- Regularly update dependencies for security patches

## 🐛 Troubleshooting

### Common Issues

#### "Invalid redirect URI" Error
- Ensure the redirect URI in Google Cloud Console exactly matches the one in your code
- Check that the URI scheme is properly configured in AndroidManifest.xml

#### "Client ID not found" Error
- Verify the client ID is correctly set in strings.xml
- Ensure the client ID is for a web application type (not Android)

#### "State mismatch" Error
- This usually indicates a timing issue or multiple authentication attempts
- Clear app data and try again

#### Network Security Issues
- Ensure your app has INTERNET permission
- Check that the network security config allows HTTPS to Google domains
- Verify that your device/emulator has internet connectivity

### Debug Tips

1. **Enable Logging**: Check Logcat for detailed error messages
2. **Test on Real Device**: Some authentication flows don't work properly on emulators
3. **Check Network**: Ensure your device can reach Google's servers
4. **Verify Configuration**: Double-check all configuration values

## 📚 Additional Resources

- [Google OAuth 2.0 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [OpenID Connect Specification](https://openid.net/connect/)
- [PKCE RFC 7636](https://tools.ietf.org/html/rfc7636)
- [Android Network Security Config](https://developer.android.com/training/articles/security-config)

## 🤝 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review the Logcat output for error messages
3. Verify your Google Cloud Console configuration
4. Test with a simple web application first to ensure your OAuth setup is correct
