# RLX Documentation
## RLX Wellness App - Complete Development Guide

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Repository Structure](#repository-structure)
3. [Getting Started](#getting-started)
4. [Development Setup](#development-setup)
5. [GitHub Upload Process](#github-upload-process)
6. [Configuration Guide](#configuration-guide)
7. [API Documentation](#api-documentation)
8. [Deployment Guide](#deployment-guide)
9. [Troubleshooting](#troubleshooting)
10. [Contributing](#contributing)

---

## 🏥 Project Overview

**RLX** is a comprehensive wellness application built with modern technologies:

- **Frontend**: Android app using Jetpack Compose
- **Backend**: Node.js with Express.js and MongoDB
- **Authentication**: Google OAuth 2.0 integration
- **Database**: MongoDB Atlas
- **Architecture**: MVVM pattern with clean separation of concerns

### Key Features
- 🔐 Secure Google OAuth authentication
- 📱 Modern Material Design 3 UI
- 👤 User profile management
- 📊 Health data collection and validation
- 🔄 Real-time form validation
- 📱 Responsive slide-out navigation
- 🎨 Custom RLX design system

---

## 📁 Repository Structure

```
RLX/
├── app/                          # Android Application
│   ├── src/main/
│   │   ├── java/com/example/googleoidcdemo/
│   │   │   ├── api/              # API service classes
│   │   │   ├── auth/             # Authentication management
│   │   │   ├── navigation/       # Navigation components
│   │   │   └── ui/               # UI screens and components
│   │   ├── res/                  # Android resources
│   │   │   ├── drawable/         # Images and icons
│   │   │   ├── layout/           # XML layouts
│   │   │   └── values/           # Strings, colors, themes
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── backend/                      # Node.js Backend
│   ├── config.js                # Configuration management
│   ├── middleware/              # Express middleware
│   ├── models/                  # MongoDB models
│   ├── routes/                  # API routes
│   ├── utils/                   # Utility functions
│   ├── server.js                # Main server file
│   └── package.json
├── docs/                        # Documentation
├── README.md
└── SETUP_INSTRUCTIONS.md
```

---

## 🚀 Getting Started

### Prerequisites

**For Android Development:**
- Android Studio (latest version)
- JDK 11 or higher
- Android SDK API 24+
- Git

**For Backend Development:**
- Node.js 16+ 
- MongoDB Atlas account
- Google Cloud Console account
- Git

### Quick Start

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/RLX.git
   cd RLX
   ```

2. **Set up the backend:**
   ```bash
   cd backend
   npm install
   cp .env.example .env
   # Edit .env with your credentials
   npm start
   ```

3. **Set up the Android app:**
   - Open Android Studio
   - Import the project
   - Sync Gradle files
   - Run on emulator or device

---

## 🛠 Development Setup

### Backend Setup

1. **Install dependencies:**
   ```bash
   cd backend
   npm install
   ```

2. **Environment configuration:**
   Create a `.env` file in the backend directory:
   ```env
   # MongoDB Configuration
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/database
   MONGODB_CLIENT_ID=your_mongodb_client_id
   MONGODB_CLIENT_SECRET=your_mongodb_client_secret

   # JWT Configuration
   JWT_SECRET=your_jwt_secret_key
   JWT_EXPIRES_IN=30d
   REFRESH_TOKEN_EXPIRES_IN=90d

   # Google OAuth Configuration
   GOOGLE_CLIENT_ID=your_google_web_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret

   # Server Configuration
   PORT=8080
   NODE_ENV=development
   USE_HTTPS=false
   SSL_CERT_PATH=./certificates/server.crt
   SSL_KEY_PATH=./certificates/server.key
   CORS_ORIGIN=http://localhost:3000,http://localhost:8080
   ```

3. **Start the development server:**
   ```bash
   npm run dev
   ```

### Android Setup

1. **Open in Android Studio:**
   - File → Open → Select the project folder
   - Wait for Gradle sync to complete

2. **Configure Google OAuth:**
   - Update `app/src/main/res/values/strings.xml`:
   ```xml
   <string name="server_client_id">YOUR_GOOGLE_WEB_CLIENT_ID</string>
   <string name="android_client_id">YOUR_GOOGLE_ANDROID_CLIENT_ID</string>
   <string name="client_secret">YOUR_GOOGLE_CLIENT_SECRET</string>
   ```

3. **Run the application:**
   - Select target device/emulator
   - Click Run button (▶️)

---

## 📤 GitHub Upload Process

### Initial Repository Setup

1. **Create a new repository on GitHub:**
   - Go to GitHub.com
   - Click "New repository"
   - Name: `RLX` or `android-google-oidc-demo`
   - Make it public or private as needed
   - Don't initialize with README (we have existing files)

2. **Initialize local Git repository:**
   ```bash
   git init
   git add .
   git commit -m "Initial commit: RLX wellness app"
   ```

3. **Connect to remote repository:**
   ```bash
   git remote add origin https://github.com/yourusername/RLX.git
   git branch -M main
   git push -u origin main
   ```

### Regular Upload Process

1. **Check repository status:**
   ```bash
   git status
   ```

2. **Add changes:**
   ```bash
   git add .
   # Or add specific files:
   git add app/src/main/java/com/example/googleoidcdemo/
   git add backend/
   ```

3. **Commit changes:**
   ```bash
   git commit -m "Descriptive commit message"
   ```

4. **Push to GitHub:**
   ```bash
   git push origin main
   ```

### Security Considerations

**Important:** Never commit sensitive data like:
- API keys
- Database credentials
- OAuth client secrets
- JWT secrets

**Use environment variables instead:**
```javascript
// ❌ Don't do this:
const API_KEY = 'sk-1234567890abcdef';

// ✅ Do this:
const API_KEY = process.env.API_KEY;
```

### Handling Secrets

If you accidentally commit secrets:

1. **Remove from history:**
   ```bash
   git filter-branch --force --index-filter "git rm --cached --ignore-unmatch path/to/file" --prune-empty --tag-name-filter cat -- --all
   ```

2. **Force push (be careful!):**
   ```bash
   git push origin main --force
   ```

3. **Clean up:**
   ```bash
   git reflog expire --expire=now --all
   git gc --prune=now --aggressive
   ```

---

## ⚙️ Configuration Guide

### Google OAuth Setup

1. **Go to Google Cloud Console:**
   - Visit: https://console.cloud.google.com/
   - Create a new project or select existing

2. **Enable Google Sign-In API:**
   - APIs & Services → Library
   - Search "Google Sign-In API"
   - Click Enable

3. **Configure OAuth consent screen:**
   - APIs & Services → OAuth consent screen
   - Choose External or Internal
   - Fill in app information
   - Add scopes: `openid`, `email`, `profile`

4. **Create OAuth 2.0 credentials:**
   
   **For Web Application:**
   - APIs & Services → Credentials
   - Create Credentials → OAuth 2.0 Client ID
   - Application type: Web application
   - Authorized redirect URIs: `http://localhost:8080/oauth/callback`
   - Copy Client ID and Client Secret

   **For Android Application:**
   - Create Credentials → OAuth 2.0 Client ID
   - Application type: Android
   - Package name: `com.example.googleoidcdemo`
   - SHA-1 certificate fingerprint: Get from your debug keystore
   - Copy Client ID

### MongoDB Atlas Setup

1. **Create MongoDB Atlas account:**
   - Visit: https://www.mongodb.com/atlas
   - Sign up for free account

2. **Create a cluster:**
   - Choose free tier (M0)
   - Select region closest to you
   - Create cluster

3. **Set up database access:**
   - Database Access → Add New Database User
   - Create username and password
   - Set permissions: Read and write to any database

4. **Configure network access:**
   - Network Access → Add IP Address
   - Add your current IP or 0.0.0.0/0 for development

5. **Get connection string:**
   - Clusters → Connect → Connect your application
   - Copy connection string
   - Replace `<password>` with your database user password

### HTTPS Setup

**Important:** The project now supports HTTPS for production security.

1. **For Development (Optional):**
   ```bash
   # Generate self-signed certificate
   mkdir backend/certificates
   cd backend/certificates
   openssl genrsa -out server.key 2048
   openssl req -new -x509 -key server.key -out server.crt -days 365
   ```

2. **For Production (Required):**
   - Use Let's Encrypt certificates
   - Or deploy to cloud platforms (Heroku, Railway, Vercel) for automatic HTTPS
   - See `HTTPS_SETUP_GUIDE.md` for detailed instructions

3. **Environment Variables:**
   ```env
   USE_HTTPS=true
   SSL_CERT_PATH=/path/to/certificate.crt
   SSL_KEY_PATH=/path/to/private.key
   ```

---

## 📚 API Documentation

### Authentication Endpoints

#### POST `/api/auth/register`
Register a new user.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": "user_id",
      "email": "john@example.com",
      "name": "John Doe"
    },
    "tokens": {
      "accessToken": "jwt_token",
      "refreshToken": "refresh_token"
    }
  }
}
```

#### POST `/api/auth/login`
Login with email/phone and password.

**Request Body:**
```json
{
  "identifier": "john@example.com",
  "password": "securePassword123"
}
```

#### PUT `/api/auth/user-details`
Update user details (requires authentication).

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "age": 25
}
```

**Headers:**
```
Authorization: Bearer <access_token>
```

### Validation Rules

**First Name & Last Name:**
- Required field
- 2-50 characters
- Letters and spaces only
- Real-time validation

**Age:**
- Required field
- Integer between 1-120
- Numbers only input

---

## 🚀 Deployment Guide

### Backend Deployment (Heroku)

1. **Install Heroku CLI:**
   ```bash
   # Download from https://devcenter.heroku.com/articles/heroku-cli
   ```

2. **Login to Heroku:**
   ```bash
   heroku login
   ```

3. **Create Heroku app:**
   ```bash
   heroku create rlx-backend
   ```

4. **Set environment variables:**
   ```bash
   heroku config:set MONGODB_URI=your_mongodb_uri
   heroku config:set GOOGLE_CLIENT_ID=your_google_client_id
   heroku config:set GOOGLE_CLIENT_SECRET=your_google_client_secret
   heroku config:set JWT_SECRET=your_jwt_secret
   ```

5. **Deploy:**
   ```bash
   git push heroku main
   ```

### Android App Deployment

1. **Generate signed APK:**
   - Build → Generate Signed Bundle/APK
   - Create new keystore or use existing
   - Choose APK
   - Select release build type

2. **Upload to Google Play Store:**
   - Go to Google Play Console
   - Create new application
   - Upload APK/AAB file
   - Fill in store listing details
   - Submit for review

---

## 🔧 Troubleshooting

### Common Issues

**1. Google OAuth not working:**
- Check SHA-1 fingerprint matches
- Verify package name is correct
- Ensure OAuth consent screen is configured

**2. MongoDB connection failed:**
- Check connection string format
- Verify network access settings
- Ensure database user has correct permissions

**3. Android build errors:**
- Clean and rebuild project
- Check Gradle version compatibility
- Verify all dependencies are resolved

**4. Backend not starting:**
- Check all environment variables are set
- Verify MongoDB connection
- Check port availability

### Debug Commands

**Check Git status:**
```bash
git status
git log --oneline
```

**Check environment variables:**
```bash
# Backend
node -e "console.log(process.env)"

# Android
# Check in Android Studio → Build → Environment
```

**Test API endpoints:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","password":"password123"}'
```

---

## 🤝 Contributing

### Development Workflow

1. **Fork the repository**
2. **Create a feature branch:**
   ```bash
   git checkout -b feature/new-feature
   ```
3. **Make your changes**
4. **Test thoroughly**
5. **Commit with descriptive messages:**
   ```bash
   git commit -m "Add: New feature for user validation"
   ```
6. **Push to your fork:**
   ```bash
   git push origin feature/new-feature
   ```
7. **Create a Pull Request**

### Code Standards

- **Android**: Follow Kotlin coding conventions
- **Backend**: Use ESLint and Prettier
- **Commits**: Use conventional commit format
- **Documentation**: Update docs for new features

### Testing

**Backend Testing:**
```bash
cd backend
npm test
```

**Android Testing:**
- Run unit tests in Android Studio
- Test on multiple devices/emulators
- Verify all user flows work correctly

---

## 📞 Support

For issues and questions:

1. **Check the troubleshooting section**
2. **Search existing GitHub issues**
3. **Create a new issue with:**
   - Detailed description
   - Steps to reproduce
   - Expected vs actual behavior
   - System information

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🎯 Roadmap

### Upcoming Features
- [ ] Push notifications
- [ ] Offline data sync
- [ ] Advanced health analytics
- [ ] Social features
- [ ] Wearable device integration

### Version History
- **v1.0.0**: Initial release with basic authentication
- **v1.1.0**: Added user profile management
- **v1.2.0**: Enhanced validation and UI improvements
- **v1.3.0**: Backend API integration and security improvements

---

*Last updated: December 2024*
*Documentation version: 1.0*
