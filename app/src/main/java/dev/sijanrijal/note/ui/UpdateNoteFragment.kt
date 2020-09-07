package dev.sijanrijal.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.sijanrijal.note.databinding.FragmentAddNoteBinding
import dev.sijanrijal.note.models.Note
import dev.sijanrijal.note.viewmodels.UpdateFragmentViewModel
import timber.log.Timber

class UpdateNoteFragment  : Fragment() {

    private lateinit var binding : FragmentAddNoteBinding
    private val viewModel : UpdateFragmentViewModel = UpdateFragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNoteBinding.inflate(
            inflater, container, false)

        val argument =  UpdateNoteFragmentArgs.fromBundle(requireArguments())
        var isUpdate = false
        if(argument.noteTitle.isNotEmpty() && argument.description.isNotEmpty()) {
            binding.editTextTitle.text.append(argument.noteTitle)
            binding.description.text.append(argument.description)
            isUpdate = true
        }

        binding.fab.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.description.text.toString().trim()
            if(!isUpdate) {
                viewModel.addNote(Note(note_title = title,
                    description = description))
            } else {
                viewModel.updateNote(argument.noteTitle, argument.description, argument.createdDate, argument.noteId,
                    Note(note_title = title, created_date = argument.createdDate, description = description))
            }
        }

        viewModel.isSuccessful.observe(this, Observer {
            if(it) {
                findNavController().navigate(UpdateNoteFragmentDirections.actionUpdateNoteFragmentToHomeFragment())
            } else {
                Timber.d("Add not successful")
            }
        })

        return binding.root
    }
}