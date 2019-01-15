package com.chhemsronglong.together.UserPostActivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.chhemsronglong.together.Common.BaseViewModel
import com.chhemsronglong.together.Common.SingleLiveEvent
import com.chhemsronglong.together.DataModel.Condition
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DataModel.PostParticipant
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.FeedPostsRepository
import com.chhemsronglong.together.Firebase.UserPost
import com.chhemsronglong.together.Firebase.UsersRepository
import com.chhemsronglong.together.UserPostActivity.MyPostActivity.Companion.POST_CONTENT
import com.chhemsronglong.together.util.map
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task


class MyPostViewModel(onFailureListener: OnFailureListener,
                      private val feedsRepo: FeedPostsRepository) : BaseViewModel(onFailureListener) {
    lateinit var uid: String
    lateinit var allPost : LiveData<List<UserPost>>

    private var loadedLikes = mapOf<String, LiveData<Boolean>>()
    private var loadedPerson = mapOf<String, LiveData<PostParticipant>>()

    private val liveDataBus = MutableLiveData<String>()
    val events: LiveData<String> = liveDataBus

    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid
        }
    }

    fun getDataByType(userId : String, type: String) :LiveData<List<UserPost>>{
        if (type == POST_CONTENT){
            allPost = feedsRepo.getUserActivity(userId)
            return allPost
        }
        return  allPost
    }

    fun getLikes(postId: String): LiveData<Boolean>? = loadedLikes[postId]
    fun loadLikes(postId: String): LiveData<Boolean> {
        val existingLoadedLikes = loadedLikes[postId]
        if (existingLoadedLikes == null) {
            val liveData = feedsRepo.getLikes(postId).map { likes ->
                likes.find { it.userId == uid } != null
            }
            loadedLikes += postId to liveData
            return liveData
        } else {
            return existingLoadedLikes
        }
    }

    fun getNumber(postId: String): LiveData<PostParticipant>? = loadedPerson[postId]
    fun loadNumber(postId: String): LiveData<PostParticipant> {
        val existingLoadedNumber = loadedPerson[postId]
        if (existingLoadedNumber == null) {
            val liveData = feedsRepo.getNumber(postId).map { likes ->
               PostParticipant(likes.size,likes.find{ it.userId == uid } != null  )
            }
            loadedPerson += postId to liveData
            return liveData
        } else {
            return existingLoadedNumber
        }
    }

    fun addLike(postId: String){
        feedsRepo.addLikePost(uid,postId).addOnFailureListener { onFailureListener }.addOnCompleteListener {
                liveDataBus.value = it.result
        }
    }

    fun addParticipant(postId: String,profilePic : String?= null){
        feedsRepo.addJoinEvent(uid,postId, profilePic).addOnFailureListener { onFailureListener }.addOnCompleteListener {
                liveDataBus.value = it.result

        }
    }




//    fun removeFriend(currentId: String, tagetId : String): Task<Unit> =
//            usersRepo.deleteFollower(currentId,tagetId)
//                    .addOnFailureListener(onFailureListener)




}