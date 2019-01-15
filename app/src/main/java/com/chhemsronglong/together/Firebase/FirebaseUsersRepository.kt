package com.chhemsronglong.together.Firebase

import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.net.Uri
import com.chhemsronglong.together.Common.task
import com.chhemsronglong.together.Common.toUnit
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.DataModel.Message
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.util.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import showVLog
import java.io.ByteArrayOutputStream
import java.util.*

class FirebaseUsersRepository : UsersRepository {

    override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid
//
    override fun updateUserProfile(currentUser: UserModel, newUser: UserModel): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()

        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
    }
    override fun uploadUserPhoto(bitmap: Bitmap): Task<String> =

     task { taskSource ->
         // Create a reference to "mountains.jpg"
         val imagePath= UUID.randomUUID().toString()+ ".jpg"
         val mountainsRef = storage.child(imagePath)

         // Create a reference to 'images/mountains.jpg'
         val mountainImagesRef = storage.child("images/profile_images/$imagePath")

         // While the file names are the same, the references point to different files
         mountainsRef.name == mountainImagesRef.name    // true
         mountainsRef.path == mountainImagesRef.path    // false

         // Get the data from an ImageView as bytesbitmap

         val baos = ByteArrayOutputStream()
         bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
         val data = baos.toByteArray()

         var uploadTask = mountainsRef.putBytes(data)

         val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
             if (!task.isSuccessful) {
                 task.exception?.let {
                     throw it
                 }
             }
             return@Continuation mountainsRef.downloadUrl
         }).addOnCompleteListener { task ->
             if (task.isSuccessful) {
                 val downloadUri = task.result
                 taskSource.setResult(downloadUri.toString())
             } else {
                 taskSource.setException(task.exception!!)
             }
         }

     }



//
    override fun updateUserPhoto(downloadUrl: String): Task<Unit> =
            database.child("users").child(currentUid()!!).child("profile").setValue(downloadUrl).toUnit()
//
    override fun getUser(): LiveData<UserModel> = getUser(currentUid()!!)
//
    override fun getUser(uid: String): LiveData<UserModel> =
            database.child("users").child(uid).liveData().map {
                it.asUser()!!
            }

    override fun getSingleUser(uid: String): Task<UserModel?> = task {

        database.child("users").child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(p0: DataSnapshot) {
                            it.setResult(p0.asUser())
                        }

                        override fun onCancelled(p0: DatabaseError) {
                             //To change body of created functions use File | Settings | File Templates.
                           showVLog("")
                            it.setResult(null)
                        }
                    }
                    )

    }

//    GET Action

    override fun getFriends(uid: String): LiveData<List<UserModel>>  {

//        var data  = database.child("posts")
        return FirebaseLiveData(database.child("user_friends").child(uid)).map {
            it.children.map {
                var post = it.getValue(UserModel::class.java)

                post!!}
        }

    }

    override fun getChatList(uid: String): LiveData<List<MainChat>> {
        return FirebaseLiveData(database.child(DATABASE_MAIN_CHAT)).map {
            it.children.map {
                var mainChat = it.getValue(MainChat::class.java)
                mainChat!!}.filter {
                it.combineUserId.contains(uid)
            }
        }
    }

    override fun getMainMessage(mainChat: MainChat): Task<String> =
            task { taskSource ->

                database.child(DATABASE_MAIN_CHAT)
//                        .orderByChild("combineUserId")
//                        .startAt(mainChat.fromUser).endAt("${mainChat.fromUser}\\uf8ff")
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {

                            var result = it.children.map {
                                it.getValue(MainChat::class.java)
                            }.filter {
                                it?.combineUserId!!.contains("${mainChat.user1Id}${mainChat.user2Id}")
                            }

                            if (result.size > 0) {
                                var mainChat = result[0]
                                taskSource.setResult(mainChat!!.mainChatId)
                            } else {
                                var key = database.child(DATABASE_MAIN_CHAT).push().key
                                mainChat.mainChatId = key!!
                                database.child(DATABASE_MAIN_CHAT).child(key!!).setValue(mainChat).toUnit().addOnCompleteListener {
                                    taskSource.setResult(key)
                                }
                            }

                        })
            }

    override fun getMessgeContents(mainMessageId: String): LiveData<List<Message>> {
        return FirebaseLiveData(database.child(DATABASE_CHAT_CONTENT).child(mainMessageId)).map {
            it.children.map {
                var message = it.getValue(Message::class.java)
                message!!}
        }
    }


    override fun checkIfAreFriend(currentId: String, tagetId: String): LiveData<Boolean> {
     return   database.child(DATABASE_USER_FRIEND).child(currentId).child(tagetId).liveData().map {
           showVLog("result")
         var result : Boolean = false
         if (it.value != null){
             result =true
         }
         result
        }

    }

    //    Edit Action

    override fun updateUserName(userName: String): Task<Unit> {
        return database.child("users").child(currentUid()!!).child("name").setValue(userName).toUnit()
    }

    override fun addFriend(currentId: String, userModel: UserModel): Task<Unit> =
            getUserFriendRef(currentId,userModel.userId).setValue(userModel).toUnit()

    override fun addMessage(message: Message): Task<Unit> {
       return database.child(DATABASE_CHAT_CONTENT).child(message.mainChatId).push().setValue(message).toUnit()
    }

    override fun deleteFollower(currentId: String, tagetId: String): Task<Unit> =
            getUserFriendRef(currentId,tagetId).removeValue().toUnit()


    private fun getUserFriendRef(currentId: String, tagetId: String) =
            database.child(DATABASE_USER_FRIEND).child(currentId).child(tagetId)

    private fun DataSnapshot.asUser(): UserModel? =
            getValue(UserModel::class.java)
}