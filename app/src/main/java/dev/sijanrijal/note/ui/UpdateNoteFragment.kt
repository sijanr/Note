package dev.sijanrijal.note.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.sijanrijal.note.databinding.FragmentAddNoteBinding
import dev.sijanrijal.note.models.Note
import dev.sijanrijal.note.viewmodels.UpdateFragmentViewModel
import timber.log.Timber

class UpdateNoteFragment : Fragment() {

    private lateinit var binding: FragmentAddNoteBinding
    private lateinit var viewModel: UpdateFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNoteBinding.inflate(
            inflater, container, false
        )

        viewModel = ViewModelProvider(this).get(UpdateFragmentViewModel::class.java)

        //get an argument from the bundle if the user is trying to update the note
        val argument = UpdateNoteFragmentArgs.fromBundle(requireArguments())
        var isUpdate = false
        if (argument.noteTitle.isNotEmpty() && argument.description.isNotEmpty()) {
            binding.editTextTitle.text.append(argument.noteTitle)
            binding.description.text.append(argument.description)
            isUpdate = true
        }

        //update/create a note in the firestore database
        binding.fab.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.description.text.toString().trim()
            binding.editTextTitle.clearFocus()
            binding.description.clearFocus()
            //create a new note
            if (!isUpdate) {
                viewModel.addNote(
                    Note(
                        note_title = title,
                        description = description
                    )
                )
            }

            //update an existing note
            else {
                viewModel.updateNote(
                    argument.noteTitle, argument.description, argument.createdDate, argument.noteId,
                    Note(
                        note_title = title,
                        created_date = argument.createdDate,
                        description = description
                    )
                )
            }
        }

        //if the note was added/updated successfuly, take the user to the home fragment to see the
        //list of user's notes
        viewModel.isSuccessful.observe(viewLifecycleOwner, Observer {
            if (it) {

                findNavController().navigate(UpdateNoteFragmentDirections.actionUpdateNoteFragmentToHomeFragment())
            } else {
                Timber.d("Add not successful")
            }
        })

        return binding.root
    }
}