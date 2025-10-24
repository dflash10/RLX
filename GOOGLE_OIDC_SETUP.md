# Google OIDC Setup Guide (No Firebase)

This guide will help you set up Google OIDC authentication directly through Google Cloud Console without using Firebase.

## Prerequisites

- Google Cloud Console access
- Your app's package name: `com.example.googleoidcdemo`
- Your SHA-1 fingerprint: `25:DD:90:BA:F8:74:8F:6B:4D:FB:50:8B:15:92:AB:77:BE:21:1E:4B`

## Step 1: Create or Select Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing project
3. Note your project ID

## Step 2: Enable Google Sign-In API

1. In Google Cloud Console, go to **APIs & Services** → **Library**
2. Search for "Google Sign-In API"
3. Click on it and press **Enable**

## Step 3: Configure OAuth Consent Screen

1. Go to **APIs & Services** → **OAuth consent screen**
2. Choose **External** user type (unless you have Google Workspace)
3. Fill in required fields:
   - **App name**: RLX
   - **User support email**: Your email
   - **Developer contact information**: Your email
4. Add scopes:
   - `openid`
   - `email`
   - `profile`
5. Add test users (your email) if in testing mode
6. Save and continue

## Step 4: Create OAuth 2.0 Client ID

1. Go to **APIs & Services** → **Credentials**
2. Click **+ CREATE CREDENTIALS** → **OAuth 2.0 Client ID**
3. Choose **Android** as application type
4. Fill in:
   - **Name**: RLX Android
   - **Package name**: `com.example.googleoidcdemo`
   - **SHA-1 certificate fingerprint**: `25:DD:90:BA:F8:74:8F:6B:4D:FB:50:8B:15:92:AB:77:BE:21:1E:4B`
5. Click **Create**

## Step 5: Create Web Application Client (for ID Token)

1. In the same **Credentials** page, click **+ CREATE CREDENTIALS** → **OAuth 2.0 Client ID**
2. Choose **Web application** as application type
3. Fill in:
   - **Name**: RLX Web Client
   - **Authorized redirect URIs**: 
     - `http://localhost:8080/callback` (for testing)
     - `https://yourdomain.com/callback` (for production)
4. Click **Create**

## Step 6: Update Your App Configuration

Update your `app/src/main/res/values/strings.xml` with the correct client IDs:

```xml
<string name="server_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
<string name="android_client_id">YOUR_ANDROID_CLIENT_ID_HERE</string>
```

## Step 7: Test the Configuration

1. Build and run your app
2. Try the Google Sign-In button
3. Check logs for any errors

## Troubleshooting

### Common Issues:

1. **"Developer error - check configuration"**
   - Verify SHA-1 fingerprint matches exactly
   - Ensure package name is correct
   - Check that OAuth consent screen is configured

2. **"Sign-in failed with error code: 10"**
   - Verify the server client ID is correct
   - Ensure Google Sign-In API is enabled

3. **"ID token validation failed"**
   - Check that the web client ID is correct
   - Verify OAuth consent screen has required scopes

### Debug Steps:

1. Check Android logs: `adb logcat | grep OidcAuthManager`
2. Verify client IDs in Google Cloud Console
3. Test with different Google accounts
4. Check OAuth consent screen status

## Security Notes

- Keep your client secret secure (web client)
- Use HTTPS in production
- Regularly rotate credentials
- Monitor usage in Google Cloud Console

## Production Considerations

1. **OAuth Consent Screen**: Submit for verification if needed
2. **Domain Verification**: Add your production domain
3. **Rate Limiting**: Monitor API usage
4. **Error Handling**: Implement proper error handling
5. **Token Refresh**: Implement token refresh logic

## Support

If you encounter issues:
1. Check Google Cloud Console logs
2. Review Android logs
3. Verify all configuration steps
4. Test with a fresh Google account
