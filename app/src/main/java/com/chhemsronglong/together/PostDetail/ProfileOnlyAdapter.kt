package com.chhemsronglong.together.PostDetail

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import bindImageFromUrlWithPlaceHolder
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.Firebase.FeedPostLike
import com.chhemsronglong.together.R

//import com.chhemsronglong.together.R.styleable.Snackbar

class ProfileOnlyAdapter(private val listener: Listener)  : RecyclerView.Adapter<ProfileOnlyAdapter.ViewHolder>() {

    interface Listener {
        fun gotoProfile(userId : String)
    }

    private var people = listOf<FeedPostLike>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: ImageView

        init {
            profileImage = itemView.findViewById(R.id.profile_image)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.profile_only_cardview, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        var user = people[i]
        bindImageFromUrlWithPlaceHolder(viewHolder.profileImage,user.profilePicture,R.mipmap.profile_image)
        viewHolder.profileImage.clipToOutline = true
        viewHolder.itemView.setOnClickListener {
            listener.gotoProfile(user.userId)
        }

    }

    override fun getItemCount(): Int {
        return people.size
    }

    fun updatePosts(newParticipaint: List<FeedPostLike>) {

        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.people, newParticipaint) { it.id })
        this.people = newParticipaint
        diffResult.dispatchUpdatesTo(this)

    }


}