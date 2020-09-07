package dev.sijanrijal.note.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Note (

    var created_date: Date = Calendar.getInstance().time,

    var description: String,
    var label: String = "None",
    var last_modified: Date = Calendar.getInstance().time,
    var note_title: String = "Title",
    var note_id: String = "")