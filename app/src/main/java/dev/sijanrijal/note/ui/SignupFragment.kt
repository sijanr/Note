package dev.sijanrijal.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dev.sijanrijal.note.R
import dev.sijanrijal.note.checkEmailPasswordValidity
import dev.sijanrijal.note.databinding.FragmentLoginBinding
import dev.sijanrijal.note.viewmodels.SignupFragmentViewModel
import timber.log.Timber

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel = SignupFragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.createAccountHeader.visibility = View.GONE
        binding.signupButton.visibility = View.GONE
        binding.loginButton.text = getString(R.string.sign_up_label)
        binding.loginButton.setOnClickListener {
            onSignUpSelected()
        }

        //listen to whether the signup process was successful or not
        // if it was successful navigate the user to the login screen
        viewModel.isSignUpSuccessful.observe(viewLifecycleOwner, Observer { isSignUpSuccessful ->
            if(isSignUpSuccessful) {
                makeToast("Signup successful. Verify your email before you login")
                findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
            } else if (!isSignUpSuccessful) {
                binding.emailTextInput.error = viewModel.errorMessage
                binding.passwordTextInput.error = viewModel.errorMessage
                makeToast(viewModel.errorMessage)
            }
        })

        return binding.root
    }

    /**
     * Checks the authentication status once the user clicks the sign up button
     * **/
    private fun onSignUpSelected() {
        val email = binding.emailTextInput.editText?.text.toString()
        val password = binding.passwordTextInput.editText?.text.toString()
        viewModel.onSignUpClicked(email, password)
    }

    private fun makeToast(message : String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
            .show()
    }
}