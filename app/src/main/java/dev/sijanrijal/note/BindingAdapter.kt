package dev.sijanrijal.note

import android.widget.TextView
import androidx.databinding.BindingAdapter
import dev.sijanrijal.note.models.Note
import java.util.*

/**
 * Binding adapters for the home fragment to display the note title and its creation date
 * **/

@BindingAdapter("dateFormat")
fun TextView.formatDate(note: Note) {
    text = note.created_date.toString("MM-dd-yyyy")
}

@BindingAdapter("noteTitle")
fun TextView.noteTitle(note: Note) {
    text = note.note_title
}