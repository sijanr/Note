package dev.sijanrijal.note.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = HomeFragmentViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        // sets the click listener in recycler view so that if the user taps in a note, it will
        // take the user to Update Note Fragment so that the user can read/update the note
        val adapter = NoteListAdapter(NoteClickListener { noteTitle, noteContent, date, noteId ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(
                    noteTitle, noteContent, date, noteId
                )
            )
            viewModel.onNavigation()
        })
        val itemDecorator = ItemDecorator()
        binding.notesRecyclerView.adapter = adapter
        binding.notesRecyclerView.addItemDecoration(itemDecorator)

        //if there is an update to the database, update the recycler view as well
        viewModel.isDatabaseChanged.observe(viewLifecycleOwner, Observer { isDatabaseChanged ->
            if (isDatabaseChanged) {
                viewModel.readyAllNotes()
            }
        })

        //if the database is ready, display the notes in the recycler view
        viewModel.isDatabaseReady.observe(viewLifecycleOwner, Observer { isDatabaseReady ->
            if (isDatabaseReady) {
                val userName =
                    FirebaseAuth.getInstance().currentUser!!.displayName?.substringBefore(" ")
                        ?: " "
                adapter.addHeaderAndNoteList(viewModel.notesList, userName)
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
            viewModel.onNavigation()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    /**
     * Logs the user out of the application if the user selects logout from the menu
     * **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_menu -> {
                logoutUser()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Remove the firestore listener when the fragment is destroyed
     * **/
    override fun onDestroy() {
        super.onDestroy()
        viewModel.removeListener()
    }

    /**
     * Log the user out of the application
     * **/
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
    }
}