package dev.sijanrijal.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.sijanrijal.note.R
import dev.sijanrijal.note.databinding.FragmentLoginBinding
import dev.sijanrijal.note.viewmodels.SignupFragmentViewModel

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: SignupFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        init()

        viewModel = ViewModelProvider(this).get(SignupFragmentViewModel::class.java)

        //listen to whether the signup process was successful or not
        // if it was successful navigate the user to the login screen
        viewModel.isSignUpSuccessful.observe(viewLifecycleOwner,  { isSignUpSuccessful ->
            if (isSignUpSuccessful) {
                makeToast("Signup successful. Verify your email before you login")
                findNavController().navigateUp()
            } else if (!isSignUpSuccessful) {
                binding.emailTextInput.error = viewModel.errorMessage
                binding.passwordTextInput.error = viewModel.errorMessage
                makeToast(viewModel.errorMessage)
            }
        })

        return binding.root
    }

    private fun init() {
        activity?.actionBar?.hide()
        binding.googleSignIn.visibility = View.GONE
        binding.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow_500))
        binding.viewBottom.setBackgroundResource(R.drawable.login_top_background_signup)
        binding.heading.text = getString(R.string.create_account)
        binding.signupButton.visibility = View.GONE
        binding.loginButton.text = getString(R.string.sign_up_label)
        binding.loginButton.setOnClickListener {
            onSignUpSelected()
        }
        binding.orTextview.visibility = View.GONE
        binding.underlineLeft.visibility = View.GONE
        binding.underlineRight.visibility = View.GONE
    }

    /**
     * Checks the authentication status once the user clicks the sign up button
     * **/
    private fun onSignUpSelected() {
        val email = binding.emailTextInput.editText?.text.toString()
        val password = binding.passwordTextInput.editText?.text.toString()
        viewModel.onSignUpClicked(email, password)
    }

    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
            .show()
    }




}