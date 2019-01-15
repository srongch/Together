package com.chhemsronglong.together.ViewLayout

import android.app.Application
import com.chhemsronglong.together.Common.FirebaseAuthManager
import com.chhemsronglong.together.Firebase.FirebaseFeedPostsRepository
import com.chhemsronglong.together.Firebase.FirebaseUsersRepository

class TogetherApp : Application() {
    val usersRepo by lazy { FirebaseUsersRepository() }
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val authManager by lazy { FirebaseAuthManager() }

    override fun onCreate() {
        super.onCreate()
    }
}