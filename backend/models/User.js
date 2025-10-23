const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema({
  googleId: {
    type: String,
    unique: true,
    sparse: true // Allows null values but ensures uniqueness when present
  },
  email: {
    type: String,
    unique: true,
    sparse: true, // Allows null values but ensures uniqueness when present
    lowercase: true,
    trim: true,
    validate: {
      validator: function(v) {
        // Email is optional if phone is provided
        return !v || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
      },
      message: 'Please provide a valid email address'
    }
  },
  phone: {
    type: String,
    unique: true,
    sparse: true, // Allows null values but ensures uniqueness when present
    trim: true,
    validate: {
      validator: function(v) {
        // Phone is optional if email is provided
        return !v || /^(\+91|91)?[6-9]\d{9}$/.test(v);
      },
      message: 'Please provide a valid Indian phone number'
    }
  },
  password: {
    type: String,
    required: function() {
      // Password is required only if user is not using Google OAuth
      return !this.googleId;
    },
    minlength: 6,
    select: false // Don't include password in queries by default
  },
  name: {
    type: String,
    required: true,
    trim: true
  },
  firstName: {
    type: String,
    required: true,
    trim: true,
    minlength: 2,
    maxlength: 50
  },
  lastName: {
    type: String,
    required: true,
    trim: true,
    minlength: 2,
    maxlength: 50
  },
  age: {
    type: Number,
    required: true,
    min: 1,
    max: 120
  },
  picture: {
    type: String,
    default: ''
  },
  verifiedEmail: {
    type: Boolean,
    default: false
  },
  verifiedPhone: {
    type: Boolean,
    default: false
  },
  refreshTokens: [{
    token: {
      type: String,
      required: true
    },
    createdAt: {
      type: Date,
      default: Date.now,
      expires: 90 * 24 * 60 * 60 // 90 days in seconds
    },
    deviceInfo: {
      type: String,
      default: 'Unknown Device'
    }
  }],
  lastLogin: {
    type: Date,
    default: Date.now
  },
  loginCount: {
    type: Number,
    default: 0
  },
  isActive: {
    type: Boolean,
    default: true
  },
  preferences: {
    theme: {
      type: String,
      enum: ['light', 'dark', 'auto'],
      default: 'auto'
    },
    notifications: {
      type: Boolean,
      default: true
    }
  }
}, {
  timestamps: true
});

// Custom validation to ensure either email or phone is provided
userSchema.pre('validate', function(next) {
  if (!this.googleId && !this.email && !this.phone) {
    return next(new Error('Either email or phone number is required'));
  }
  next();
});

// Hash password before saving
userSchema.pre('save', async function(next) {
  // Only hash the password if it has been modified (or is new)
  if (!this.isModified('password')) return next();
  
  try {
    // Hash password with cost of 12
    const salt = await bcrypt.genSalt(12);
    this.password = await bcrypt.hash(this.password, salt);
    next();
  } catch (error) {
    next(error);
  }
});

// Index for faster queries
userSchema.index({ 'refreshTokens.token': 1 });

// Virtual for user's full profile
userSchema.virtual('profile').get(function() {
  return {
    id: this._id,
    email: this.email,
    phone: this.phone,
    name: this.name,
    firstName: this.firstName,
    lastName: this.lastName,
    age: this.age,
    picture: this.picture,
    verifiedEmail: this.verifiedEmail,
    verifiedPhone: this.verifiedPhone,
    lastLogin: this.lastLogin,
    loginCount: this.loginCount,
    preferences: this.preferences
  };
});

// Method to add refresh token
userSchema.methods.addRefreshToken = function(token, deviceInfo = 'Unknown Device') {
  this.refreshTokens.push({
    token: token,
    deviceInfo: deviceInfo
  });
  
  // Keep only last 5 refresh tokens per user
  if (this.refreshTokens.length > 5) {
    this.refreshTokens = this.refreshTokens.slice(-5);
  }
  
  return this.save();
};

// Method to remove refresh token
userSchema.methods.removeRefreshToken = function(token) {
  this.refreshTokens = this.refreshTokens.filter(t => t.token !== token);
  return this.save();
};

// Method to clear all refresh tokens
userSchema.methods.clearAllRefreshTokens = function() {
  this.refreshTokens = [];
  return this.save();
};

// Method to update login info
userSchema.methods.updateLoginInfo = function() {
  this.lastLogin = new Date();
  this.loginCount += 1;
  return this.save();
};

// Method to compare password
userSchema.methods.comparePassword = async function(candidatePassword) {
  if (!this.password) {
    return false;
  }
  return await bcrypt.compare(candidatePassword, this.password);
};

// Static method to find user by Google ID
userSchema.statics.findByGoogleId = function(googleId) {
  return this.findOne({ googleId: googleId, isActive: true });
};

// Static method to find user by email
userSchema.statics.findByEmail = function(email) {
  return this.findOne({ email: email.toLowerCase(), isActive: true });
};

// Static method to find user by phone
userSchema.statics.findByPhone = function(phone) {
  // Normalize phone number (remove +91, 91 prefix)
  const normalizedPhone = phone.replace(/^(\+91|91)/, '');
  return this.findOne({ 
    $or: [
      { phone: phone },
      { phone: `+91${normalizedPhone}` },
      { phone: `91${normalizedPhone}` }
    ],
    isActive: true 
  });
};

// Static method to find user by email or phone
userSchema.statics.findByEmailOrPhone = function(identifier) {
  // Check if identifier is email or phone
  const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(identifier);
  
  if (isEmail) {
    return this.findByEmail(identifier);
  } else {
    return this.findByPhone(identifier);
  }
};

// Static method to create or update user from Google OAuth
userSchema.statics.createOrUpdateFromGoogle = async function(googleUserData) {
  const { id: googleId, email, name, picture, verified_email } = googleUserData;
  
  let user = await this.findByGoogleId(googleId);
  
  if (user) {
    // Update existing user
    user.name = name;
    user.picture = picture || user.picture;
    user.verifiedEmail = verified_email;
    user.lastLogin = new Date();
    user.loginCount += 1;
  } else {
    // Check if user exists with same email
    user = await this.findByEmail(email);
    
    if (user) {
      // Link Google account to existing user
      user.googleId = googleId;
      user.name = name;
      user.picture = picture || user.picture;
      user.verifiedEmail = verified_email;
      user.lastLogin = new Date();
      user.loginCount += 1;
    } else {
      // Create new user
      user = new this({
        googleId: googleId,
        email: email,
        name: name,
        picture: picture || '',
        verifiedEmail: verified_email,
        lastLogin: new Date(),
        loginCount: 1
      });
    }
  }
  
  await user.save();
  return user;
};

module.exports = mongoose.model('User', userSchema);
