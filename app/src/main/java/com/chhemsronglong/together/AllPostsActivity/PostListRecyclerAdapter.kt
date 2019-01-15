package com.chhemsronglong.together.AllPostsActivity

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import bindImageFromUrl
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DataModel.PostParticipant
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.R
import com.chhemsronglong.together.R.id.*
import java.text.FieldPosition

//import com.chhemsronglong.together.R.styleable.Snackbar

class PostListRecyclerAdapter(var userId : String ,private val listener: Listener )  : RecyclerView.Adapter<PostListRecyclerAdapter.ViewHolder>() {

    interface Listener {
        fun clickOnPost(post: Post)
        fun addFavorite(postId: String)
        fun addJoin(postId: String)
        fun loadNumber(postId: String)
        fun loadLikes(postId: String)
        fun openProfile(userId : String)
    }

    private var posts = listOf<Post>()
    private var postLikes: Map<String, Boolean> = emptyMap()
    private var postNumber: Map<String, PostParticipant> = emptyMap()
    private var postIndex = listOf<String>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageCorner : ImageView = itemView.findViewById(R.id.image_corner)
        var profileImage : ImageView = itemView.findViewById(R.id.profile_image)
        var userName : TextView = itemView.findViewById(R.id.user_name)
        var time : TextView = itemView.findViewById(R.id.time)
        var titleText : TextView = itemView.findViewById(R.id.title_text)
        var notes : TextView = itemView.findViewById(R.id.notes)
        var numberOfPeople : TextView = itemView.findViewById(R.id.number_of_people)
        var location : TextView = itemView.findViewById(R.id.location)

        var likeButton : Button = itemView.findViewById(R.id.like_button)
        var addButton : Button = itemView.findViewById(R.id.add_button)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.post_list_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        var post = posts[i]
        val likes = postLikes[post.postId]?: false
        var number = postNumber[post.postId]?: PostParticipant()
        with(viewHolder){

            //set background border color base on type of activity
            when( post.type % 5){
                0 ->  {
                    viewHolder.imageCorner.setBackgroundResource(R.drawable.post_box_corner_dinning)
                }
                1 ->{
                    viewHolder.imageCorner.setBackgroundResource(R.drawable.post_box_corner_movie)
                }
                2 -> {
                    viewHolder.imageCorner.setBackgroundResource(R.drawable.post_box_corner_trip)
                }
                3 -> {
                    viewHolder.imageCorner.setBackgroundResource(R.drawable.post_box_corner_sport)
                }
                else ->  {
                    viewHolder.imageCorner.setBackgroundResource(R.drawable.post_box_corner_event)
                }
            }

            bindImageFromUrl(profileImage,post.userprofile)
            profileImage.clipToOutline = true

            userName.text = post.username
            titleText.text = post.title
            time.text = post.convertTimeFormat(post.timestamp)
            notes.text = post.note
            numberOfPeople.text = "${number.number} of ${post.person} going."
            location.text = post.locationString

            likeButton.setBackgroundResource(
                    if (likes) R.mipmap.like_on_icon
                    else R.mipmap.like_off_icon)

            // add button is not visible for poster
            if (userId == post.userId){
                addButton.visibility = View.GONE
            }else{
                addButton.visibility = View.VISIBLE
                addButton.setBackgroundResource(
                                if (number.isGoing) R.mipmap.minus_icon
                                else R.mipmap.plus_icon)
            }

            itemView.setOnClickListener { v: View  ->
                var position: Int = getAdapterPosition()
                listener.clickOnPost(post)
                //       println("Click detected on item $position")
            }

            likeButton.setOnClickListener{
                println("like click")
                listener.addFavorite(post.postId)
            }

            addButton.setOnClickListener {
                listener.addJoin(post.postId)
            }

            profileImage.setOnClickListener {
                listener.openProfile(post.userId!!)
            }
        }
        listener.loadLikes(post.postId)
        listener.loadNumber(post.postId)

    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePost(posts: List<Post>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.posts,posts) { it.postId })
        this.posts = posts
        postIndex = posts.map { it.postId }
        diffResult.dispatchUpdatesTo(this)
    }

    fun updatePostLikes(postId: String, likes: Boolean) {
        postLikes += (postId to likes)
        //search with postId index is and update change.
        notifyItemChanged(postIndex.indexOf(postId))
    }

    fun updateNumber(postId: String, number: PostParticipant) {
        postNumber += (postId to number)
        //search with postId index is and update change.
        notifyItemChanged(postIndex.indexOf(postId))
    }
}