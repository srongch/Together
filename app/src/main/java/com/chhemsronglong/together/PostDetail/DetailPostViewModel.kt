package com.chhemsronglong.together.PostDetail

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
import com.chhemsronglong.together.DataModel.PostNewsModel
import com.chhemsronglong.together.DataModel.PostParticipant
import com.chhemsronglong.together.Firebase.FeedPostLike
import com.google.android.gms.tasks.Task


class DetailPostViewModel(onFailureListener: OnFailureListener,
                          private val feedPostsRepo: FeedPostsRepository) : BaseViewModel(onFailureListener) {
    lateinit var uid: String
    lateinit var post: LiveData<Post>
    lateinit var participiants: LiveData<List<FeedPostLike>>
    lateinit var news: LiveData<List<PostNewsModel>>

    private var loadedLikes = mapOf<String, LiveData<Boolean>>()
    private var loadedPerson = mapOf<String, LiveData<PostParticipant>>()

    private val liveDataBus = MutableLiveData<String>()
    val events: LiveData<String> = liveDataBus

    fun init(uid: String, postId: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid

            post = feedPostsRepo.getFeedPost(uid,postId)
            participiants = feedPostsRepo.getParticipaint(postId)
            news = feedPostsRepo.getPostNews(postId)
        }
    }

    fun loadLikes(postId: String): LiveData<Boolean> {
        val liveData = feedPostsRepo.getLikes(postId).map { likes ->
                likes.find { it.userId == uid } != null
        }
        loadedLikes += postId to liveData
        return liveData
    }

    fun loadNumber(postId: String): LiveData<PostParticipant> {
            val liveData = feedPostsRepo.getNumber(postId).map { likes ->
                PostParticipant(likes.size,likes.find{ it.userId == uid } != null  )
            }
            loadedPerson += postId to liveData
            return liveData
    }

    fun addNews (postId: String, postNewsModel: PostNewsModel) : Task<Unit> =
            feedPostsRepo.createNews(postId,postNewsModel)
                    .addOnFailureListener(onFailureListener)


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


//    fun toggleLike(postId: String) {
//        feedPostsRepo.toggleLike(postId, uid).addOnFailureListener(onFailureListener)
//    }
//
//    fun getLikes(postId: String): LiveData<FeedPostLikes>? = loadedLikes[postId]
//
//    fun loadLikes(postId: String): LiveData<FeedPostLikes> {
//        val existingLoadedLikes = loadedLikes[postId]
//        if (existingLoadedLikes == null) {
//            val liveData = feedPostsRepo.getLikes(postId).map { likes ->
//                FeedPostLikes(
//                        likesCount = likes.size,
//                        likedByUser = likes.find { it.userId == uid } != null)
//            }
//            loadedLikes += postId to liveData
//            return liveData
//        } else {
//            return existingLoadedLikes
//        }
//    }
//
//    fun openComments(postId: String) {
//        _goToCommentsScreen.value = postId
//    }
}