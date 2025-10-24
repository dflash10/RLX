# RLX App - Internationalization (i18n) Setup Guide

## 📋 Overview

This guide explains the internationalization (i18n) system implemented in the RLX app, which allows for easy localization and multi-language support.

## 🏗️ Architecture

### File Structure
```
app/src/main/
├── assets/
│   └── locale/
│       └── en_EN.json          # Default English locale
├── java/com/example/googleoidcdemo/
│   └── utils/
│       ├── LocalizationManager.kt    # Core i18n manager
│       └── StringResource.kt         # String resource constants
└── res/values/
    └── strings.xml             # Android string resources (fallback)
```

## 🔧 Implementation Details

### 1. LocalizationManager.kt
- **Purpose**: Core i18n management class
- **Features**:
  - Load locale data from JSON files
  - Parse JSON with custom parser
  - Support for nested keys (e.g., "app.name")
  - String formatting with arguments
  - Locale switching functionality

### 2. StringResource.kt
- **Purpose**: Centralized string resource constants
- **Features**:
  - All string keys defined as constants
  - Organized by screen/feature
  - Easy to maintain and update
  - Type-safe string references

### 3. Locale Files (JSON)
- **Location**: `app/src/main/assets/locale/`
- **Format**: JSON with nested structure
- **Default**: `en_EN.json`
- **Structure**:
```json
{
  "app": {
    "name": "RLX",
    "tagline": "Discover your wellness Journey"
  },
  "welcome": {
    "title": "Discover your wellness Journey",
    "start_button": "Start Now"
  }
}
```

## 📝 Usage Examples

### Basic Usage
```kotlin
// In a Composable function
@Composable
fun MyScreen() {
    Text(
        text = getStringResource(StringResource.WELCOME_TITLE)
    )
}
```

### With Arguments
```kotlin
// For strings with parameters
Text(
    text = getStringResource(StringResource.USER_DETAILS_HEIGHT_LABEL, height)
)
```

### Direct Usage
```kotlin
// Using LocalizationManager directly
val localizationManager = LocalizationManager(context)
val text = localizationManager.getString("welcome.title")
```

## 🌍 Adding New Locales

### 1. Create Locale File
Create a new JSON file in `app/src/main/assets/locale/`:
- `es_ES.json` for Spanish (Spain)
- `fr_FR.json` for French (France)
- `de_DE.json` for German (Germany)

### 2. Translate Content
Copy the structure from `en_EN.json` and translate all values:
```json
{
  "app": {
    "name": "RLX",
    "tagline": "Descubre tu viaje de bienestar"
  },
  "welcome": {
    "title": "Descubre tu viaje de bienestar",
    "start_button": "Comenzar Ahora"
  }
}
```

### 3. Switch Locale
```kotlin
val localizationManager = LocalizationManager(context)
localizationManager.setLocale("es_ES")
```

## 📊 String Resource Categories

### App Labels
- `APP_NAME`: App name
- `APP_TAGLINE`: App tagline
- `POWERED_BY`: Powered by text

### Welcome Screen
- `WELCOME_TITLE`: Main welcome text
- `WELCOME_START_BUTTON`: Start button text
- `WELCOME_LOGO_DESCRIPTION`: Logo accessibility text

### Login Screen
- `LOGIN_TITLE`: Login screen title
- `LOGIN_EMAIL_PHONE_LABEL`: Email/phone field label
- `LOGIN_EMAIL_PHONE_HINT`: Email/phone field hint
- `LOGIN_PASSWORD_LABEL`: Password field label
- `LOGIN_PASSWORD_HINT`: Password field hint
- `LOGIN_FORGOT_PASSWORD`: Forgot password link
- `LOGIN_BUTTON`: Login button text
- `LOGIN_PHONE_BUTTON`: Phone login button text

### Sign Up Screen
- `SIGNUP_TITLE`: Sign up screen title
- `SIGNUP_NAME_LABEL`: Name field label
- `SIGNUP_NAME_HINT`: Name field hint
- `SIGNUP_EMAIL_LABEL`: Email field label
- `SIGNUP_EMAIL_HINT`: Email field hint
- `SIGNUP_PHONE_LABEL`: Phone field label
- `SIGNUP_PHONE_HINT`: Phone field hint
- `SIGNUP_PASSWORD_LABEL`: Password field label
- `SIGNUP_PASSWORD_HINT`: Password field hint
- `SIGNUP_VERIFY_OTP_BUTTON`: OTP verification button
- `SIGNUP_OR_DIVIDER`: OR divider text
- `SIGNUP_GOOGLE_SIGNIN`: Google sign-in button
- `SIGNUP_ALREADY_HAVE_ACCOUNT`: Already have account text

### User Details Screen
- `USER_DETAILS_TITLE`: User details title
- `USER_DETAILS_SUBTITLE`: User details subtitle
- `USER_DETAILS_FIRST_NAME_LABEL`: First name field label
- `USER_DETAILS_FIRST_NAME_HINT`: First name field hint
- `USER_DETAILS_LAST_NAME_LABEL`: Last name field label
- `USER_DETAILS_LAST_NAME_HINT`: Last name field hint
- `USER_DETAILS_AGE_LABEL`: Age field label
- `USER_DETAILS_AGE_HINT`: Age field hint
- `USER_DETAILS_HEIGHT_LABEL`: Height field label (with parameter)
- `USER_DETAILS_WEIGHT_LABEL`: Weight field label (with parameter)
- `USER_DETAILS_GENDER_LABEL`: Gender field label
- `USER_DETAILS_GENDER_MALE`: Male option
- `USER_DETAILS_GENDER_FEMALE`: Female option
- `USER_DETAILS_GENDER_OTHER`: Other option
- `USER_DETAILS_BLOOD_TYPE_LABEL`: Blood type field label
- `USER_DETAILS_BLOOD_TYPE_*`: Blood type options
- `USER_DETAILS_NEXT_BUTTON`: Next button text
- `USER_DETAILS_PROFILE_TITLE`: Profile title
- `USER_DETAILS_LOGOUT_BUTTON`: Logout button text

