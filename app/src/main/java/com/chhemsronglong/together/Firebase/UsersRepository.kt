package com.chhemsronglong.together.Firebase

import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.net.Uri
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.DataModel.Message
import com.chhemsronglong.together.DataModel.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.Exclude
import com.google.firebase.firestore.auth.User

interface UsersRepository {
    fun currentUid(): String?
    fun uploadUserPhoto(bitmap: Bitmap): Task<String>
    fun updateUserPhoto(downloadUrl: String): Task<Unit>
    fun updateUserProfile(currentUser: UserModel, newUser: UserModel): Task<Unit>
    fun getUser(): LiveData<UserModel>
    fun getUser(uid: String): LiveData<UserModel>
    fun getSingleUser(uid: String) : Task<UserModel?>

//    ADD Action
    fun addFriend(currentId: String,userModel: UserModel) : Task<Unit>
    fun addMessage(message: Message) : Task<Unit>

//    DELETE ACTION
    fun deleteFollower(currentId: String, tagetId: String): Task<Unit>


//    GET Action

    fun getChatList(uid:String) : LiveData<List<MainChat>>

    fun getFriends(uid: String): LiveData<List<UserModel>>
    fun checkIfAreFriend(currentId: String,tagetId : String) : LiveData<Boolean>

    fun getMainMessage(mainChat: MainChat) : Task<String>
    fun getMessgeContents(mainMessageId : String) : LiveData<List<Message>>

//    Edit Action
    fun updateUserName(userName: String): Task<Unit>

}

data class UserFriend(val userId: String = "", val value : String= "",@get:Exclude val id: String = "")
