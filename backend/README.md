# RLX Backend API

A Node.js backend API for the RLX mobile application with MongoDB Atlas integration for user authentication and session management.

## Features

- 🔐 Google OIDC Authentication
- 👤 User Management with MongoDB Atlas
- 🔄 JWT Token Management (Access & Refresh Tokens)
- 📱 Mobile App Integration
- 🛡️ Security Features (Rate Limiting, CORS, Helmet)
- 💾 Persistent User Sessions

## Prerequisites

- Node.js (v16 or higher)
- MongoDB Atlas Account
- Google OAuth Credentials

## Installation

1. **Install Dependencies**
   ```bash
   cd backend
   npm install
   ```

2. **Environment Configuration**
   The configuration is already set up in `config.js` with your provided credentials:
   - MongoDB Atlas connection string
   - Google OAuth credentials
   - JWT secrets

3. **Start the Server**
   ```bash
   # Development mode with auto-restart
   npm run dev
   
   # Production mode
   npm start
   ```

## API Endpoints

### Authentication

- `POST /api/auth/google/callback` - Handle Google OAuth callback
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout user
- `POST /api/auth/logout-all` - Logout from all devices
- `GET /api/auth/profile` - Get user profile
- `PUT /api/auth/profile` - Update user profile
- `GET /api/auth/check` - Check authentication status

### OAuth Callback

- `GET /oauth/callback` - Web callback for OAuth flow

### Health Check

- `GET /health` - Server health status

## Database Schema

### User Model

```javascript
{
  googleId: String (unique),
  email: String (unique, required),
  name: String (required),
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
  }
}
```

## Security Features

- **Rate Limiting**: 100 requests per 15 minutes per IP
- **CORS**: Configured for mobile app origins
- **Helmet**: Security headers
- **JWT Tokens**: Secure token-based authentication
- **Token Refresh**: Automatic token refresh mechanism
- **Session Management**: Multi-device session support

## Mobile App Integration

The backend is configured to work with the Android app:

1. **Base URL**: `http://10.0.2.2:8080/api` (Android emulator)
2. **OAuth Redirect**: `http://localhost:8080/oauth/callback`
3. **CORS**: Configured for mobile app requests

## Configuration

All configuration is in `config.js`:

- **MongoDB**: Atlas connection with your credentials
- **Google OAuth**: Your provided client ID and secret
- **JWT**: Secure token configuration
- **Server**: Port 8080

## Development

### Project Structure

```
backend/
├── config.js              # Configuration
├── server.js              # Main server file
├── models/
│   └── User.js            # User model
├── middleware/
│   └── auth.js            # Authentication middleware
├── routes/
│   └── auth.js            # Authentication routes
├── utils/
│   └── jwt.js             # JWT utilities
└── package.json           # Dependencies
```

### Adding New Features

1. Create new models in `models/`
2. Add routes in `routes/`
3. Create middleware in `middleware/`
4. Update server.js to include new routes

## Production Deployment

1. **Environment Variables**: Use environment variables instead of config.js
2. **HTTPS**: Enable HTTPS for production
3. **Database**: Use production MongoDB Atlas cluster
4. **Monitoring**: Add logging and monitoring
5. **Scaling**: Consider load balancing for high traffic

## Troubleshooting

### Common Issues

1. **MongoDB Connection**: Check Atlas credentials and network access
2. **CORS Errors**: Verify CORS configuration for your domain
3. **Token Issues**: Check JWT secret and expiration settings
4. **Google OAuth**: Verify redirect URIs in Google Console

### Logs

Check console output for detailed error messages and request logs.

## Support

For issues or questions, contact the development team.
