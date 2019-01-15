package com.chhemsronglong.together.Common

import com.chhemsronglong.together.Firebase.auth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager : AuthManager {
    override fun signOut() {
        auth.signOut()
    }

    override fun signIn(email: String, password: String): Task<Unit> =
        auth.signInWithEmailAndPassword(email, password).toUnit()

    override fun getCurrentUser(): FirebaseUser? {
       return auth.currentUser
    }
}