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
import dev.sijanrijal.note.CHECK_INBOX_VERIFICATION
import dev.sijanrijal.note.databinding.FragmentLoginBinding
import dev.sijanrijal.note.viewmodels.LoginFragmentViewModel
import timber.log.Timber

class LoginFragment : Fragment() {


    private val viewModel: LoginFragmentViewModel = LoginFragmentViewModel()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val auth = FirebaseAuth.getInstance()

        viewModel.setupFirebaseAuthListener()

        auth.currentUser?.let {
            Timber.d("Already signed in")
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }

        binding.loginButton.setOnClickListener {
            viewModel.onLoginButtonClicked(binding.emailTextInput.editText?.text.toString(),
                binding.passwordTextInput.editText?.text.toString())
        }

        viewModel.isSignInSuccessful.observe(this, Observer { isSignInSuccessful ->
            if (isSignInSuccessful) {
                if(viewModel.isNewUser) {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFirstSignInFragment())
                } else {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                }
            } else {
                if(viewModel.errorMessage.equals(CHECK_INBOX_VERIFICATION)) {
                    binding.emailTextInput.error = null
                    binding.passwordTextInput.error = null
                } else {
                    binding.emailTextInput.error = viewModel.errorMessage
                    binding.passwordTextInput.error = viewModel.errorMessage
                }
            }
        })

        binding.signupButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment2())
        }

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(viewModel.mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(viewModel.mAuthListener)
    }
}