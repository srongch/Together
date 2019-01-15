package com.chhemsronglong.together.Firebase

import android.arch.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query
import showVLog

class FirebaseLiveData(private val query: Query) : LiveData<DataSnapshot>() {
    private val listener = ValueEventListenerAdapter {
        value = it
        showVLog("value change")
    }

    override fun onActive() {
        showVLog("active");
        super.onActive()
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        showVLog("inactive");
        super.onInactive()
        query.removeEventListener(listener)
    }

}