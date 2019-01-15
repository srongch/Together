package com.chhemsronglong.together.ProfileTab

import activityByType
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import bindImageFromUrlWithPlaceHolder
import colorByType
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.Firebase.UserPost
import com.chhemsronglong.together.R
import org.jetbrains.anko.textColor
import android.graphics.Color
import android.text.method.TextKeyListener.clear
import showVLog
import java.security.spec.PSSParameterSpec

//import com.chhemsronglong.together.R.styleable.Snackbar

class ProfilePostListAdatper(private val isMyPost: Boolean, private val listener: Listener)  : RecyclerView.Adapter<ProfilePostListAdatper.ViewHolder>() {

    interface Listener {
        fun loadPost(postId: String, isMyPost: Boolean)
        fun ifNeedExtraRow(): Boolean
        fun openDetail(postId: String)
        fun addCellAction(isMyPost: Boolean)
    }

    private var posts = listOf<UserPost>()
    private var postDetail: Map<String, Post> = emptyMap()
    private var postIndex = listOf<String>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profilePic: ImageView = itemView.findViewById(R.id.profile_image)
        var userName: TextView = itemView.findViewById(R.id.username)
        var title: TextView = itemView.findViewById(R.id.title_text)
        var eventType: TextView = itemView.findViewById(R.id.type)
        var dateText: TextView = itemView.findViewById(R.id.time)
        var blackAddCover : ImageView = itemView.findViewById(R.id.non_cover_image)
        var addImage : ImageView = itemView.findViewById(R.id.add_image)
        init { }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.profile_card_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        var userPost  : UserPost? = null
        var postModel : Post? = null

        if (posts.size <= i){
            userPost = UserPost()
            postModel = Post()
        }else{
            userPost = posts[i]
            postModel = postDetail[userPost.postId]?: Post()
        }

        with(viewHolder){
            if (postModel.postId == ""){
                blackAddCover.visibility = View.VISIBLE
                addImage.visibility = View.VISIBLE
                itemView.setOnClickListener { listener.addCellAction(isMyPost) }
            }else{
                blackAddCover.visibility = View.GONE
                addImage.visibility = View.GONE

                bindImageFromUrlWithPlaceHolder(profilePic,postModel.userprofile,R.mipmap.profile_image)
                profilePic.clipToOutline = true
                userName.text = postModel.username
                title.text = postModel.title
                eventType.text = activityByType(postModel.type)
                eventType.textColor = Color.parseColor(colorByType(postModel.type))
                dateText.text = postModel.convertTimeFormat(postModel?.timestamp)
                itemView.setOnClickListener {listener.openDetail(postModel.postId)}
            }

            listener.loadPost(userPost.postId,isMyPost)

        }
        showVLog("called")

    }

    override fun getItemCount(): Int {

        if (listener.ifNeedExtraRow() == true){

            return posts.size + 1
        }else{
            return posts.size
        }

    }

    fun updatePosts(newPosts: List<UserPost>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.posts, newPosts) { it.id })
        if (newPosts.size == 0) {
            this.posts = emptyList()
            this.postIndex = emptyList()
        }
        else {
            this.posts = newPosts
            postIndex = posts.map { it.postId }
        }
        diffResult.dispatchUpdatesTo(this)
    }

    fun updatePost(post : Post) {
        postDetail += (post.postId to post)
        notifyItemChanged(postIndex.indexOf(post.postId))
    }
}