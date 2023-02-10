package com.android.studio.azne

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class AzneApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}