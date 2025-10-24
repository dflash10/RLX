package com.example.googleoidcdemo.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * StringResource utility for easy access to localized strings
 */
object StringResource {
    
    // App Labels
    const val APP_NAME = "app.name"
    const val APP_TAGLINE = "app.tagline"
    const val POWERED_BY = "app.powered_by"
    
    // Welcome Screen
    const val WELCOME_TITLE = "welcome.title"
    const val WELCOME_START_BUTTON = "welcome.start_button"
    const val WELCOME_LOGO_DESCRIPTION = "welcome.logo_description"
    
    // Login Screen
    const val LOGIN_TITLE = "login.title"
    const val LOGIN_EMAIL_PHONE_LABEL = "login.email_phone_label"
    const val LOGIN_EMAIL_PHONE_HINT = "login.email_phone_hint"
    const val LOGIN_PASSWORD_LABEL = "login.password_label"
    const val LOGIN_PASSWORD_HINT = "login.password_hint"
    const val LOGIN_FORGOT_PASSWORD = "login.forgot_password"
    const val LOGIN_BUTTON = "login.button"
    const val LOGIN_PHONE_BUTTON = "login.phone_button"
    const val LOGIN_LOGO_DESCRIPTION = "login.logo_description"
    
    // Sign Up Screen
    const val SIGNUP_TITLE = "signup.title"
    const val SIGNUP_NAME_LABEL = "signup.name_label"
    const val SIGNUP_NAME_HINT = "signup.name_hint"
    const val SIGNUP_EMAIL_LABEL = "signup.email_label"
    const val SIGNUP_EMAIL_HINT = "signup.email_hint"
    const val SIGNUP_PHONE_LABEL = "signup.phone_label"
    const val SIGNUP_PHONE_HINT = "signup.phone_hint"
    const val SIGNUP_PASSWORD_LABEL = "signup.password_label"
    const val SIGNUP_PASSWORD_HINT = "signup.password_hint"
    const val SIGNUP_VERIFY_OTP_BUTTON = "signup.verify_otp_button"
    const val SIGNUP_OR_DIVIDER = "signup.or_divider"
    const val SIGNUP_GOOGLE_SIGNIN = "signup.google_signin"
    const val SIGNUP_GOOGLE_LOGO_DESCRIPTION = "signup.google_logo_description"
    const val SIGNUP_ALREADY_HAVE_ACCOUNT = "signup.already_have_account"
    const val SIGNUP_LOGO_DESCRIPTION = "signup.logo_description"
    
    // User Details Screen
    const val USER_DETAILS_TITLE = "user_details.title"
    const val USER_DETAILS_SUBTITLE = "user_details.subtitle"
    const val USER_DETAILS_FIRST_NAME_LABEL = "user_details.first_name_label"
    const val USER_DETAILS_FIRST_NAME_HINT = "user_details.first_name_hint"
    const val USER_DETAILS_LAST_NAME_LABEL = "user_details.last_name_label"
    const val USER_DETAILS_LAST_NAME_HINT = "user_details.last_name_hint"
    const val USER_DETAILS_AGE_LABEL = "user_details.age_label"
    const val USER_DETAILS_AGE_HINT = "user_details.age_hint"
    const val USER_DETAILS_HEIGHT_LABEL = "user_details.height_label"
    const val USER_DETAILS_WEIGHT_LABEL = "user_details.weight_label"
    const val USER_DETAILS_GENDER_LABEL = "user_details.gender_label"
    const val USER_DETAILS_GENDER_MALE = "user_details.gender_male"
    const val USER_DETAILS_GENDER_FEMALE = "user_details.gender_female"
    const val USER_DETAILS_GENDER_OTHER = "user_details.gender_other"
    const val USER_DETAILS_BLOOD_TYPE_LABEL = "user_details.blood_type_label"
    const val USER_DETAILS_BLOOD_TYPE_A_POSITIVE = "user_details.blood_type_a_positive"
    const val USER_DETAILS_BLOOD_TYPE_A_NEGATIVE = "user_details.blood_type_a_negative"
    const val USER_DETAILS_BLOOD_TYPE_B_POSITIVE = "user_details.blood_type_b_positive"
    const val USER_DETAILS_BLOOD_TYPE_B_NEGATIVE = "user_details.blood_type_b_negative"
    const val USER_DETAILS_BLOOD_TYPE_AB_POSITIVE = "user_details.blood_type_ab_positive"
    const val USER_DETAILS_BLOOD_TYPE_AB_NEGATIVE = "user_details.blood_type_ab_negative"
    const val USER_DETAILS_BLOOD_TYPE_O_POSITIVE = "user_details.blood_type_o_positive"
    const val USER_DETAILS_BLOOD_TYPE_O_NEGATIVE = "user_details.blood_type_o_negative"
    const val USER_DETAILS_NEXT_BUTTON = "user_details.next_button"
    const val USER_DETAILS_PROFILE_TITLE = "user_details.profile_title"
    const val USER_DETAILS_LOGOUT_BUTTON = "user_details.logout_button"
    const val USER_DETAILS_USER_AVATAR_DESCRIPTION = "user_details.user_avatar_description"
    const val USER_DETAILS_DEFAULT_AVATAR_DESCRIPTION = "user_details.default_avatar_description"
    const val USER_DETAILS_USER_PROFILE_DESCRIPTION = "user_details.user_profile_description"
    
