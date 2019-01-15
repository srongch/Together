package com.chhemsronglong.together.MainActivity

import com.chhemsronglong.together.Common.BaseViewModel
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.UsersRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task


class MainViewModel(onFailureListener: OnFailureListener,
                    private val usersRepo: UsersRepository) : BaseViewModel(onFailureListener) {

    lateinit var uid: String
    fun getUser(currentUserId: String): Task<UserModel?> =
            usersRepo.getSingleUser(currentUserId)
                    .addOnFailureListener(onFailureListener)


}