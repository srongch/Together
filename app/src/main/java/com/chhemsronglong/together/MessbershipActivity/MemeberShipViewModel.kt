package com.chhemsronglong.together.MessbershipActivity

import android.app.Application
import android.arch.lifecycle.LiveData
import com.chhemsronglong.together.Common.*
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.UsersRepository
import com.chhemsronglong.together.Firebase.auth
import com.chhemsronglong.together.Firebase.database
import com.chhemsronglong.together.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import showVLog

class MemeberShipViewModel(onFailureListener: OnFailureListener,
                           private val userRepo: UsersRepository,
                           private val authManager: FirebaseAuthManager) : BaseViewModel(onFailureListener) {


    // LOGIN
    fun logIn (email: String, password: String,callback: (isSuccess : Boolean, userId : FirebaseUser?)-> Unit){
       authManager.signIn(email,password).addOnSuccessListener { callback(true,authManager.getCurrentUser())
       authManager}
               .addOnFailureListener { callback(false,null) }
    }

    // GET LOGIN USER INFO
    fun getUser(userId : String) = userRepo.getSingleUser(userId)

    // GET DEVICE TOKEN
    fun getToken(callback: (isSuccess : Boolean,tokenId : String)-> Unit) {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
//                    Log.w(TAG, "getInstanceId failed", task.exception)
                        showVLog("failed")
                        callback(false,"")
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token
                    callback(true,token!!)

                })
    }

}