package dev.sijanrijal.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dev.sijanrijal.note.checkUserName
import dev.sijanrijal.note.databinding.FragmentFirstSignInBinding
import timber.log.Timber

class FirstSignInFragment : Fragment() {

    private lateinit var binding: FragmentFirstSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstSignInBinding.inflate(
            inflater, container, false
        )

        // checks the validity of first and last name and if it passes, it adds the user to the
        // firestore database and navigates the user to the home fragment
        binding.doneButton.setOnClickListener {
            val firstName = binding.firstName.editText!!.text.toString()
            val lastName = binding.lastName.editText!!.text.toString()
            val isValid = checkUserName(firstName, lastName)
            if (isValid) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.let { user ->
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build()
                    user.updateProfile(profileUpdate)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                addUserToDatabase()
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

    /**
     * If the user authentication is successful, add the user to the firestore database and
     * navigate the user to the home fragment
     * **/
    fun addUserToDatabase(): Boolean {
        var isAdded = false
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val user = hashMapOf(
                "first_name" to it.displayName!!.substringBefore(" "),
                "last_name" to it.displayName!!.substringAfter(" "),
                "userId" to it.uid
            )
            FirebaseFirestore.getInstance().collection("users")
                .document(it.uid)
                .set(user)
                .addOnSuccessListener {
                    Timber.d("User document created")
                    findNavController().navigate(FirstSignInFragmentDirections.actionFirstSignInFragmentToHomeFragment())

                }
                .addOnFailureListener {
                    Timber.e("Error writing user document $it")
                }
        }
        return isAdded
    }
}
