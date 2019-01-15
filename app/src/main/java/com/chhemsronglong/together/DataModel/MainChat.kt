package com.chhemsronglong.together.DataModel

import com.google.firebase.database.Exclude

data class MainChat(

        var user1Id : String = "",
        var user1Name : String = "",
        var user1Profile : String? = null,

        var user2Id : String = "",
        var user2Name : String = "",
        var user2Profile : String? = null,

        var combineUserId : String = "",

        var lastMessage : String = "",
        var fromUser : String = "",

        var mainChatId : String = "",

        val timestamp: Long = System.currentTimeMillis(),
        @get:Exclude val id: String = ""
        )


