package com.chhemsronglong.together.Firebase

import android.arch.lifecycle.LiveData
import com.chhemsronglong.together.Common.task
import com.chhemsronglong.together.Common.toUnit
import com.chhemsronglong.together.DataModel.*
import com.chhemsronglong.together.util.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot

class FirebaseFeedPostsRepository : FeedPostsRepository {

    override fun getFeedPost(uid: String, postId: String): LiveData<Post> =
            FirebaseLiveData(database.child("posts").child(postId)).map {

                it?.asFeedPost()!!
            }

    override fun getFeedPosts(uid: String,location: String): LiveData<List<Post>> {

        var ref = database.child("posts")
        if (location != ""){
            ref.child("locality").equalTo(location)
        }

  return FirebaseLiveData(ref).map {
            it.children.map {
                var post = it.getValue(Post::class.java)
                post!!}
        }
    }

    override fun getParticipaint(postId: String): LiveData<List<FeedPostLike>> {
     return database.child(DATABASE_PARTICIPAINT).child(postId).liveData().map {
            it.children.map {
               FeedPostLike(it.key!!,it.value!!.toString())
            }
        }
    }

    override fun addJoinEvent(uid: String, postId: String,profile : String?): Task<String>  =
            task { taskSource ->
                database.child("post_participants").child(postId).child(uid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap1 = it.value
                            if (postsMap1 != null) {
                                database.child("post_participants").child(postId).child(uid).removeValue()
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                taskSource.setResult("Removed")
                                            } else {
                                                taskSource.setException(it.exception!!)
                                            }
                                        }
                            } else {
                                database.child("post_participants").child(postId).child(uid).setValue(profile)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                taskSource.setResult("Joined")
                                            } else {
                                                taskSource.setException(it.exception!!)
                                            }
                                        }
                            }

                        })
            }

    override fun addLikePost(uid: String, postId: String): Task<String>  =
            task { taskSource ->
                database.child("likes").child(postId).child(uid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            val postsMap1 = it.value
                            if (postsMap1 == "true") {
                                database.child("likes").child(postId).child(uid).removeValue()
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                taskSource.setResult("Remove from favorite")
                                            } else {
                                                taskSource.setException(it.exception!!)
                                            }
                                        }
                            } else {
                                database.child("likes").child(postId).child(uid).setValue("true")
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                taskSource.setResult("Added to favorite")
                                            } else {
                                                taskSource.setException(it.exception!!)
                                            }
                                        }
                            }

                        })
            }

    override fun getLikes(postId: String): LiveData<List<FeedPostLike>> =
        FirebaseLiveData(database.child("likes").child(postId)).map {
            it.children.map { FeedPostLike(it.key!!,it.value.toString()) }
        }

    override fun getNumber(postId: String): LiveData<List<FeedPostLike>> =
        FirebaseLiveData(database.child("post_participants").child(postId)).map {
            it.children.map { FeedPostLike(it.key!!,it.value.toString()) }
        }

    override fun createNews(postId: String, postNewsModel: PostNewsModel): Task<Unit> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val reference = database.child(DATABASE_NEWS).child(postId).push()
        return reference.setValue(postNewsModel).toUnit()
    }

    override fun getPostNews(postId: String): LiveData<List<PostNewsModel>>  {
     return   FirebaseLiveData(database.child(DATABASE_NEWS).child(postId)).map {
            it.children.map { it.getValue(PostNewsModel::class.java)!! }
        }

    }



    override fun getUserFavorite(uid: String): LiveData<List<UserPost>> {
            return FirebaseLiveData(database.child("user_likes_post").child(uid)).map {
                it.children.map {
                    UserPost(it.key!!,it.value.toString()) }
            }
        }

    override fun getUserActivity(uid: String): LiveData<List<UserPost>> {
        return FirebaseLiveData(database.child(DATABASE_USER_GOTO_POST).child(uid)).map {
            it.children.map {
                UserPost(it.key!!,it.value.toString()) }
        }
    }

    override fun getUserPosts(uid: String): LiveData<List<UserPost>> {
        return FirebaseLiveData(database.child(DATABASE_USER_POSTED).child(uid)).map {
            it.children.map {
                UserPost(it.key!!,it.value.toString()) }
        }
    }

    override fun getUserFriend(uid: String): LiveData<List<UserFriend>> {
        return FirebaseLiveData(database.child(DATABASE_USER_FRIEND).child(uid)).map {
            it.children.map {
                UserFriend(it.key!!,it.value.toString()) }
        }
    }

    override fun getUserAlerts(uid: String): LiveData<List<UserAlertModel>> {
        return FirebaseLiveData(database.child(DATABASE_USER_NOTIFICATION).child(uid)).map {
            it.children.map {
                UserAlertModel(it.key!!,it.value.toString().toLong()) }
        }
    }

    override fun getNotification(notificationId: String): LiveData<AlertModel> {
        return FirebaseLiveData(database.child(DATABASE_NOTIFICATION).child(notificationId)).map {
            it?.getValue(AlertModel::class.java)!!
        }
    }


    private fun DataSnapshot.asFeedPost(): Post?{
    if (this.value == null){
        return null
    }
        return getValue(Post::class.java)
    }


}