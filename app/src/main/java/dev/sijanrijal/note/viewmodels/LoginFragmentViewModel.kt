package dev.sijanrijal.note.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dev.sijanrijal.note.AUTHENTICATION_ERROR
import dev.sijanrijal.note.CHECK_INBOX_VERIFICATION
import dev.sijanrijal.note.VALIDITY_FAIL
import dev.sijanrijal.note.checkEmailPasswordValidity
import timber.log.Timber

class LoginFragmentViewModel : ViewModel() {

    private val _isSignInSuccessful = MutableLiveData<Boolean>()
    val isSignInSuccessful: LiveData<Boolean>
        get() = _isSignInSuccessful

    var errorMessage = ""

    var isNewUser = false

    /**
     * Checks for errors in user's email and password and notifies the UI whether the authentication
     * was successful or not
     * **/
    fun onLoginButtonClicked(email: String?, password: String?) {
        if (checkEmailPasswordValidity(email, password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkUserLoginVerification()
                    } else {
                        errorMessage = AUTHENTICATION_ERROR
                        Timber.d("Authentication failed: $errorMessage")
                        _isSignInSuccessful.value = false
                    }
                }

        } else {
            errorMessage = VALIDITY_FAIL
            _isSignInSuccessful.value = false
        }
    }


    /**
     * Checks whether the user is a registered user and if so, performs an additional check to
     * verify if the user has verfied the registered email
     * **/
    fun checkUserLoginVerification() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            if (!user.isEmailVerified) {
                errorMessage = CHECK_INBOX_VERIFICATION
                FirebaseAuth.getInstance().signOut()
            } else {
                isNewUser = user.metadata!!.lastSignInTimestamp == user.metadata!!.creationTimestamp
                Timber.d("isNewUser: $isNewUser")
            }
            _isSignInSuccessful.value = user.isEmailVerified
        }
    }

}
