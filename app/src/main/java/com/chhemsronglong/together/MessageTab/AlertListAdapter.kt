package com.chhemsronglong.together.MessageTab

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import bindImageFromUrl
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.DataModel.AlertModel
import com.chhemsronglong.together.DataModel.UserAlertModel
import com.chhemsronglong.together.R

class AlertListAdapter(private val listener: Listener)  : RecyclerView.Adapter<AlertListAdapter.ViewHolder>() {

    interface Listener {
        fun loadNotification(notificationId : String, position : Int)
        fun gotoProfile(userId : String)
        fun gotoPostDetail( postId : String)
    }

    private var alertList = listOf<UserAlertModel>()
    private var notifications: Map<String, AlertModel> = emptyMap()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: ImageView
        var sender: TextView

        init {
            profileImage = itemView.findViewById(R.id.profileImage)
            profileImage.clipToOutline = true
            sender = itemView.findViewById(R.id.sender)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
         var v = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.alert_list_layout, viewGroup, false)
                return ViewHolder(v)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        var userAlert = alertList[i]
        var notification = notifications[userAlert.notificationId]

        if (notification != null){
            bindImageFromUrl(viewHolder.profileImage,notification.fromUserProfile)
            viewHolder.sender.text = notification.text

            viewHolder.profileImage.setOnClickListener {
                listener.gotoProfile(notification.fromUserId)
            }

            viewHolder.itemView.setOnClickListener {
                listener.gotoPostDetail(notification.postId)
            }
        }
        listener.loadNotification(userAlert.notificationId,i)


    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    fun updateNotification(position: Int, notificationId: String, alertModel: AlertModel) {
        notifications += (notificationId to alertModel)
        notifyItemChanged(position)
    }


    fun updatePosts(userAlert: List<UserAlertModel>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.alertList, userAlert) { it.id })
        this.alertList = userAlert
        diffResult.dispatchUpdatesTo(this)
    }





}