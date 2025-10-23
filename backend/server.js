const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const https = require('https');
const fs = require('fs');
const config = require('./config');

// Import routes
const authRoutes = require('./routes/auth');

const app = express();

// Security middleware
app.use(helmet());

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: {
    success: false,
    message: 'Too many requests from this IP, please try again later.'
  }
});
app.use(limiter);

// CORS configuration
const corsOptions = {
  origin: function (origin, callback) {
    const allowedOrigins = config.CORS_ORIGIN.split(',');
    if (!origin || allowedOrigins.includes(origin)) {
      callback(null, true);
    } else {
      callback(new Error('Not allowed by CORS'));
    }
  },
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With']
};
app.use(cors(corsOptions));

// Body parsing middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// MongoDB connection
mongoose.connect(config.MONGODB_URI)
.then(() => {
  console.log('✅ Connected to MongoDB Atlas');
})
.catch((error) => {
  console.error('❌ MongoDB connection error:', error);
  process.exit(1);
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    success: true,
    message: 'RHealth Backend API is running',
    timestamp: new Date().toISOString(),
    environment: config.NODE_ENV,
    mongodb: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected'
  });
});

// API routes
app.use('/api/auth', authRoutes);

// OAuth callback endpoint (for direct browser redirects)
app.get('/oauth/callback', (req, res) => {
  const { code, state, error } = req.query;
  
  if (error) {
    return res.status(400).send(`
      <html>
        <body>
          <h1>Authentication Error</h1>
          <p>Error: ${error}</p>
          <p>Please try again.</p>
        </body>
      </html>
    `);
  }
  
  if (!code) {
    return res.status(400).send(`
      <html>
        <body>
          <h1>Authentication Error</h1>
          <p>No authorization code received.</p>
          <p>Please try again.</p>
        </body>
      </html>
    `);
  }
  
  // Redirect to mobile app with the authorization code
  const mobileAppUrl = `com.example.googleoidcdemo://oauth/callback?code=${code}&state=${state || ''}`;
  
  res.send(`
    <html>
      <body>
        <h1>Authentication Successful</h1>
        <p>Redirecting to mobile app...</p>
        <script>
          // Try to redirect to mobile app
          window.location.href = '${mobileAppUrl}';
          
          // Fallback: show instructions if mobile app doesn't open
          setTimeout(() => {
            document.body.innerHTML = \`
              <h1>Authentication Successful</h1>
              <p>If the mobile app didn't open automatically, please:</p>
              <ol>
                <li>Open the RHealth mobile app</li>
                <li>The authentication should complete automatically</li>
              </ol>
              <p>Authorization Code: <code>${code}</code></p>
            \`;
          }, 3000);
        </script>
      </body>
    </html>
  `);
});

// 404 handler
app.use('*', (req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint not found',
    path: req.originalUrl
  });
});

// Global error handler
app.use((error, req, res, next) => {
  console.error('Global error handler:', error);
  
  if (error.name === 'ValidationError') {
    return res.status(400).json({
      success: false,
      message: 'Validation error',
      errors: Object.values(error.errors).map(err => err.message)
    });
  }
  
  if (error.name === 'CastError') {
    return res.status(400).json({
      success: false,
      message: 'Invalid ID format'
    });
  }
  
  res.status(500).json({
    success: false,
    message: 'Internal server error',
    error: config.NODE_ENV === 'development' ? error.message : 'Something went wrong'
  });
});

// Graceful shutdown
process.on('SIGINT', async () => {
  console.log('🛑 Shutting down server...');
  await mongoose.connection.close();
  process.exit(0);
});

process.on('SIGTERM', async () => {
  console.log('🛑 Shutting down server...');
  await mongoose.connection.close();
  process.exit(0);
});

const PORT = config.PORT;

// Start server with HTTPS or HTTP based on configuration
if (config.USE_HTTPS && config.SSL_CERT_PATH && config.SSL_KEY_PATH) {
  try {
    const options = {
      cert: fs.readFileSync(config.SSL_CERT_PATH),
      key: fs.readFileSync(config.SSL_KEY_PATH)
    };
    
    https.createServer(options, app).listen(PORT, () => {
      console.log(`🚀 RHealth Backend API running on HTTPS port ${PORT}`);
      console.log(`📱 Environment: ${config.NODE_ENV}`);
      console.log(`🔗 Health check: https://localhost:${PORT}/health`);
      console.log(`🔐 Auth endpoints: https://localhost:${PORT}/api/auth`);
      console.log(`🔒 SSL Certificate: ${config.SSL_CERT_PATH}`);
    });
  } catch (error) {
    console.error('❌ HTTPS setup failed:', error.message);
    console.log('🔄 Falling back to HTTP...');
    startHttpServer();
  }
} else {
  startHttpServer();
}

function startHttpServer() {
  app.listen(PORT, () => {
    console.log(`🚀 RHealth Backend API running on HTTP port ${PORT}`);
    console.log(`📱 Environment: ${config.NODE_ENV}`);
    console.log(`🔗 Health check: http://localhost:${PORT}/health`);
    console.log(`🔐 Auth endpoints: http://localhost:${PORT}/api/auth`);
    if (config.NODE_ENV === 'production') {
      console.log('⚠️  WARNING: Running in production without HTTPS!');
    }
  });
}

module.exports = app;
