package dev.sijanrijal.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dev.sijanrijal.note.checkUserName
import dev.sijanrijal.note.databinding.FragmentFirstSignInBinding

class FirstSignInFragment : Fragment() {

    private lateinit var binding : FragmentFirstSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstSignInBinding.inflate(
            inflater, container, false)

        binding.doneButton.setOnClickListener {
            val firstName = binding.firstName.editText!!.text.toString()
            val lastName = binding.lastName.editText!!.text.toString()
            val isValid = checkUserName(firstName, lastName)
            if(isValid) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {user ->
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build()
                    user.updateProfile(profileUpdate)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                findNavController().navigate(FirstSignInFragmentDirections.actionFirstSignInFragmentToHomeFragment())
                            }
                        }
                }
            } else {
                binding.firstName.error = "Provide a valid name"
                binding.lastName.error = "Provide a valid name"
            }
        }
        return binding.root
    }
}