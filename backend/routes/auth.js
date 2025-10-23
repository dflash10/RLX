const express = require('express');
const axios = require('axios');
const User = require('../models/User');
const { generateTokenPair } = require('../utils/jwt');
const { authenticateRefreshToken } = require('../middleware/auth');
const { validateRegistration, validateLogin, validateProfileUpdate, validateUserDetails } = require('../middleware/validation');
const config = require('../config');

const router = express.Router();

// User registration endpoint
router.post('/register', validateRegistration, async (req, res) => {
  try {
    const { name, email, phone, password } = req.body;

    // Check if user already exists
    const existingUser = await User.findOne({
      $or: [
        ...(email ? [{ email: email.toLowerCase() }] : []),
        ...(phone ? [{ phone: phone }] : [])
      ]
    });

    if (existingUser) {
      return res.status(409).json({
        success: false,
        message: 'User already exists with this email or phone number'
      });
    }

    // Create new user
    const user = new User({
      name,
      email: email ? email.toLowerCase() : undefined,
      phone: phone,
      password
    });

    await user.save();

    // Generate tokens
    const tokens = generateTokenPair(user);

    // Add refresh token to user's refresh tokens
    await user.addRefreshToken(tokens.refreshToken, req.headers['user-agent'] || 'Unknown Device');

    res.status(201).json({
      success: true,
      message: 'User registered successfully',
      data: {
        user: user.profile,
        tokens: tokens
      }
    });

  } catch (error) {
    console.error('Registration error:', error);
    
    if (error.code === 11000) {
      return res.status(409).json({
        success: false,
        message: 'User already exists with this email or phone number'
      });
    }

    res.status(500).json({
      success: false,
      message: 'Registration failed',
      error: error.message
    });
  }
});

// User login endpoint
router.post('/login', validateLogin, async (req, res) => {
  try {
    const { identifier, password } = req.body;

    // Find user by email or phone
    const user = await User.findByEmailOrPhone(identifier).select('+password');

    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Invalid credentials'
      });
    }

    // Check password
    const isPasswordValid = await user.comparePassword(password);
    if (!isPasswordValid) {
      return res.status(401).json({
        success: false,
        message: 'Invalid credentials'
      });
    }

    // Update login info
    await user.updateLoginInfo();

    // Generate tokens
    const tokens = generateTokenPair(user);

    // Add refresh token to user's refresh tokens
    await user.addRefreshToken(tokens.refreshToken, req.headers['user-agent'] || 'Unknown Device');

    res.json({
      success: true,
      message: 'Login successful',
      data: {
        user: user.profile,
        tokens: tokens
      }
    });

  } catch (error) {
    console.error('Login error:', error);
    res.status(500).json({
      success: false,
      message: 'Login failed',
      error: error.message
    });
  }
});

// Google OAuth callback endpoint
router.post('/google/callback', async (req, res) => {
  try {
    const { code, state } = req.body;

    if (!code) {
      return res.status(400).json({
        success: false,
        message: 'Authorization code is required'
      });
    }

    // Exchange authorization code for access token
    const tokenResponse = await axios.post('https://oauth2.googleapis.com/token', {
      code: code,
      client_id: config.GOOGLE_CLIENT_ID,
      client_secret: config.GOOGLE_CLIENT_SECRET,
      redirect_uri: 'http://localhost:8080/oauth/callback',
      grant_type: 'authorization_code'
    }, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });

    const { access_token } = tokenResponse.data;

    // Get user info from Google
    const userInfoResponse = await axios.get('https://www.googleapis.com/oauth2/v2/userinfo', {
      headers: {
        'Authorization': `Bearer ${access_token}`
      }
    });

    const googleUserData = userInfoResponse.data;

    // Create or update user in database
    const user = await User.createOrUpdateFromGoogle(googleUserData);

    // Generate tokens
    const tokens = generateTokenPair(user);

    // Add refresh token to user's refresh tokens
    await user.addRefreshToken(tokens.refreshToken, req.headers['user-agent'] || 'Unknown Device');

    res.json({
      success: true,
      message: 'Authentication successful',
      data: {
        user: user.profile,
        tokens: tokens
      }
    });

  } catch (error) {
    console.error('Google OAuth callback error:', error);
    
    if (error.response) {
      return res.status(400).json({
        success: false,
        message: 'Google OAuth error',
        error: error.response.data
      });
    }

    res.status(500).json({
      success: false,
      message: 'Authentication failed',
      error: error.message
    });
  }
});

