package dev.sijanrijal.note.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dev.sijanrijal.note.ItemDecorator
import dev.sijanrijal.note.NoteClickListener
import dev.sijanrijal.note.NoteListAdapter
import dev.sijanrijal.note.R
import dev.sijanrijal.note.databinding.FragmentHomeBinding
import dev.sijanrijal.note.viewmodels.HomeFragmentViewModel
import timber.log.Timber
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        topAppBarMenuItemClickListener()

        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)

        // sets the click listener in recycler view so that if the user taps in a note, it will
        // take the user to Update Note Fragment so that the user can read/update the note
        adapter = NoteListAdapter(NoteClickListener { noteTitle, noteContent, date, noteId->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(
                    noteTitle, noteContent, date, noteId
                )
            )
        })

        val itemDecorator = ItemDecorator()
        binding.notesRecyclerView.adapter = adapter
        binding.notesRecyclerView.addItemDecoration(itemDecorator)
        addSwipeToDelete()

        //if there is an update to the database, update the recycler view as well
        viewModel.isDatabaseChanged.observe(viewLifecycleOwner, { isDatabaseChanged ->
            if (isDatabaseChanged) {
                adapter.addHeaderAndNoteList(viewModel.notesList, FirebaseAuth.getInstance().currentUser!!.displayName?.substringBefore(" ") ?: " ")
            }
        })

        // click listener to naviagate user to update note fragment to create a new note
        binding.fab.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(
                    "",
                    "",
                    Date(),
                    ""
                )
            )
        }
        return binding.root
    }

    private fun topAppBarMenuItemClickListener() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
           when (menuItem.itemId) {
                R.id.logout_menu -> {
                    logoutUser()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                    true
                }
               else -> false
            }
        }
    }

    /**
     * Log the user out of the application
     * **/
    private fun logoutUser() {
        Timber.d("Logging out user")
        FirebaseAuth.getInstance().signOut()
    }


    private fun addSwipeToDelete() {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = viewModel.notesList[position - 1]
                Timber.d("Note $note")
                viewModel.deleteNote(note)
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerView)
    }
}