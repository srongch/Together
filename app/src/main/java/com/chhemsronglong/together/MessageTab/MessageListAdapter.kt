package com.chhemsronglong.together.MessageTab

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import bindImageFromUrlWithPlaceHolder
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.R

class MessageListAdapter(var userId: String, private val listener: Listener)  : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    interface Listener {
        fun openChat(mainChat : MainChat)
    }

    private var chatList = listOf<MainChat>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: ImageView
        var sender: TextView
        var lastMessage: TextView? = null

        init {
            profileImage = itemView.findViewById(R.id.profileImage)
            profileImage.clipToOutline = true
            sender = itemView.findViewById(R.id.sender)
            lastMessage = itemView.findViewById(R.id.message)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        var v = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.message_list_layout, viewGroup, false)
        return ViewHolder(v)


    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        var mainChat = chatList[i]

        var name: String
        var profile: String
        var message = if (userId == mainChat.fromUser) "you: " else ""
        if (userId == mainChat.user1Id) {
            name = mainChat.user2Name
            profile = mainChat.user2Profile!!
        } else {
            name = mainChat.user1Name
            profile = mainChat.user1Profile!!
        }

        viewHolder.sender.text = name
        bindImageFromUrlWithPlaceHolder(viewHolder.profileImage, profile,R.mipmap.profile_image)
        viewHolder.lastMessage!!.text = message + mainChat.lastMessage
        viewHolder.itemView.setOnClickListener {
            listener.openChat(mainChat)

        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    fun updatePosts(newChats: List<MainChat>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.chatList, newChats) { it.id })
        this.chatList = newChats
        diffResult.dispatchUpdatesTo(this)
    }





}