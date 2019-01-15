package com.chhemsronglong.together.ChatActivity

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import bindImageFromUrl
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.DataModel.Message
import com.chhemsronglong.together.R
import convertDateFormat
import convertTimeFormat
import isSameDay
import showILog
import showVLog

class ChatListAdapter(val authuserId : String,val clickListener: (currentPosition:Int) -> Unit)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    private var messages = listOf<Message>()
    private var profile : String?  = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        var profileImage: ImageView
         var message: TextView
          var time: TextView
        var date : TextView

        init {
//              profileImage = itemView.findViewById(R.id.profileImage)
              message = itemView.findViewById(R.id.message)
               date = itemView.findViewById(R.id.date)
            time = itemView.findViewById(R.id.time)
//            itemView.setOnClickListener { v: View  ->
//                var position: Int = getAdapterPosition()
//
//                println("Click detected on item $position")
//            }

            itemView.setOnClickListener { v: View ->
                var position: Int = getAdapterPosition()
                clickListener(position)
                //       println("Click detected on item $position")
            }

        }
    }

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: ImageView
        var message: TextView
        var time: TextView
        var date : TextView

        init {
            profileImage = itemView.findViewById(R.id.profileImage)
            message = itemView.findViewById(R.id.message)
            date = itemView.findViewById(R.id.date)
            time = itemView.findViewById(R.id.time)
//            itemView.setOnClickListener { v: View  ->
//                var position: Int = getAdapterPosition()
//
//                println("Click detected on item $position")
//            }

            itemView.setOnClickListener { v: View ->
                var position: Int = getAdapterPosition()
                clickListener(position)
                //       println("Click detected on item $position")
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
//        val v = LayoutInflater.from(viewGroup.context)
//                .inflate(R.layout.friend_list_layout, viewGroup, false)

        when (i) {
            VIEW_TYPE_MESSAGE_SENT-> {
                var v = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.chat_message_sent, viewGroup, false)
                return ViewHolder(v)
            }
            else ->{
                var v = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.chat_message_received, viewGroup, false)
                return ViewHolder1(v)
            }

        }

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {

      //  showVLog("row is $i")
        var message = messages[i]
        var isTheSameDay = false
        if (i+1 == messages.size){
            showVLog("scroll to top : $i")
            isTheSameDay = false
        }
        else if (messages.size > i + 1){
            var previous = messages[i+1]
            isTheSameDay = isSameDay(previous.timestamp,message.timestamp)
            showVLog("same date is $isTheSameDay")

        }

        val viewType = viewHolder.itemViewType
        var topDate : TextView? = null
        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            var viewholder = viewHolder as ViewHolder1
            if (viewHolder.profileImage != null) {
                viewHolder.profileImage.clipToOutline = true
                bindImageFromUrl(viewHolder.profileImage,profile)
                viewHolder.message.text = message.content
                viewHolder.time.text = convertTimeFormat(message.timestamp)
                topDate = viewHolder.date
            }
        } else {
            // If some other user sent the message
            var viewholder = viewHolder as ViewHolder
            viewHolder.message.text = message.content
            viewHolder.time.text = convertTimeFormat(message.timestamp)
            topDate = viewHolder.date
        }

        if (!isTheSameDay){
            topDate?.visibility = View.VISIBLE
            topDate?.text = convertDateFormat(message.timestamp)
        }else{
            topDate?.visibility = View.GONE
        }







//        viewHolder.profileImage?.let {
//            viewHolder.profileImage?.clipToOutline = true
//            viewHolder.profileImage?.setImageResource(R.mipmap.image1)
//        }
        //   viewHolder.itemTitle.text = titles[i]
        //   viewHolder.itemDetail.text = details[i]
        //   viewHolder.itemImage.setImageResource(images[i])

//        (viewHolder as ViewHolder).bind(partItemList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {

        var message = messages[position]

        if (message.fromUserId == authuserId){
            return VIEW_TYPE_MESSAGE_SENT
        }else{
            return VIEW_TYPE_MESSAGE_RECEIVED
        }

    }

    fun updateProfile(string: String?){
        profile = string
        notifyDataSetChanged()
    }

    fun updateMessage(messages: List<Message>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.messages,messages) { it.fromUserId })
        this.messages = messages
        diffResult.dispatchUpdatesTo(this)
    }



}