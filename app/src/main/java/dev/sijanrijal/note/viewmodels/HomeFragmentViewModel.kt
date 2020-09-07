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

    private var databaseRef : CollectionReference = FirebaseFirestore.getInstance().collection("users")
        .document(FirebaseAuth.getInstance().currentUser!!.uid).collection("notes")

    private val _isDatabaseChanged = MutableLiveData<Boolean>()
    val isDatabaseChanged : LiveData<Boolean>
        get() = _isDatabaseChanged

    private val listenerRegistration : ListenerRegistration = databaseRef.addSnapshotListener {value: QuerySnapshot?, error: FirebaseFirestoreException? ->
        if(error != null) {
            Timber.d("Listen failed $error")
            return@addSnapshotListener
        }
        Timber.d("Listen successful")
        _isDatabaseChanged.value = true
    }

    private val _isDatabaseReady = MutableLiveData<Boolean>()
    val isDatabaseReady : LiveData<Boolean>
        get() = _isDatabaseReady

    val notesList  = ArrayList<Note>()

    fun readyAllNotes() {
        notesList.clear()
        databaseRef.get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    val element = Note(
                        note_title = document.get("note_title").toString(),
                        description = document.getString("description") ?: "",
                        note_id = document.getString("note_id") ?: ""
                    )
                    notesList.add(element)
                    Timber.d("Notes Added $element")
                }
                _isDatabaseReady.value = true
            }
            .addOnFailureListener { exception ->
                Timber.d("Failed to retrieve documents")
            }
    }


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