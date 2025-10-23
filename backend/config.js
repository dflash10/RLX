module.exports = {
  // MongoDB Atlas Connection
  MONGODB_URI: process.env.MONGODB_URI || 'mongodb://localhost:27017/rhealth',
  
  // MongoDB Atlas Service Account
  MONGODB_CLIENT_ID: process.env.MONGODB_CLIENT_ID || '',
  MONGODB_CLIENT_SECRET: process.env.MONGODB_CLIENT_SECRET || '',
  
  // JWT Configuration
  JWT_SECRET: process.env.JWT_SECRET || 'rhealth_jwt_secret_key_2024_secure_random_string',
  JWT_EXPIRES_IN: process.env.JWT_EXPIRES_IN || '30d',
  REFRESH_TOKEN_EXPIRES_IN: process.env.REFRESH_TOKEN_EXPIRES_IN || '90d',
  
  // Server Configuration
  PORT: process.env.PORT || 8080,
  NODE_ENV: process.env.NODE_ENV || 'development',
  
  // Google OAuth Configuration
  GOOGLE_CLIENT_ID: process.env.GOOGLE_CLIENT_ID || '',
  GOOGLE_CLIENT_SECRET: process.env.GOOGLE_CLIENT_SECRET || '',
  
  // CORS Configuration
  CORS_ORIGIN: process.env.CORS_ORIGIN || 'http://localhost:3000,http://localhost:8080'
};
