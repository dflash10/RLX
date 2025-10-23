# Backend Setup Instructions

## Environment Variables

Create a `.env` file in the backend directory with the following variables:

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
CORS_ORIGIN=http://localhost:3000,http://localhost:8080
```

## Android Configuration

Update the following files with your actual credentials:

### app/src/main/res/values/strings.xml
```xml
<string name="server_client_id">YOUR_GOOGLE_WEB_CLIENT_ID</string>
<string name="android_client_id">YOUR_GOOGLE_ANDROID_CLIENT_ID</string>
<string name="client_secret">YOUR_GOOGLE_CLIENT_SECRET</string>
```

## Getting Credentials

1. **Google OAuth**: Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create OAuth 2.0 credentials for Web and Android applications
   - Use the Web client ID for `GOOGLE_CLIENT_ID` and `server_client_id`
   - Use the Android client ID for `android_client_id`
   - Use the client secret for `GOOGLE_CLIENT_SECRET` and `client_secret`

2. **MongoDB**: Set up MongoDB Atlas and get your connection string

3. **JWT Secret**: Generate a secure random string for JWT signing
