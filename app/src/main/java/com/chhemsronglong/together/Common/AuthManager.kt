package com.chhemsronglong.together.Common

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

interface AuthManager {
    fun signOut()
    fun signIn(email: String, password: String): Task<Unit>
    fun getCurrentUser(): FirebaseUser?
}