package com.chhemsronglong.together.OtherUserProfile

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
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.auth.User


class FriendProfileViewModel(onFailureListener: OnFailureListener,
                             private val userRepo: UsersRepository,
                             private val feedPostsRepo: FeedPostsRepository) : BaseViewModel(onFailureListener) {

    lateinit var uid: String

    lateinit var user: LiveData<UserModel>
    lateinit var myFavarite: LiveData<List<UserPost>>
    lateinit var myActivity: LiveData<List<UserPost>>
    lateinit var myPosts : LiveData<List<UserPost>>
    lateinit var myFriends : LiveData<List<UserModel>>
    lateinit var ifFriend : LiveData<Boolean>

    private var activityPostList = mapOf<String, LiveData<Post>>()
    private var postList = mapOf<String, LiveData<Post>>()

    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid

            user = userRepo.getUser(uid)
            myFavarite = feedPostsRepo.getUserFavorite(uid)
            myActivity = feedPostsRepo.getUserActivity(uid)
            myPosts = feedPostsRepo.getUserPosts(uid)
            myFriends = userRepo.getFriends(uid)
        }
    }

    fun getPost(postId: String, isMyPost : Boolean): LiveData<Post>? {
        if (isMyPost) return  activityPostList[postId]
        else return postList[postId]
    }

    fun loadsPost(postId: String, isMyPost: Boolean): LiveData<Post> {

        if (isMyPost){
            val existingLoadedNumber = activityPostList[postId]
            if (existingLoadedNumber == null) {
                var livedata = feedPostsRepo.getFeedPost(uid,postId)
                activityPostList += postId to livedata
                return livedata
            } else {
                return existingLoadedNumber
            }
        }else{
            val existingLoadedNumber = postList[postId]
            if (existingLoadedNumber == null) {
                var livedata = feedPostsRepo.getFeedPost(uid,postId)
                postList += postId to livedata
                return livedata
            } else {
                return existingLoadedNumber
            }
        }
    }

    fun checkIfWeAreFriend(currentId: String,tagetId : String){
        ifFriend = userRepo.checkIfAreFriend(currentId,tagetId)
    }

    fun addFriend(currentId: String, newUser: UserModel): Task<Unit> =
            userRepo.addFriend(currentId,newUser)
                    .addOnFailureListener(onFailureListener)

    fun removeFriend(currentId: String, tagetId : String): Task<Unit> =
            userRepo.deleteFollower(currentId,tagetId)
                    .addOnFailureListener(onFailureListener)

    fun getMainMessage(mainChat: MainChat): Task<String> {
      return  userRepo.getMainMessage(mainChat)
    }


}