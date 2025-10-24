# RLX - Short Documentation

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
- 🌍 Multi-language support (i18n)

---

## 🚀 Quick Start

### Prerequisites
- **Android**: Android Studio, JDK 11+, Android SDK API 24+
- **Backend**: Node.js 16+, MongoDB Atlas, Google Cloud Console

### Setup
1. **Clone repository:**
   ```bash
   git clone https://github.com/dflash10/RLX.git
   cd RLX
   ```

2. **Backend setup:**
   ```bash
   cd backend
   npm install
   cp .env.example .env  # Configure your credentials
   npm start
   ```

3. **Android setup:**
   - Open in Android Studio
   - Sync Gradle files
   - Run on emulator/device

---

## 📁 Project Structure

```
RLX/
├── app/                    # Android Application
│   ├── src/main/java/     # Kotlin source code
│   ├── src/main/res/      # Android resources
│   └── src/main/assets/   # i18n locale files
├── backend/               # Node.js Backend
│   ├── config.js         # Configuration
│   ├── routes/           # API routes
│   ├── models/           # MongoDB models
│   └── server.js         # Main server
├── docs/                 # Documentation
└── README.md
```

---

## ⚙️ Configuration

### Backend Environment Variables
```env
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/database
JWT_SECRET=your_jwt_secret_key
GOOGLE_CLIENT_ID=your_google_web_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
PORT=8080
```

### Android Configuration
Update `app/src/main/res/values/strings.xml`:
```xml
<string name="server_client_id">YOUR_GOOGLE_WEB_CLIENT_ID</string>
<string name="android_client_id">YOUR_GOOGLE_ANDROID_CLIENT_ID</string>
```

---

## 🌍 Internationalization (i18n)

The app supports multiple languages with a comprehensive i18n system:

### Supported Languages
- **English (en_EN)** - Default
- **Spanish (es_ES)** - Complete translation
- **French (fr_FR)** - Complete translation

### Usage
```kotlin
// Get localized string
Text(text = getStringResource(StringResource.WELCOME_TITLE))

// With parameters
Text(text = getStringResource(StringResource.USER_DETAILS_HEIGHT_LABEL, height))
```

### Adding New Languages
1. Create new JSON file in `app/src/main/assets/locale/`
2. Translate all strings following the structure
3. Test locale switching

---

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `PUT /api/auth/user-details` - Update user details

### Health Check
- `GET /health` - API health status

---

## 🚀 Deployment

### Backend (Heroku)
```bash
heroku create rlx-backend
heroku config:set MONGODB_URI=your_mongodb_uri
heroku config:set JWT_SECRET=your_jwt_secret
git push heroku main
```

### Android
1. Generate signed APK in Android Studio
2. Upload to Google Play Console
3. Submit for review

---

## 🔧 Troubleshooting

### Common Issues
- **Google OAuth**: Check SHA-1 fingerprint and package name
- **MongoDB**: Verify connection string and network access
- **Android Build**: Clean and rebuild project
- **Backend**: Check environment variables and port availability

### Debug Commands
```bash
# Check Git status
git status

# Test API
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","password":"password123"}'
```

---

## 🛠️ Development

### Adding New Features
1. Create feature branch: `git checkout -b feature/new-feature`
2. Make changes and test
3. Commit with descriptive message
4. Push and create Pull Request

### Code Standards
- **Android**: Follow Kotlin conventions
- **Backend**: Use ESLint and Prettier
- **Commits**: Use conventional commit format

---

## 📞 Support

For issues and questions:
1. Check troubleshooting section
2. Search existing GitHub issues
3. Create new issue with detailed description

---

## 📄 License

This project is licensed under the MIT License.

---

## 🎯 Roadmap

### Upcoming Features
- [ ] Push notifications
- [ ] Offline data sync
- [ ] Advanced health analytics
- [ ] Social features
- [ ] Wearable device integration

---

*Last updated: December 2024*  
*Version: 1.0*  
*Repository: https://github.com/dflash10/RLX*
