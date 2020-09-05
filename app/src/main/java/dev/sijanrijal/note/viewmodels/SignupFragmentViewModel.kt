package dev.sijanrijal.note.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dev.sijanrijal.note.AUTHENTICATION_ERROR
import dev.sijanrijal.note.VALIDITY_FAIL
import dev.sijanrijal.note.VERIFY_MESSAGE_ERROR
import dev.sijanrijal.note.checkEmailPasswordValidity
import dev.sijanrijal.note.ui.SignupFragmentDirections
import timber.log.Timber

class SignupFragmentViewModel : ViewModel() {

    private val _isSignUpSuccessful = MutableLiveData<Boolean>()
    val isSignUpSuccessful : LiveData<Boolean>
        get() = _isSignUpSuccessful

    var errorMessage = ""

    fun onSignUpClicked(email : String?, password : String?) {
        if(checkEmailPasswordValidity(email, password)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Timber.d("onComplete: AuthState: ${FirebaseAuth.getInstance().currentUser?.uid}")
                        sendVerificationEmail()
                        FirebaseAuth.getInstance().signOut()
                    } else {
                        Timber.d("onComplete : task not successful")
                        errorMessage = AUTHENTICATION_ERROR
                        _isSignUpSuccessful.value = false
                    }
                }
        } else {
            errorMessage = VALIDITY_FAIL
            _isSignUpSuccessful.value = false
        }
    }

    private fun sendVerificationEmail() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let { user ->
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    _isSignUpSuccessful.value = task.isSuccessful
                    if(!task.isSuccessful) {
                        errorMessage = VERIFY_MESSAGE_ERROR
                        _isSignUpSuccessful.value = false
                    }
                }
        }
    }
}