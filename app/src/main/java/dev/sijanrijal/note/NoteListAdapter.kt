package dev.sijanrijal.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sijanrijal.note.databinding.NotesListLayoutBinding
import dev.sijanrijal.note.models.Note
import kotlinx.android.synthetic.main.user_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.NotNull
import java.lang.ClassCastException
import java.util.*


private const val ITEM_HEADER = 0
private const val ITEM_NOTE = 1
class NoteListAdapter(val noteClickListener: NoteClickListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(NoteListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_NOTE -> NoteListViewHolder.from(parent)
            ITEM_HEADER -> UserViewHolder.from(parent)
            else -> throw ClassCastException("Unknow viewtype $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_HEADER
            is DataItem.NoteItem -> ITEM_NOTE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is NoteListViewHolder -> {
                val note = getItem(position) as DataItem.NoteItem
                holder.bind(noteClickListener, note.note)
            }
            is UserViewHolder -> {
                val userName = getItem(position) as DataItem.Header
                holder.bind(userName.userName)

            }
        }
    }

    fun addHeaderAndNoteList(list: List<Note>, username: String) {
        adapterScope.launch {
            val items = when(list) {
                null -> listOf(DataItem.Header(username))
                else -> listOf(DataItem.Header(username)) + list.map { DataItem.NoteItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class NoteListViewHolder private constructor(val binding: @NotNull NotesListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

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

    class UserViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        fun bind(userName: String) {
            view.header_title.text = "Here are your notes $userName"
        }
        companion object {
            fun from(parent: ViewGroup) : UserViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_header, parent, false)
                return UserViewHolder(view)
            }
        }
    }
}

class NoteListDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

}

class NoteClickListener(val clickListener : (noteTitle: String, noteContent: String, createdDate: Date, noteId: String) -> Unit) {
    fun onClick(note: Note) = clickListener(note.note_title, note.description, note.created_date, note.note_id)
}

sealed class DataItem {
    data class NoteItem(val note : Note) : DataItem() {
        override val noteId = note.note_id
    }

    data class Header(val userName: String) : DataItem() {
        override val noteId: String = "0"
    }

    abstract val noteId : String
}
