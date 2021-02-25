package dev.sijanrijal.note.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dev.sijanrijal.note.CHECK_INBOX_VERIFICATION
import dev.sijanrijal.note.R
import dev.sijanrijal.note.databinding.FragmentLoginBinding
import dev.sijanrijal.note.viewmodels.LoginFragmentViewModel
import timber.log.Timber

class LoginFragment : Fragment() {


    companion object {
        private const val RC_SIGN_IN = 1
    }
    private lateinit var viewModel: LoginFragmentViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        Timber.d("Here at Login fragment")

        //get firebase authentication instance
        auth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this).get(LoginFragmentViewModel::class.java)

        //navigate the user to the home screen if the user is a logged in user
        auth.currentUser?.let {
            Timber.d("Already signed in")
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.googleSignIn.setSize(SignInButton.SIZE_STANDARD)
        googleSignInListener()

        binding.loginButton.setOnClickListener {
            viewModel.onLoginButtonClicked(
                binding.emailTextInput.editText?.text.toString(),
                binding.passwordTextInput.editText?.text.toString()
            )
        }

        // navigate the user to the home fragment if login is successful and the user is a verified
        // user or display an error message if there were any issues with authentication
        viewModel.isSignInSuccessful.observe(viewLifecycleOwner, { isSignInSuccessful ->
            if (isSignInSuccessful) {
                if (viewModel.isNewUser) {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFirstSignInFragment())
                } else {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                }
            } else {
                if (viewModel.errorMessage == CHECK_INBOX_VERIFICATION) {
                    displayErrorMessage(null)
                } else {
                    displayErrorMessage(viewModel.errorMessage)
                }
            }
        })

        // navigate the user to the sign up fragment, if the user is an unregistered user
        binding.signupButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment2())
        }

        return binding.root
    }

    private fun googleSignInListener() {
        binding.googleSignIn.setOnClickListener {
            val signinIntent = googleSignInClient.signInIntent
            startActivityForResult(signinIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!.idToken!!)
            } catch (e : ApiException) {
                Toast.makeText(requireContext(), "Failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayErrorMessage(errorMessage: String?) {
        errorMessage?.let { message ->
            binding.emailTextInput.error = message
            binding.passwordTextInput.error = message
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user!!.metadata!!.lastSignInTimestamp == user.metadata!!.creationTimestamp) {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFirstSignInFragment())
                    } else {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    }
                }
            }
    }

}