### Common Labels
- `COMMON_LOADING`: Loading text
- `COMMON_ERROR`: Error text
- `COMMON_SUCCESS`: Success text
- `COMMON_CANCEL`: Cancel button
- `COMMON_OK`: OK button
- `COMMON_SAVE`: Save button
- `COMMON_EDIT`: Edit button
- `COMMON_DELETE`: Delete button
- `COMMON_BACK`: Back button
- `COMMON_NEXT`: Next button
- `COMMON_PREVIOUS`: Previous button
- `COMMON_DONE`: Done button
- `COMMON_CONTINUE`: Continue button
- `COMMON_SKIP`: Skip button
- `COMMON_RETRY`: Retry button
- `COMMON_CLOSE`: Close button

### Validation Messages
- `VALIDATION_REQUIRED_FIELD`: Required field error
- `VALIDATION_INVALID_EMAIL`: Invalid email error
- `VALIDATION_INVALID_PHONE`: Invalid phone error
- `VALIDATION_PASSWORD_TOO_SHORT`: Password too short error
- `VALIDATION_PASSWORD_WEAK`: Weak password error
- `VALIDATION_NAME_TOO_SHORT`: Name too short error
- `VALIDATION_AGE_INVALID`: Invalid age error
- `VALIDATION_HEIGHT_INVALID`: Invalid height error
- `VALIDATION_WEIGHT_INVALID`: Invalid weight error

### Error Messages
- `ERROR_NETWORK`: Network error
- `ERROR_SERVER`: Server error
- `ERROR_AUTHENTICATION_FAILED`: Authentication failed
- `ERROR_INVALID_CREDENTIALS`: Invalid credentials
- `ERROR_USER_NOT_FOUND`: User not found
- `ERROR_EMAIL_ALREADY_EXISTS`: Email already exists
- `ERROR_PHONE_ALREADY_EXISTS`: Phone already exists
- `ERROR_OTP_EXPIRED`: OTP expired
- `ERROR_OTP_INVALID`: Invalid OTP
- `ERROR_SESSION_EXPIRED`: Session expired

### Success Messages
- `SUCCESS_LOGIN`: Login successful
- `SUCCESS_SIGNUP`: Sign up successful
- `SUCCESS_PROFILE_UPDATED`: Profile updated
- `SUCCESS_OTP_SENT`: OTP sent
- `SUCCESS_OTP_VERIFIED`: OTP verified

## 🔄 Migration Guide

### From Hardcoded Strings to i18n

**Before:**
```kotlin
Text(text = "Welcome to RLX")
```

**After:**
```kotlin
Text(text = getStringResource(StringResource.WELCOME_TITLE))
```

### From Android String Resources to i18n

**Before:**
```kotlin
Text(text = stringResource(R.string.welcome_title))
```

**After:**
```kotlin
Text(text = getStringResource(StringResource.WELCOME_TITLE))
```

## 🛠️ Development Workflow

### 1. Adding New Strings
1. Add the string to `en_EN.json`
2. Add the constant to `StringResource.kt`
3. Use the constant in your Composable
4. Test with different locales

### 2. Updating Existing Strings
1. Update the string in `en_EN.json`
2. Update all other locale files
3. Test the changes

### 3. Adding New Locales
1. Create new JSON file in `assets/locale/`
2. Translate all strings
3. Test locale switching

## 🧪 Testing

### Unit Tests
```kotlin
@Test
fun testLocalizationManager() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val manager = LocalizationManager(context)
    
    val result = manager.getString("welcome.title")
    assertEquals("Discover your wellness Journey", result)
}
```

### UI Tests
```kotlin
@Test
fun testWelcomeScreenText() {
    composeTestRule.setContent {
        WelcomeScreenI18n(onStartNowClick = {})
    }
    
    composeTestRule.onNodeWithText("Discover your wellness Journey")
        .assertIsDisplayed()
}
```

## 📱 Best Practices

### 1. String Organization
- Group related strings together
- Use descriptive constant names
- Follow consistent naming conventions

### 2. Parameter Handling
- Use indexed parameters: `{0}`, `{1}`, etc.
- Provide fallback values
- Test with different parameter combinations

### 3. Locale Management
- Always provide fallback to default locale
- Handle missing translations gracefully
- Test with different device locales

### 4. Performance
- Load locale data once
- Cache parsed JSON
- Avoid repeated string lookups

## 🚀 Future Enhancements

### Planned Features
- [ ] Dynamic locale loading from server
- [ ] RTL (Right-to-Left) language support
- [ ] Pluralization support
- [ ] Date/time formatting
- [ ] Number formatting
- [ ] Currency formatting

### Integration Ideas
- [ ] Translation management service
- [ ] Crowdsourced translations
- [ ] Automatic translation updates
- [ ] A/B testing for different translations

## 📞 Support

For questions or issues with the i18n system:
1. Check this documentation
2. Review the example implementations
3. Test with the provided sample files
4. Create an issue in the repository

---

*Last updated: December 2024*
*Version: 1.0*
