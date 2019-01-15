package com.chhemsronglong.together.AllPostsActivity

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
import com.chhemsronglong.together.UserPostActivity.MyPostActivity
import com.chhemsronglong.together.util.map
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task


class AllPostsViewModel(onFailureListener: OnFailureListener,
                        private val feedsRepo: FeedPostsRepository) : BaseViewModel(onFailureListener) {
    lateinit var uid: String
    lateinit var allPost : LiveData<List<Post>>

    lateinit var allUserPost : LiveData<List<UserPost>>

    private var loadedLikes = mapOf<String, LiveData<Boolean>>()
    private var loadedPerson = mapOf<String, LiveData<PostParticipant>>()
    private var loadPosts = mapOf<String, LiveData<Post>>()

    private val liveDataBus = MutableLiveData<String>()
    val events: LiveData<String> = liveDataBus

    fun init(uid: String,longTitude : Double, latitute : Double) {
        if (!this::uid.isInitialized) {
            this.uid = uid
            allPost = feedsRepo.getFeedPosts(uid,"").map {
                it.sortedByDescending { it.timestamp }
            }
        }
    }

    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid
        }
    }

    //filter data by condition. and search string. search string empty mean reset.
    fun filterDataWithCondition(condition: Condition) : List<Post>{

        var postList = mutableListOf<Post>()

        allPost.value?.forEach {


                if (condition.all){
                    if (isCanSearch(it,condition)) postList.add(it)
                }else{

                    if (condition.dinning && it.type == 0){
                        if (isCanSearch(it,condition)) postList.add(it)
                    }else if (condition.movie && it.type == 1){
                        if (isCanSearch(it,condition)) postList.add(it)
                    }else if (condition.trip && it.type == 2){
                        if (isCanSearch(it,condition)) postList.add(it)
                    }else if (condition.sport && it.type == 3){
                        if (isCanSearch(it,condition)) postList.add(it)
                    }else if (condition.event && it.type == 4){
                        if (isCanSearch(it,condition)) postList.add(it)
                    }

                }
        }
        return postList
    }


    // check if post is match with search condition.
    fun isCanSearch(post: Post,condition: Condition):Boolean{
        var combine = "${post.title} ${post.note}"

        if (!condition.searchString.isEmpty()){
            if (combine.contains(condition.searchString)) return true else  false
        }else{
            return true
        }
        return false
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


    fun getDataByType(type: String) :LiveData<List<UserPost>>{
        if (type == MyPostActivity.POST_CONTENT){
            allUserPost = feedsRepo.getUserPosts(uid)
        }else if(type == MyPostActivity.LIKED_CONTENT){
            allUserPost = feedsRepo.getUserFavorite(uid)
        }else if (type == MyPostActivity.GOING_CONTENT){
            allUserPost = feedsRepo.getUserActivity(uid)
        }
        return allUserPost
    }

    fun getPost(postId: String): LiveData<Post>? = loadPosts[postId]
    fun loadPost(postId: String): LiveData<Post> {
        val existingLoadedPosts = loadPosts[postId]
        if (existingLoadedPosts == null) {
            val liveData = feedsRepo.getFeedPost(uid,postId)
            loadPosts += postId to liveData
            return liveData
        } else {
            return existingLoadedPosts
        }
    }



}