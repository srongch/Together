package com.chhemsronglong.together.ChatActivity

import android.arch.lifecycle.LiveData
import com.chhemsronglong.together.DataModel.Post
import com.google.android.gms.tasks.OnFailureListener
import showVLog
import android.graphics.Bitmap
import com.chhemsronglong.together.Common.*
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.DataModel.Message
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.*
import com.chhemsronglong.together.util.IMAGE_PATH_PROFILE
import com.chhemsronglong.together.util.map
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.auth.User


class ChatViewModel(onFailureListener: OnFailureListener,
                    private val userRepo: UsersRepository) : BaseViewModel(onFailureListener) {

    lateinit var fromUserId: String
    lateinit var toUserId : String
    lateinit var mainMessageId : String

    lateinit var chatList: LiveData<List<Message>>
    lateinit var user : LiveData<UserModel>


    private var activityPostList = mapOf<String, LiveData<Post>>()
    private var postList = mapOf<String, LiveData<Post>>()

    fun init(fromUserId: String,toUserId : String,mainMessageId : String) {
        if (!this::fromUserId.isInitialized) {
            this.fromUserId = fromUserId
            this.toUserId = toUserId
            this.mainMessageId = mainMessageId

            chatList = userRepo.getMessgeContents(mainMessageId).map {
                it.sortedByDescending { it.timestamp }
            }
            user = userRepo.getUser(toUserId)
        }
    }

    fun addMessage(message: Message) : Task<Unit> = userRepo.addMessage(message)

//    fun uploadUserImage(bitmap: Bitmap, callback: (status: Boolean) -> Unit){
//        uploadImage("$uid/$IMAGE_PATH_PROFILE",bitmap) { data, downloadUrl ->
//            showVLog("Picture uplaoded :  $downloadUrl")
//
//            if (data){
//             var task =    userRepo.updateUserPhoto(downloadUrl!!)
//                task.addOnSuccessListener {
//                    callback (true)
//                }
//                task.addOnFailureListener {
//                    callback (false)
//                }
//            }else{
//                callback(false)
//            }
//        }
//    }


    fun updateUserName(userName : String): Task<Unit> = userRepo.updateUserName(userName).addOnFailureListener(onFailureListener)


    fun getPost(postId: String, isMyPost : Boolean): LiveData<Post>? {
        if (isMyPost) return  activityPostList[postId]
        else return postList[postId]
    }

//    fun loadsPost(postId: String, isMyPost: Boolean): LiveData<Post> {
//
//        if (isMyPost){
//            val existingLoadedNumber = activityPostList[postId]
//            if (existingLoadedNumber == null) {
//                var livedata = feedPostsRepo.getFeedPost(uid,postId)
//                activityPostList += postId to livedata
//                return livedata
//            } else {
//                return existingLoadedNumber
//            }
//        }else{
//            val existingLoadedNumber = postList[postId]
//            if (existingLoadedNumber == null) {
//                var livedata = feedPostsRepo.getFeedPost(uid,postId)
//                postList += postId to livedata
//                return livedata
//            } else {
//                return existingLoadedNumber
//            }
//        }
//    }


}