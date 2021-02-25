package dev.sijanrijal.note

import java.text.SimpleDateFormat
import java.util.*

//minimum password length
private const val PASSWORD_MIN_LENGTH = 10

//email verification failed message
const val VERIFY_MESSAGE_ERROR = "Failed to verify email"

//authentication error message
const val AUTHENTICATION_ERROR = "Failed to sign in"

const val ACCOUNT_SIGNUP_FAIL = "Failed to create an account"

//user's email or password validity error message
const val VALIDITY_FAIL = "Enter a valid email and a password that is at least 10 digits"

const val CHECK_INBOX_VERIFICATION = "Check your inbox to verify your email"

/**
 * Checks the user's email and password length
 * **/
fun checkEmailPasswordValidity(email: String?, password: String?): Boolean {
    val emailStringLength = email?.length ?: -1
    val passwordLength = password?.length ?: -1
    return (emailStringLength > 10 && passwordLength >= PASSWORD_MIN_LENGTH)
}

/**
 * Verify that the user's first and last name isn't null and does not contain special characters
 * and numbers
 * **/
fun checkUserName(firstName: String?, lastName: String?): Boolean {
    val firstNameCheck = firstName?.filter { it.isLetter() } ?: ""
    val lastNameCheck = lastName?.filter { it.isLetter() } ?: ""

    val firstNameLength = firstName?.length ?: -1
    val lastNameLength = lastName?.length ?: -1
    return ((firstNameCheck.isNotEmpty()) && (lastNameCheck.isNotEmpty())
            && (firstNameLength == firstNameCheck.length) && (lastNameCheck.length == lastNameLength))

}

/**
 * Convert a Date object to a given format
 * **/
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}