// Refresh token endpoint
router.post('/refresh', authenticateRefreshToken, async (req, res) => {
  try {
    const { refreshToken } = req.body;
    const user = req.user;

    // Generate new token pair
    const tokens = generateTokenPair(user);

    // Remove old refresh token and add new one
    await user.removeRefreshToken(refreshToken);
    await user.addRefreshToken(tokens.refreshToken, req.headers['user-agent'] || 'Unknown Device');

    res.json({
      success: true,
      message: 'Token refreshed successfully',
      data: {
        tokens: tokens
      }
    });

  } catch (error) {
    console.error('Token refresh error:', error);
    res.status(500).json({
      success: false,
      message: 'Token refresh failed',
      error: error.message
    });
  }
});

// Logout endpoint
router.post('/logout', authenticateRefreshToken, async (req, res) => {
  try {
    const { refreshToken } = req.body;
    const user = req.user;

    // Remove the specific refresh token
    await user.removeRefreshToken(refreshToken);

    res.json({
      success: true,
      message: 'Logged out successfully'
    });

  } catch (error) {
    console.error('Logout error:', error);
    res.status(500).json({
      success: false,
      message: 'Logout failed',
      error: error.message
    });
  }
});

// Logout from all devices
router.post('/logout-all', authenticateRefreshToken, async (req, res) => {
  try {
    const user = req.user;

    // Clear all refresh tokens
    await user.clearAllRefreshTokens();

    res.json({
      success: true,
      message: 'Logged out from all devices successfully'
    });

  } catch (error) {
    console.error('Logout all error:', error);
    res.status(500).json({
      success: false,
      message: 'Logout failed',
      error: error.message
    });
  }
});

// Get user profile
router.get('/profile', require('../middleware/auth').authenticateToken, async (req, res) => {
  try {
    const user = req.user;

    res.json({
      success: true,
      data: {
        user: user.profile
      }
    });

  } catch (error) {
    console.error('Get profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get profile',
      error: error.message
    });
  }
});

// Update user profile
router.put('/profile', require('../middleware/auth').authenticateToken, validateProfileUpdate, async (req, res) => {
  try {
    const user = req.user;
    const { name, preferences } = req.body;

    if (name) user.name = name;
    if (preferences) user.preferences = { ...user.preferences, ...preferences };

    await user.save();

    res.json({
      success: true,
      message: 'Profile updated successfully',
      data: {
        user: user.profile
      }
    });

  } catch (error) {
    console.error('Update profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to update profile',
      error: error.message
    });
  }
});

// Update user details (for "Tell a bit about yourself" form)
router.put('/user-details', require('../middleware/auth').authenticateToken, validateUserDetails, async (req, res) => {
  try {
    const user = req.user;
    const { firstName, lastName, age } = req.body;

    // Update user details
    user.firstName = firstName;
    user.lastName = lastName;
    user.age = age;
    
    // Update the full name as well
    user.name = `${firstName} ${lastName}`;

    await user.save();

    res.json({
      success: true,
      message: 'User details updated successfully',
      data: {
        user: user.profile
      }
    });

  } catch (error) {
    console.error('Update user details error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to update user details',
      error: error.message
    });
  }
});

// Check if user is authenticated
router.get('/check', require('../middleware/auth').authenticateToken, async (req, res) => {
  try {
    const user = req.user;

    res.json({
      success: true,
      message: 'User is authenticated',
      data: {
        user: user.profile
      }
    });

  } catch (error) {
    console.error('Auth check error:', error);
    res.status(500).json({
      success: false,
      message: 'Authentication check failed',
      error: error.message
    });
  }
});

module.exports = router;
