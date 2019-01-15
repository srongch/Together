package com.chhemsronglong.together.HomeTabbar

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
//import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.chhemsronglong.together.Common.BaseViewModel
import com.chhemsronglong.together.Common.SingleLiveEvent
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.Firebase.FeedPostsRepository
import com.chhemsronglong.together.util.map
import com.google.android.gms.tasks.OnFailureListener
import org.jetbrains.anko.support.v4.toast
import showVLog
import android.arch.lifecycle.MutableLiveData
import android.widget.Toast
import com.chhemsronglong.together.DataModel.PostParticipant
import com.chhemsronglong.together.Firebase.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeViewModel(onFailureListener: OnFailureListener,
                    private val feedPostsRepo: FeedPostsRepository) : BaseViewModel(onFailureListener) {
    lateinit var uid: String

    lateinit var feedPosts: LiveData<List<Post>>

    private var loadedLikes = mapOf<String, LiveData<Boolean>>()
    private var loadedPerson = mapOf<String, LiveData<PostParticipant>>()

    private val liveDataBus = MutableLiveData<String>()
    val events: LiveData<String> = liveDataBus


    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid
            feedPosts = feedPostsRepo.getFeedPosts(uid,"").map {
                showVLog("size of list ${it.size}")
                it.sortedByDescending { it.timestamp }
            }
        }
    }

    fun filterByLocation (location : String): LiveData<List<Post>> {
        feedPosts = feedPostsRepo.getFeedPosts(uid,location).map {
            showVLog("size of filter list ${it.size}")
            it.sortedByDescending { it.timestamp }
        }
        return feedPosts
    }


    fun addLike(postId: String){
        feedPostsRepo.addLikePost(uid,postId).addOnFailureListener { onFailureListener }.addOnCompleteListener {
                liveDataBus.value = it.result

        }
    }

    fun addParticipant(postId: String,profilePic : String?= null){
        feedPostsRepo.addJoinEvent(uid,postId, profilePic).addOnFailureListener { onFailureListener }.addOnCompleteListener {
                liveDataBus.value = it.result
        }
    }


    fun getLikes(postId: String): LiveData<Boolean>? = loadedLikes[postId]
    fun loadLikes(postId: String): LiveData<Boolean> {
        val existingLoadedLikes = loadedLikes[postId]
        if (existingLoadedLikes == null) {
            val liveData = feedPostsRepo.getLikes(postId).map { likes ->
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
            val liveData = feedPostsRepo.getNumber(postId).map { likes ->
                PostParticipant(likes.size,likes.find{ it.userId == uid } != null  )
            }
            loadedPerson += postId to liveData
            return liveData
        } else {
            return existingLoadedNumber
        }
    }


}