package dev.sijanrijal.note.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.sijanrijal.note.models.Note
import dev.sijanrijal.note.toString
import timber.log.Timber
import java.util.*

class UpdateFragmentViewModel : ViewModel() {
    private lateinit var databaseRef: CollectionReference

    init {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            databaseRef = FirebaseFirestore.getInstance().collection("users")
                .document(user.uid).collection("notes")
        }
    }

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean>
        get() = _isSuccessful

    fun addNote(note: Note) {
        note.note_id  = databaseRef.document().id
        databaseRef.document(note.note_id)
            .set(note)
            .addOnSuccessListener {
                Timber.d("Note successfully added document value ${note.note_id}")
                _isSuccessful.value = true
            }
            .addOnFailureListener {
                Timber.d("Failed to add notes $it")
                _isSuccessful.value = false
            }
    }

    fun updateNote(
        noteTitle: String,
        noteDescription: String,
        createdDate: Date,
        noteId : String,
        updatedNote: Note) {
        if (noteTitle.equals(updatedNote.note_title, false)
            && noteDescription.equals(updatedNote.description, false)) {
            _isSuccessful.value = true
        } else {
            Timber.d("Document id: $noteId")
            databaseRef.document(noteId)
                .update("note_title", updatedNote.note_title,
                "description", updatedNote.description,
                "created_date", createdDate.toString("MM-dd-yyyy"),
                "last_modified", updatedNote.last_modified)
                .addOnSuccessListener {
                    _isSuccessful.value = true
                }
        }
    }

}
