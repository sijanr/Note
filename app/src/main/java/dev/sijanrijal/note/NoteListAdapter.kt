package dev.sijanrijal.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.sijanrijal.note.databinding.NotesListLayoutBinding
import dev.sijanrijal.note.models.Note

class NoteListAdapter : RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder>() {

    var list = mutableListOf<Note>(
        Note("Weekend", "Today was a good weekend", "Jan 2020"),
        Note("Monday", "Classes in school", "Jan 2020"),
        Note("Tuesday", "Break from school", "Jan 2020")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.notes_list_layout, parent, false
        ) as NotesListLayoutBinding
        return NoteListViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        holder.setData(position)
    }

    inner class NoteListViewHolder constructor(private val binding: NotesListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(position: Int) {
            binding.apply {
                notesTitle.text = list[position].title
                notesTimestamp.text = list[position].timeStamp
            }
        }
    }
}