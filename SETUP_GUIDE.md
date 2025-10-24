# RLX Complete Setup Guide

This guide will help you set up the complete RLX application with Google OIDC authentication, MongoDB Atlas integration, and persistent user sessions.

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Android App   │    │   Node.js API   │    │  MongoDB Atlas  │
│                 │    │   (Port 8080)   │    │     Cloud       │
│ - Google OIDC   │◄──►│ - Authentication│◄──►│ - User Storage  │
│ - Session Mgmt  │    │ - JWT Tokens    │    │ - Sessions      │
│ - Remember Me   │    │ - User Profile  │    │ - Preferences   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📋 Prerequisites

- Android Studio (latest version)
- Node.js (v16 or higher)
- MongoDB Atlas Account
- Google Cloud Console Account

## 🚀 Quick Start

### 1. Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the server**
   ```bash
   npm run dev
   ```

   The server will start on `http://localhost:8080`

### 2. Android App Setup

1. **Open Android Studio**
2. **Open the project** (root directory)
3. **Sync Gradle** (if prompted)
4. **Run the app** on emulator or device

## 🔧 Detailed Configuration

### Backend Configuration

The backend is pre-configured with your credentials:

- **MongoDB Atlas**: `mongodb+srv://bcdhanush10_db_user:L6G1mHEXaJAdoryh@cluster0.tqqrbdf.mongodb.net/...`
- **Google OAuth**: Client ID and Secret already configured
- **JWT**: Secure token management
- **Port**: 8080

### Android Configuration

The Android app is configured to:

- **API Base URL**: `http://10.0.2.2:8080/api` (emulator) or your computer's IP
- **OAuth Redirect**: `http://localhost:8080/oauth/callback`
- **Google OAuth**: Your provided Android client ID

## 🔐 Authentication Flow

### 1. User Login Process

1. User taps "Google Sign In" button
2. App opens browser with Google OAuth
3. User signs in and grants permissions
4. Google redirects to backend callback
5. Backend exchanges code for tokens
6. Backend creates/updates user in MongoDB
7. Backend returns JWT tokens to app
8. App stores tokens locally for future use

### 2. Remember Me Functionality

1. App checks for existing session on startup
2. If valid session exists, user is automatically logged in
3. If session is expired, app attempts to refresh tokens
4. If refresh fails, user is logged out

### 3. Session Management

- **Access Tokens**: Short-lived (30 days)
- **Refresh Tokens**: Long-lived (90 days)
- **Local Storage**: Encrypted SharedPreferences
- **Multi-device**: Support for multiple devices per user

## 📱 Features Implemented

### ✅ Completed Features

- [x] Google OIDC Authentication
- [x] MongoDB Atlas Integration
- [x] User Session Persistence
- [x] Remember Me Functionality
- [x] JWT Token Management
- [x] User Profile Display
- [x] Logout Functionality
- [x] Multi-device Support
- [x] Secure Token Storage
- [x] Automatic Token Refresh

### 🎯 User Experience

1. **First Time Users**:
   - See welcome screen
   - Tap "Start Now" → Sign Up screen
   - Tap Google button → OAuth flow
   - Complete authentication → User details screen

2. **Returning Users**:
   - App automatically checks for existing session
   - If valid session exists → Direct to user details screen
   - If no session → Welcome screen

3. **Session Management**:
   - Tokens are automatically refreshed when needed
   - Users stay logged in across app restarts
   - Logout clears all session data

## 🛠️ Development

### Backend Development

```bash
# Start development server with auto-restart
npm run dev

# View logs
tail -f logs/app.log

# Test endpoints
curl http://localhost:8080/health
```

### Android Development

1. **Build and run** in Android Studio
2. **Check logs** in Logcat for debugging
3. **Test on emulator** first, then physical device

### API Testing

```bash
# Health check
curl http://localhost:8080/health

# Test authentication (after OAuth flow)
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/auth/profile
```

## 🔍 Troubleshooting

### Common Issues

1. **Backend won't start**
   - Check if port 8080 is available
   - Verify Node.js version (v16+)
   - Check MongoDB Atlas connection

2. **Android app can't connect to backend**
   - For emulator: Use `10.0.2.2:8080`
   - For physical device: Use your computer's IP address
   - Check if backend is running

3. **Google OAuth not working**
   - Verify redirect URI in Google Console
   - Check client ID configuration
   - Ensure OAuth consent screen is configured

4. **Session not persisting**
   - Check SharedPreferences permissions
   - Verify token expiration settings
   - Check network connectivity

### Debug Mode

Enable debug logging in Android:

```kotlin
// In MainActivity.kt
Log.d(TAG, "Debug message")
```

Check backend logs in terminal for detailed information.

## 📊 Database Schema

### Users Collection

```javascript
{
  _id: ObjectId,
  googleId: String,
  email: String,
  name: String,
  picture: String,
  verifiedEmail: Boolean,
  refreshTokens: [{
    token: String,
    createdAt: Date,
    deviceInfo: String
  }],
  lastLogin: Date,
  loginCount: Number,
  isActive: Boolean,
  preferences: {
    theme: String,
    notifications: Boolean
  },
  createdAt: Date,
  updatedAt: Date
}
```

## 🔒 Security Features

- **HTTPS**: All API communication (in production)
- **JWT Tokens**: Secure authentication
- **Rate Limiting**: Prevent abuse
- **CORS**: Configured for mobile app
- **Input Validation**: All inputs validated
- **Token Refresh**: Automatic token renewal
- **Session Cleanup**: Expired tokens removed

## 🚀 Production Deployment

### Backend Deployment

1. **Environment Variables**: Replace config.js with env vars
2. **HTTPS**: Enable SSL certificates
3. **Database**: Use production MongoDB cluster
4. **Monitoring**: Add logging and monitoring
5. **Scaling**: Consider load balancing

### Android App Deployment

1. **Sign APK**: Generate signed release APK
2. **Google Play**: Upload to Play Store
3. **Testing**: Test on various devices
4. **Updates**: Plan for future updates

## 📞 Support

For issues or questions:

1. Check this guide first
2. Review logs for error messages
3. Test individual components
4. Contact development team

## 🎉 Success!

Once everything is set up, you should have:

- ✅ A working Node.js backend with MongoDB
- ✅ An Android app with Google OIDC
- ✅ Persistent user sessions
- ✅ Remember me functionality
- ✅ Secure authentication flow

The app will remember users across sessions and provide a seamless authentication experience!