    // Common Labels
    const val COMMON_LOADING = "common.loading"
    const val COMMON_ERROR = "common.error"
    const val COMMON_SUCCESS = "common.success"
    const val COMMON_CANCEL = "common.cancel"
    const val COMMON_OK = "common.ok"
    const val COMMON_SAVE = "common.save"
    const val COMMON_EDIT = "common.edit"
    const val COMMON_DELETE = "common.delete"
    const val COMMON_BACK = "common.back"
    const val COMMON_NEXT = "common.next"
    const val COMMON_PREVIOUS = "common.previous"
    const val COMMON_DONE = "common.done"
    const val COMMON_CONTINUE = "common.continue"
    const val COMMON_SKIP = "common.skip"
    const val COMMON_RETRY = "common.retry"
    const val COMMON_CLOSE = "common.close"
    
    // Validation Messages
    const val VALIDATION_REQUIRED_FIELD = "validation.required_field"
    const val VALIDATION_INVALID_EMAIL = "validation.invalid_email"
    const val VALIDATION_INVALID_PHONE = "validation.invalid_phone"
    const val VALIDATION_PASSWORD_TOO_SHORT = "validation.password_too_short"
    const val VALIDATION_PASSWORD_WEAK = "validation.password_weak"
    const val VALIDATION_NAME_TOO_SHORT = "validation.name_too_short"
    const val VALIDATION_AGE_INVALID = "validation.age_invalid"
    const val VALIDATION_HEIGHT_INVALID = "validation.height_invalid"
    const val VALIDATION_WEIGHT_INVALID = "validation.weight_invalid"
    
    // Error Messages
    const val ERROR_NETWORK = "errors.network_error"
    const val ERROR_SERVER = "errors.server_error"
    const val ERROR_AUTHENTICATION_FAILED = "errors.authentication_failed"
    const val ERROR_INVALID_CREDENTIALS = "errors.invalid_credentials"
    const val ERROR_USER_NOT_FOUND = "errors.user_not_found"
    const val ERROR_EMAIL_ALREADY_EXISTS = "errors.email_already_exists"
    const val ERROR_PHONE_ALREADY_EXISTS = "errors.phone_already_exists"
    const val ERROR_OTP_EXPIRED = "errors.otp_expired"
    const val ERROR_OTP_INVALID = "errors.otp_invalid"
    const val ERROR_SESSION_EXPIRED = "errors.session_expired"
    
    // Success Messages
    const val SUCCESS_LOGIN = "success.login_successful"
    const val SUCCESS_SIGNUP = "success.signup_successful"
    const val SUCCESS_PROFILE_UPDATED = "success.profile_updated"
    const val SUCCESS_OTP_SENT = "success.otp_sent"
    const val SUCCESS_OTP_VERIFIED = "success.otp_verified"
}

/**
 * Composable function to get localized string
 */
@Composable
fun getString(key: String, vararg args: Any): String {
    val context = LocalContext.current
    val localizationManager = remember { LocalizationManager(context) }
    return localizationManager.getString(key, *args)
}

/**
 * Composable function to get localized string with StringResource constants
 */
@Composable
fun getStringResource(key: String, vararg args: Any): String {
    return getString(key, *args)
}
