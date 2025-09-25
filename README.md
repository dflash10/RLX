# Android Google OIDC Demo

A simple Android application demonstrating Google OpenID Connect (OIDC) authentication integration using Google Sign-In SDK.

## 📱 Overview

This project showcases how to integrate Google OIDC authentication in an Android application. It provides a foundation for implementing secure user authentication using Google's OAuth 2.0 and OpenID Connect protocols.

## ✨ Features

- **Google OIDC Integration**: Seamless Google Sign-In with OIDC support
- **Modern UI**: Clean Material Design 3 interface
- **Error Handling**: Dedicated error screen for authentication failures
- **Kotlin**: Built with modern Kotlin language
- **View Binding**: Type-safe view references

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/googleoidcdemo/
│   │   ├── MainActivity.kt          # Main activity with Google Sign-In button
│   │   └── ErrorActivity.kt         # Error handling activity
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml    # Main activity layout
│   │   │   └── activity_error.xml   # Error activity layout
│   │   └── values/
│   │       └── strings.xml          # String resources
│   └── AndroidManifest.xml          # App configuration
├── build.gradle                     # App-level dependencies
└── proguard-rules.pro              # ProGuard configuration
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 21 (Android 5.0) or higher
- Google Cloud Console project with OAuth 2.0 credentials

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/dflash10/android-google-oidc-demo.git
   cd android-google-oidc-demo
   ```

2. **Configure Google OAuth 2.0**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select an existing one
   - Enable the Google Sign-In API
   - Create OAuth 2.0 credentials:
     - Application type: Android
     - Package name: `com.example.googleoidcdemo`
     - SHA-1 certificate fingerprint: (get from your debug keystore)

3. **Update Configuration**
   - Open `app/src/main/res/values/strings.xml`
   - Replace `YOUR_SERVER_CLIENT_ID` with your actual server client ID:
     ```xml
     <string name="server_client_id">YOUR_ACTUAL_CLIENT_ID.apps.googleusercontent.com</string>
     ```

4. **Build and Run**
   - Open the project in Android Studio
   - Sync the project with Gradle files
   - Run the app on an emulator or physical device

## 🔧 Configuration

### Google Sign-In Setup

The app uses Google Sign-In SDK with the following configuration:

```kotlin
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestEmail()
    .requestIdToken(getString(R.string.server_client_id))
    .build()
```

### Dependencies

Key dependencies used in this project:

- **Google Play Services Auth**: `com.google.android.gms:play-services-auth:21.2.0`
- **AndroidX Core**: `androidx.core:core-ktx:1.13.1`
- **Material Design**: `com.google.android.material:material:1.12.0`
- **Kotlin**: `org.jetbrains.kotlin:kotlin-stdlib:1.9.24`

## 📱 Screenshots

### Main Screen
- Clean interface with Google Sign-In button
- Material Design 3 styling

### Error Screen
- User-friendly error messages
- Easy navigation back to main screen

## 🔐 Security Considerations

- **Client ID**: Store your server client ID securely
- **Certificate Fingerprints**: Ensure your SHA-1 fingerprints are correctly configured
- **ProGuard**: Enable code obfuscation for release builds
- **HTTPS**: Always use HTTPS for production environments

## 🛠️ Development

### Building the Project

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📋 TODO / Future Enhancements

- [ ] Implement actual Google Sign-In flow
- [ ] Add user profile display after successful authentication
- [ ] Implement token refresh mechanism
- [ ] Add logout functionality
- [ ] Implement proper error handling with specific error messages
- [ ] Add unit tests
- [ ] Add CI/CD pipeline

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/dflash10/android-google-oidc-demo/issues) page
2. Create a new issue with detailed information
3. Provide steps to reproduce the problem

## 🔗 Useful Links

- [Google Sign-In for Android Documentation](https://developers.google.com/identity/sign-in/android)
- [OpenID Connect Documentation](https://openid.net/connect/)
- [Android Developer Guide](https://developer.android.com/guide)
- [Material Design Guidelines](https://material.io/design)

## 📊 Project Stats

- **Language**: Kotlin
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle
- **Architecture**: Single Activity with Navigation

---

**Note**: This is a demo project for educational purposes. Make sure to implement proper security measures and error handling in production applications.
