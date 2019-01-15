package com.chhemsronglong.together.HomeTabbar

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.chhemsronglong.together.Common.CommonViewModel
import com.chhemsronglong.together.PostDetail.DetailPostViewModel
import com.chhemsronglong.together.ViewLayout.TogetherApp
import com.google.android.gms.tasks.OnFailureListener


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val app: TogetherApp,
                       private val onFailureListener: OnFailureListener) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val feedPostsRepo = app.feedPostsRepo
        return HomeViewModel(onFailureListener, feedPostsRepo) as T
    }
}