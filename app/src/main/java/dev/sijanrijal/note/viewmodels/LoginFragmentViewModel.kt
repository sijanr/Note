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

    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    private val _isSignInSuccessful = MutableLiveData<Boolean>()
    val isSignInSuccessful: LiveData<Boolean>
        get() = _isSignInSuccessful

    var errorMessage = ""

    var isNewUser = false

    fun onLoginButtonClicked(email: String?, password: String?) {
        if (checkEmailPasswordValidity(email, password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->

                }
                .addOnFailureListener { exception ->
                    Timber.d("Authentication failed: ${exception.message}")
                    errorMessage = AUTHENTICATION_ERROR
                    _isSignInSuccessful.value = false
                }
        } else {
            errorMessage = VALIDITY_FAIL
            _isSignInSuccessful.value = false
        }
    }


    fun setupFirebaseAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            user?.let {
                Timber.d("onAuthStateChanged: signed in ${it.uid}")
                if(!it.isEmailVerified) {
                    errorMessage = CHECK_INBOX_VERIFICATION
                    FirebaseAuth.getInstance().signOut()
                } else {
                    isNewUser = it.metadata!!.lastSignInTimestamp == it.metadata!!.creationTimestamp
                    Timber.d("isNewUser: $isNewUser")
                }
                _isSignInSuccessful.value = it.isEmailVerified
            }
        }
    }

}
