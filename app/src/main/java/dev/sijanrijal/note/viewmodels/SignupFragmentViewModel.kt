package dev.sijanrijal.note.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dev.sijanrijal.note.*
import timber.log.Timber

class SignupFragmentViewModel : ViewModel() {

    private val _isSignUpSuccessful = MutableLiveData<Boolean>()
    val isSignUpSuccessful: LiveData<Boolean>
        get() = _isSignUpSuccessful

    var errorMessage = ""

    /**
     * Authenticate a new user sign up process and if the authentication is successful, send a
     * verification email to the user. If there was an issue validating user's email or password
     * display the error message
     * **/
    fun onSignUpClicked(email: String?, password: String?) {
        if (checkEmailPasswordValidity(email, password)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("onComplete: AuthState: ${FirebaseAuth.getInstance().currentUser?.uid}")
                        sendVerificationEmail()
                        FirebaseAuth.getInstance().signOut()
                    } else {
                        Timber.d("onComplete : task not successful")
                        errorMessage = ACCOUNT_SIGNUP_FAIL
                        _isSignUpSuccessful.value = false
                    }
                }
        } else {
            errorMessage = VALIDITY_FAIL
            _isSignUpSuccessful.value = false
        }
    }

    /**
     * Send a verification email to the new registered user
     * **/
    private fun sendVerificationEmail() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let { user ->
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    _isSignUpSuccessful.value = task.isSuccessful
                    if (!task.isSuccessful) {
                        errorMessage = VERIFY_MESSAGE_ERROR
                        _isSignUpSuccessful.value = false
                    }
                }
        }
    }
}