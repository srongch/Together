package com.chhemsronglong.together.FriendListActivity

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import bindImageFromUrl
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.R


class FriendListAdapter(private val listener: Listener )  : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    interface Listener {
        fun deleteFrind(userModel: UserModel)
        fun viewUser(userModel: UserModel)
    }

    private var friends = listOf<UserModel>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: ImageView
        var name: TextView
        var actionButton : ImageButton
      //  var itemDetail: TextView

        init {
            profileImage = itemView.findViewById(R.id.profile_image)
            name = itemView.findViewById(R.id.name)
            actionButton = itemView.findViewById(R.id.action_button)
         //   itemDetail = itemView.findViewById(R.id.item_detail)
//            itemView.setOnClickListener { v: View  ->
//                var position: Int = getAdapterPosition()
//
//                println("Click detected on item $position")
//            }

//            itemView.setOnClickListener { v: View  ->
//                var position: Int = getAdapterPosition()
//                clickListener(position)
//         //       println("Click detected on item $position")
//            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.friend_list_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        with(viewHolder){
            var userModel = friends[i]
            profileImage.clipToOutline = true
            bindImageFromUrl(profileImage,userModel.profile)
            actionButton.setImageResource(R.mipmap.friend_list_add_icon)
            name.text = userModel.name

            actionButton.setOnClickListener {
                listener.deleteFrind(userModel)
            }

            itemView.setOnClickListener{
                listener.viewUser(userModel)
            }

        }

    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun updateFriend(newFriends: List<UserModel>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.friends, newFriends) { it.id })
        this.friends = newFriends
        diffResult.dispatchUpdatesTo(this)
    }
}