package com.chhemsronglong.together.Common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.chhemsronglong.together.AllPostsActivity.AllPostsViewModel
import com.chhemsronglong.together.ChatActivity.ChatViewModel


import com.chhemsronglong.together.Common.CommonViewModel
import com.chhemsronglong.together.FriendListActivity.FriendListViewModel
import com.chhemsronglong.together.HomeTabbar.HomeViewModel
import com.chhemsronglong.together.MainActivity.MainViewModel
import com.chhemsronglong.together.MessageTab.AlertViewModel
import com.chhemsronglong.together.MessageTab.MessageViewModel
import com.chhemsronglong.together.MessbershipActivity.MemeberShipViewModel
import com.chhemsronglong.together.OtherUserProfile.FriendProfileViewModel
import com.chhemsronglong.together.PostDetail.DetailPostViewModel
import com.chhemsronglong.together.ProfileTab.ProfileTabViewModel
import com.chhemsronglong.together.UserPostActivity.MyPostViewModel
import com.chhemsronglong.together.ViewLayout.TogetherApp
import com.google.android.gms.tasks.OnFailureListener
import showVLog

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val app: TogetherApp,
                       private val commonViewModel: CommonViewModel,
                       private val onFailureListener: OnFailureListener) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val usersRepo = app.usersRepo
        val feedPostsRepo = app.feedPostsRepo
        val authManager = app.authManager

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(onFailureListener, feedPostsRepo) as T
        }else if (modelClass.isAssignableFrom(DetailPostViewModel::class.java)) {
            return DetailPostViewModel(onFailureListener, feedPostsRepo) as T
        }else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(onFailureListener, usersRepo) as T
        }else if (modelClass.isAssignableFrom(ProfileTabViewModel::class.java)) {
            return ProfileTabViewModel(onFailureListener,usersRepo ,feedPostsRepo) as T
        }else if (modelClass.isAssignableFrom(FriendListViewModel::class.java)) {
            return FriendListViewModel(onFailureListener,usersRepo) as T
        }else if (modelClass.isAssignableFrom(FriendProfileViewModel::class.java)) {
            showVLog("FriendProfileViewModel created")
            return FriendProfileViewModel(onFailureListener,usersRepo,feedPostsRepo) as T
        }else if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(onFailureListener,usersRepo) as T
        }else if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(onFailureListener,usersRepo) as T
        }else if (modelClass.isAssignableFrom(AllPostsViewModel::class.java)) {
            return AllPostsViewModel(onFailureListener,feedPostsRepo) as T
        }else if (modelClass.isAssignableFrom(MemeberShipViewModel::class.java)) {
            return MemeberShipViewModel(onFailureListener,usersRepo,authManager) as T
        }else if (modelClass.isAssignableFrom(MyPostViewModel::class.java)) {
            return MyPostViewModel(onFailureListener,feedPostsRepo) as T
        }
        else if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(onFailureListener,feedPostsRepo) as T
        }else {
            error("Unknown view model class $modelClass")
        }

    }
}