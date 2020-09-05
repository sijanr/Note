package dev.sijanrijal.note


private const val PASSWORD_MIN_LENGTH = 10
const val VERIFY_MESSAGE_ERROR = "Failed to verify email"
const val AUTHENTICATION_ERROR = "Account creation failed"
const val VALIDITY_FAIL = "Enter a valid email and a 10 digit password"
const val CHECK_INBOX_VERIFICATION = "Check your inbox to verify your email"

fun checkEmailPasswordValidity(email: String?, password: String?) : Boolean {
    var isSuccessful = false
    val emailStringLength = email?.length ?: -1
    val passwordLength = password?.length ?: -1
    return (emailStringLength > 10 && passwordLength >= PASSWORD_MIN_LENGTH)
}

fun checkUserName(firstName : String?, lastName: String?) : Boolean {
    val firstNameCheck = firstName?.filter { it.isLetter() } ?: ""
    val lastNameCheck = lastName?.filter { it.isLetter() } ?: ""

    val firstNameLength = firstName?.length ?: -1
    val lastNameLength = lastName?.length ?: -1
    return((firstNameCheck.isNotEmpty()) && (lastNameCheck.isNotEmpty())
        && (firstNameLength == firstNameCheck.length) && (lastNameCheck.length == lastNameLength))

}