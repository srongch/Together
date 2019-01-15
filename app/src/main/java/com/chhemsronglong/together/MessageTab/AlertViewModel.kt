package com.chhemsronglong.together.MessageTab

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.chhemsronglong.together.Common.*
import com.chhemsronglong.together.DataModel.*
import com.chhemsronglong.together.Firebase.*
import com.chhemsronglong.together.util.map


class AlertViewModel(onFailureListener: OnFailureListener,
                     private val feedsRepo: FeedPostsRepository) : BaseViewModel(onFailureListener) {

    lateinit var uid: String

    lateinit var alertList: LiveData<List<UserAlertModel>>
    private var notification =  mapOf <String, LiveData<AlertModel>>()


    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid

         alertList = feedsRepo.getUserAlerts(uid).map {
             it.sortedByDescending {
                 it.timestamp
             }
         }

        }
    }

    fun getNotificaiton(notificationId: String): LiveData<AlertModel>? = notification[notificationId]
    fun loadNofitication(notificationId: String): LiveData<AlertModel> {
        val existingLoadedNotification = getNotificaiton(notificationId)
        if (existingLoadedNotification == null) {
            val liveData = feedsRepo.getNotification(notificationId)
            notification += notificationId to liveData
            return liveData
        } else {
            return existingLoadedNotification
        }
    }


}