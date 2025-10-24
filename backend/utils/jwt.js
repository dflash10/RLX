const jwt = require('jsonwebtoken');
const config = require('../config');

// Generate access token
const generateAccessToken = (user) => {
  const payload = {
    userId: user._id,
    email: user.email,
    name: user.name
  };

  return jwt.sign(payload, config.JWT_SECRET, {
    expiresIn: config.JWT_EXPIRES_IN,
    issuer: 'rlx-api',
    audience: 'rlx-mobile'
  });
};

// Generate refresh token
const generateRefreshToken = (user) => {
  const payload = {
    userId: user._id,
    type: 'refresh'
  };

  return jwt.sign(payload, config.JWT_SECRET, {
    expiresIn: config.REFRESH_TOKEN_EXPIRES_IN,
    issuer: 'rlx-api',
    audience: 'rlx-mobile'
  });
};

// Generate token pair
const generateTokenPair = (user) => {
  const accessToken = generateAccessToken(user);
  const refreshToken = generateRefreshToken(user);
  
  return {
    accessToken,
    refreshToken,
    expiresIn: getTokenExpiration(config.JWT_EXPIRES_IN)
  };
};

// Get token expiration time in seconds
const getTokenExpiration = (expiresIn) => {
  const timeUnits = {
    's': 1,
    'm': 60,
    'h': 3600,
    'd': 86400
  };
  
  const match = expiresIn.match(/^(\d+)([smhd])$/);
  if (!match) return 3600; // Default 1 hour
  
  const value = parseInt(match[1]);
  const unit = match[2];
  
  return value * timeUnits[unit];
};

// Verify token
const verifyToken = (token) => {
  try {
    return jwt.verify(token, config.JWT_SECRET);
  } catch (error) {
    throw error;
  }
};

// Decode token without verification (for debugging)
const decodeToken = (token) => {
  return jwt.decode(token);
};

module.exports = {
  generateAccessToken,
  generateRefreshToken,
  generateTokenPair,
  getTokenExpiration,
  verifyToken,
  decodeToken
};
