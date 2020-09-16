package dev.sijanrijal.note

import android.app.Application
import timber.log.Timber

class NoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(TimberConfig)
        }
    }
}

object TimberConfig : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element) + ":" + element.lineNumber
    }
}