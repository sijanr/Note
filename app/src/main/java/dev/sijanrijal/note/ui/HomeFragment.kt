package dev.sijanrijal.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dev.sijanrijal.note.NoteListAdapter
import dev.sijanrijal.note.R
import dev.sijanrijal.note.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            binding.userNameText.text = it.displayName ?: "Null"
        }

        //set the recycler view
//        val adapter = NoteListAdapter()
//        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//        binding.notesRecyclerView.adapter = adapter
//        binding.notesRecyclerView.layoutManager = layoutManager

        return binding.root
    }



}