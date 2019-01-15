package com.chhemsronglong.together.FriendListActivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.chhemsronglong.together.Common.BaseViewModel
import com.chhemsronglong.together.Common.SingleLiveEvent
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.FeedPostsRepository
import com.chhemsronglong.together.Firebase.UsersRepository
import com.chhemsronglong.together.R.id.search_friend
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task


class FriendListViewModel(onFailureListener: OnFailureListener,
                          private val usersRepo: UsersRepository) : BaseViewModel(onFailureListener) {
    lateinit var uid: String
    lateinit var myFriends : LiveData<List<UserModel>>

    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid
            myFriends = usersRepo.getFriends(uid)
        }
    }

    fun filterName(searchText: String) : List<UserModel>{
        if (searchText.isNullOrEmpty()) return myFriends.value!!
        else return myFriends.value!!.filter { it.name.contains(searchText,true) }
    }

    fun removeFriend(currentId: String, tagetId : String): Task<Unit> =
            usersRepo.deleteFollower(currentId,tagetId)
                    .addOnFailureListener(onFailureListener)




}