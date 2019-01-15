package com.chhemsronglong.together.Firebase

import android.arch.lifecycle.LiveData
import android.location.Location
import com.chhemsronglong.together.DataModel.AlertModel
import com.google.android.gms.tasks.Task
import org.w3c.dom.Comment
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DataModel.PostNewsModel
import com.chhemsronglong.together.DataModel.UserAlertModel
import com.google.firebase.database.Exclude

interface FeedPostsRepository {
    fun getFeedPost(uid: String, postId: String): LiveData<Post>
    fun getFeedPosts(uid: String,localtion : String): LiveData<List<Post>>
    fun addJoinEvent(uid:String, postId : String, profile : String?) : Task<String>
    fun addLikePost(uid:String, postId : String) : Task<String>
    fun getLikes(postId: String): LiveData<List<FeedPostLike>>
    fun getNumber(postId: String): LiveData<List<FeedPostLike>>
    fun getParticipaint(postId: String): LiveData<List<FeedPostLike>>
    fun createNews(postId: String, postNewsModel: PostNewsModel): Task<Unit>
    fun getPostNews(postId: String): LiveData<List<PostNewsModel>>

    fun getUserFavorite(uid: String): LiveData<List<UserPost>>
    fun getUserActivity(uid: String): LiveData<List<UserPost>>
    fun getUserPosts(uid:String) : LiveData<List<UserPost>>
    fun getUserFriend(uid:String) : LiveData<List<UserFriend>>


    fun getUserAlerts (uid: String) :  LiveData<List<UserAlertModel>>
    fun getNotification (notificationId : String ) : LiveData<AlertModel>

}


data class UserPost(val postId: String = "", val value : String= "",@get:Exclude val id: String = "")
data class FeedPostLike(val userId: String, val profilePicture : String,@get:Exclude val id: String = "")