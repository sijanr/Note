package dev.sijanrijal.note.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import dev.sijanrijal.note.models.Note
import timber.log.Timber

class HomeFragmentViewModel : ViewModel() {

    //reference to the document collection where the user's notes are stored
    private var databaseRef: CollectionReference = FirebaseFirestore.getInstance().collection("users")
        .document(FirebaseAuth.getInstance().currentUser!!.uid).collection("notes")

    private val _isDatabaseChanged = MutableLiveData<Boolean>()
    val isDatabaseChanged: LiveData<Boolean>
        get() = _isDatabaseChanged

    private var listener : ListenerRegistration

    val notesList = ArrayList<Note>()

    init {

        listener = databaseRef.addSnapshotListener { values, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Timber.d("Listen failed $error")
                    return@addSnapshotListener
                }
                Timber.d("Listen successful")

                notesList.clear()

                for (document in values!!) {
                    val element = Note(
                        note_title = document.get("note_title").toString(),
                        description = document.getString("description") ?: "",
                        note_id = document.getString("note_id") ?: ""
                    )
                    notesList.add(element)
                    Timber.d("Notes Added $element")
                }
                _isDatabaseChanged.value = true
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
     * Remove database change listener
     * **/
    private fun removeListener() {
        listener.remove()
    }


    override fun onCleared() {
        super.onCleared()
        removeListener()
    }

}