package com.chhemsronglong.together.DataModel

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime

data class Post(
        var type : Int = 0,
        var latitute : Double = 0.0,
        var longitude : Double = 0.0,
        var locationString : String = "",
        var locality : String = "",
        var locationName : String = "",
        var title: String = "",
        var person: String = "",
        @Exclude val timestamp: Long = System.currentTimeMillis(),
        var date: String = "",
        var time : String = "",
        var note : String = "",
        var imageId : String? = null,
        var postId : String = "",
        var userId : String? = null,
        var username : String? = null,
        var userprofile : String? = null,
        var fullSearch : String = "$title $note",
        @get:Exclude val id: String = ""
        ){
       // fun timestampDate(): java.util.Date = java.util.Date(timestamp as Long)
        fun convertTimeFormat(time: Long?): String {
           if (time != null){
               val format = SimpleDateFormat("dd-MM-yyyy HH:MM")
               val netDate = Date(time)
               return format.format(time)
           }else{
               return  "00.00.0000 00:00"
           }

        }
}
