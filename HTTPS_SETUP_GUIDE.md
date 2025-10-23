# HTTPS Setup Guide for RX3 RHealth App

## 🔒 Why HTTPS is Important

- **Security**: Encrypts data transmission between client and server
- **Authentication**: Prevents man-in-the-middle attacks
- **Trust**: Users see the secure lock icon in browsers
- **OAuth Requirements**: Google OAuth requires HTTPS for production
- **Mobile App Security**: Protects API communications

## 🚀 HTTPS Configuration Options

### Option 1: Development with Self-Signed Certificates

**For local development only:**

1. **Generate self-signed certificate:**
   ```bash
   # Create certificates directory
   mkdir backend/certificates
   cd backend/certificates
   
   # Generate private key
   openssl genrsa -out server.key 2048
   
   # Generate certificate
   openssl req -new -x509 -key server.key -out server.crt -days 365
   ```

2. **Set environment variables:**
   ```env
   USE_HTTPS=true
   SSL_CERT_PATH=./certificates/server.crt
   SSL_KEY_PATH=./certificates/server.key
   NODE_ENV=development
   ```

3. **Start server:**
   ```bash
   npm start
   ```

4. **Access your API:**
   - API: `https://localhost:8080`
   - Health check: `https://localhost:8080/health`

### Option 2: Production with Let's Encrypt (Recommended)

**For production deployment:**

1. **Install Certbot:**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install certbot
   
   # macOS
   brew install certbot
   ```

2. **Generate Let's Encrypt certificate:**
   ```bash
   sudo certbot certonly --standalone -d yourdomain.com
   ```

3. **Set environment variables:**
   ```env
   USE_HTTPS=true
   SSL_CERT_PATH=/etc/letsencrypt/live/yourdomain.com/fullchain.pem
   SSL_KEY_PATH=/etc/letsencrypt/live/yourdomain.com/privkey.pem
   NODE_ENV=production
   CORS_ORIGIN=https://yourdomain.com,https://www.yourdomain.com
   ```

### Option 3: Cloud Platform HTTPS (Easiest)

**For Heroku, Railway, or other cloud platforms:**

1. **Heroku (Automatic HTTPS):**
   ```bash
   # Deploy to Heroku
   git push heroku main
   
   # Heroku automatically provides HTTPS
   # Your app will be available at: https://your-app-name.herokuapp.com
   ```

2. **Railway (Automatic HTTPS):**
   ```bash
   # Connect GitHub repository to Railway
   # Railway automatically provides HTTPS
   ```

3. **Vercel (Automatic HTTPS):**
   ```bash
   # Deploy to Vercel
   vercel --prod
   
   # Vercel automatically provides HTTPS
   ```

## 🔧 Environment Configuration

### Development Environment
```env
# .env (Development)
NODE_ENV=development
USE_HTTPS=true
SSL_CERT_PATH=./certificates/server.crt
SSL_KEY_PATH=./certificates/server.key
CORS_ORIGIN=http://localhost:3000,http://localhost:8080,https://localhost:8080
```

### Production Environment
```env
# .env (Production)
NODE_ENV=production
USE_HTTPS=true
SSL_CERT_PATH=/etc/letsencrypt/live/yourdomain.com/fullchain.pem
SSL_KEY_PATH=/etc/letsencrypt/live/yourdomain.com/privkey.pem
CORS_ORIGIN=https://yourdomain.com,https://www.yourdomain.com
```

## 📱 Android App HTTPS Configuration

### Update Android Network Security Config

1. **Create network security config:**
   ```xml
   <!-- app/src/main/res/xml/network_security_config.xml -->
   <?xml version="1.0" encoding="utf-8"?>
   <network-security-config>
       <domain-config cleartextTrafficPermitted="false">
           <domain includeSubdomains="true">yourdomain.com</domain>
           <domain includeSubdomains="true">api.yourdomain.com</domain>
       </domain-config>
       
       <!-- For development only -->
       <debug-overrides>
           <trust-anchors>
               <certificates src="system"/>
               <certificates src="user"/>
           </trust-anchors>
       </debug-overrides>
   </network-security-config>
   ```

2. **Update AndroidManifest.xml:**
   ```xml
   <application
       android:networkSecurityConfig="@xml/network_security_config"
       ... >
   ```

3. **Update API base URL:**
   ```kotlin
   // In your API service
   private const val BASE_URL = "https://yourdomain.com/api"
   ```

## 🔐 Google OAuth HTTPS Requirements

### Update Google OAuth Configuration

1. **Google Cloud Console:**
   - Go to APIs & Services → Credentials
   - Edit your OAuth 2.0 Client ID
   - Update Authorized redirect URIs:
     ```
     https://yourdomain.com/oauth/callback
     ```

2. **Update Android OAuth redirect:**
   ```kotlin
   // In OidcAuthManager.kt
   private const val REDIRECT_URI = "https://yourdomain.com/oauth/callback"
   ```

## 🚀 Deployment with HTTPS

### Heroku Deployment
```bash
# Set environment variables
heroku config:set USE_HTTPS=true
heroku config:set NODE_ENV=production
heroku config:set CORS_ORIGIN=https://yourdomain.com

# Deploy
git push heroku main
```

### VPS Deployment with Nginx
```nginx
# /etc/nginx/sites-available/yourdomain.com
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;
    
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

## 🔍 Testing HTTPS

### Test SSL Certificate
```bash
# Test certificate validity
openssl x509 -in server.crt -text -noout

# Test HTTPS endpoint
curl -k https://localhost:8080/health
```

### Browser Testing
1. Open `https://yourdomain.com/health`
2. Check for green lock icon
3. Verify certificate details
4. Test API endpoints

## ⚠️ Security Considerations

### Certificate Management
- **Never commit certificates to Git**
- **Use environment variables for paths**
- **Rotate certificates regularly**
- **Monitor certificate expiration**

### Production Checklist
- [ ] Valid SSL certificate installed
- [ ] HTTPS redirect configured
- [ ] Security headers enabled
- [ ] CORS properly configured
- [ ] Google OAuth URLs updated
- [ ] Android app uses HTTPS endpoints

## 🛠 Troubleshooting

### Common Issues

**1. Certificate not found:**
```
Error: ENOENT: no such file or directory
```
**Solution:** Check file paths in environment variables

**2. Self-signed certificate warnings:**
```
NET::ERR_CERT_AUTHORITY_INVALID
```
**Solution:** Accept certificate in browser for development

**3. CORS errors with HTTPS:**
```
Access to fetch at 'https://api.domain.com' from origin 'https://app.domain.com' has been blocked by CORS policy
```
**Solution:** Update CORS_ORIGIN environment variable

**4. Google OAuth redirect mismatch:**
```
Error 400: redirect_uri_mismatch
```
**Solution:** Update Google OAuth redirect URIs in Google Cloud Console

## 📚 Additional Resources

- [Let's Encrypt Documentation](https://letsencrypt.org/docs/)
- [Node.js HTTPS Documentation](https://nodejs.org/api/https.html)
- [Android Network Security Config](https://developer.android.com/training/articles/security-config)
- [Google OAuth 2.0 Documentation](https://developers.google.com/identity/protocols/oauth2)

---

**Remember:** Always use HTTPS in production for security and compliance! 🔒
