package com.chhemsronglong.together.MessageTab

import android.arch.lifecycle.LiveData
import com.chhemsronglong.together.DataModel.Post
import com.google.android.gms.tasks.OnFailureListener
import showVLog
import android.graphics.Bitmap
import com.chhemsronglong.together.Common.*
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.*
import com.chhemsronglong.together.util.IMAGE_PATH_PROFILE
import com.chhemsronglong.together.util.map
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.auth.User


class MessageViewModel(onFailureListener: OnFailureListener,
                       private val userRepo: UsersRepository) : BaseViewModel(onFailureListener) {

    lateinit var uid: String

    lateinit var chatList: LiveData<List<MainChat>>
    lateinit var myFavarite: LiveData<List<UserPost>>
    lateinit var myActivity: LiveData<List<UserPost>>
    lateinit var myPosts : LiveData<List<UserPost>>
    lateinit var myFriends : LiveData<List<UserModel>>

    private var activityPostList = mapOf<String, LiveData<Post>>()
    private var postList = mapOf<String, LiveData<Post>>()

    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid

            chatList = userRepo.getChatList(uid).map {
                it.sortedByDescending { it.timestamp }
            }
        }
    }

    fun uploadUserImage(bitmap: Bitmap, callback: (status: Boolean) -> Unit){
        uploadImage("$uid/$IMAGE_PATH_PROFILE",bitmap) { data, downloadUrl ->
            showVLog("Picture uplaoded :  $downloadUrl")

            if (data){
             var task =    userRepo.updateUserPhoto(downloadUrl!!)
                task.addOnSuccessListener {
                    callback (true)
                }
                task.addOnFailureListener {
                    callback (false)
                }
            }else{
                callback(false)
            }
        }
    }


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