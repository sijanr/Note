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

    private lateinit var viewModel : HomeFragmentViewModel

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
        binding.lifecycleOwner =this
        setHasOptionsMenu(true)
        //        set the recycler view
        val adapter = NoteListAdapter(NoteClickListener {noteTitle, noteContent, date, noteId ->
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(
                noteTitle, noteContent, date, noteId
            ))
            viewModel.onNavigation()
        })
        val itemDecorator = ItemDecorator()
        binding.notesRecyclerView.adapter = adapter
        binding.notesRecyclerView.addItemDecoration(itemDecorator)
        viewModel.isDatabaseChanged.observe(viewLifecycleOwner, Observer { isDatabaseChanged ->
            if(isDatabaseChanged) {
                viewModel.readyAllNotes()
            }
        })

        viewModel.isDatabaseReady.observe(this, Observer { isDatabaseReady ->
            if(isDatabaseReady) {
                val userName = FirebaseAuth.getInstance().currentUser!!.displayName?.substringBefore(" ") ?: " "
                adapter.addHeaderAndNoteList(viewModel.notesList, userName)
            }
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment("", "", Date(), ""))
            viewModel.onNavigation()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout_menu -> {
                logoutUser()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.removeListener()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
    }
}