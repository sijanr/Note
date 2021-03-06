package dev.sijanrijal.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sijanrijal.note.databinding.NotesListLayoutBinding
import dev.sijanrijal.note.models.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.NotNull
import java.lang.ClassCastException
import java.util.*


private const val ITEM_HEADER = 0
private const val ITEM_NOTE = 1

class NoteListAdapter(val noteClickListener: NoteClickListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(NoteListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Main)

    /**
     * Create viewholders to display notes and header of the notes
     * **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_NOTE -> NoteListViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewtype $viewType")
        }
    }

    /**
     * Get the view type to display the correct view in recyclerview
     * **/
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.NoteItem -> ITEM_NOTE
        }
    }


    /**
     * Get the correct data item for a viewholder in the position
     * **/
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteListViewHolder -> {
                val note = getItem(position) as DataItem.NoteItem
                holder.bind(noteClickListener, note.note)
            }
        }
    }

    /**
     * Adds the user name to be displayed as a header and list of notes
     * **/
    fun addHeaderAndNoteList(list: List<Note>) {
        adapterScope.launch {
            val items = list.map { DataItem.NoteItem(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    /**
     * ViewHolder class that holds the views that display notes
     * **/
    class NoteListViewHolder private constructor(val binding: @NotNull NotesListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            noteClickListener: NoteClickListener,
            item: Note
        ) {
            binding.note = item
            binding.noteListClick = noteClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): NoteListViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = NotesListLayoutBinding
                    .inflate(inflater, parent, false)
                return NoteListViewHolder(binding)
            }
        }
    }


}

/**
 * DiffUtil Callback to handle the changes in notes list
 * **/
class NoteListDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

}

/**
 * Class that handles click listener for recycler view items
 * **/
class NoteClickListener(val clickListener: (noteTitle: String, noteContent: String, createdDate: Date, noteId: String) -> Unit) {
    fun onClick(note: Note) =
        clickListener(note.note_title, note.description, note.created_date, note.note_id)
}

/**
 * Class that holds the type of items in the recycler view
 * **/
sealed class DataItem {
    data class NoteItem(val note: Note) : DataItem() {
        override val noteId = note.note_id
    }


    abstract val noteId: String
}
