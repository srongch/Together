package com.chhemsronglong.together.DataModel

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime

data class PostNewsModel(
        var content: String = "",
        val timestamp: Date = Date(),
        @get:Exclude val id: String = ""
        ){

        fun convertTimeFormat(time: Date): String {
                val format = SimpleDateFormat("dd-MM-yyyy HH:MM")
                return format.format(time)
        }
}
