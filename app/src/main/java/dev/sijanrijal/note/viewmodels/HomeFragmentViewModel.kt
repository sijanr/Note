package dev.sijanrijal.note.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import dev.sijanrijal.note.models.Note
import timber.log.Timber

class HomeFragmentViewModel : ViewModel() {

    //reference to the document collection where the user's notes are stored
    private var databaseRef: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid).collection("notes")

    private val _isDatabaseChanged = MutableLiveData<Boolean>()
    val isDatabaseChanged: LiveData<Boolean>
        get() = _isDatabaseChanged

    //listener to the firestore database
    private val listenerRegistration: ListenerRegistration =
        databaseRef.addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                Timber.d("Listen failed $error")
                return@addSnapshotListener
            }
            Timber.d("Listen successful")
            _isDatabaseChanged.value = true
        }

    val notesList = ArrayList<Note>()

    /**
     * Gets the notes of the user from the firestore database
     * **/
    fun readyAllNotes() {
        notesList.clear()
        databaseRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val element = Note(
                        note_title = document.get("note_title").toString(),
                        description = document.getString("description") ?: "",
                        note_id = document.getString("note_id") ?: ""
                    )
                    notesList.add(element)
                    Timber.d("Notes Added $element")
                }
            }
            .addOnFailureListener { exception ->
                Timber.d("Failed to retrieve documents")
            }
    }

    /**
     * Delete a note from the database
     * **/
    fun deleteNote(note: Note) {
        databaseRef.document(note.note_id)
            .delete()
            .addOnSuccessListener {
                Timber.d("Note deleted")
            }
            .addOnFailureListener {exception ->
                Timber.d("Failed to delete note $exception")
            }
    }


    /**
     * Remove firestore listener
     * **/
    fun removeListener() {
        listenerRegistration.remove()
    }

    fun onNavigation() {
        _isDatabaseChanged.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onClear called")
    }


}