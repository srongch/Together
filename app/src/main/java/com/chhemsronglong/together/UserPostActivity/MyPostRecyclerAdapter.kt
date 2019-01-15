package com.chhemsronglong.together.UserPostActivity

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
import com.chhemsronglong.together.Firebase.UserPost
import com.chhemsronglong.together.R
import com.chhemsronglong.together.R.id.*
import com.google.firebase.firestore.auth.User
import showVLog

//import com.chhemsronglong.together.R.styleable.Snackbar

class MyPostRecyclerAdapter(var userId : String, private val listener: Listener)  : RecyclerView.Adapter<MyPostRecyclerAdapter.ViewHolder>() {

    interface Listener {
        fun openDetail(postId: String)
        fun addFavorite(postId: String)
        fun addJoin(postId: String)
        fun loadNumber(postId: String)
        fun loadLike(postId: String)
        fun loadPost(postId: String)
    }


    private var userPost = listOf<UserPost>()
    private var posts: Map<String, Post> = emptyMap()
    private var postLikes: Map<String, Boolean> = emptyMap()
    private var postNumber: Map<String, PostParticipant> = emptyMap()
    private var postIndex = listOf<String>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var backgroundImage : ImageView
        var activityType : ImageView
        var likeButton: Button
        var addButton: Button
        var title : TextView
        var dateText : TextView
        var notes : TextView
        var numberOfPeople : TextView
        var location : TextView

        init {
            backgroundImage = itemView.findViewById(R.id.image_corner)
            activityType = itemView.findViewById(R.id.activities_type)
            likeButton = itemView.findViewById(R.id.add_button)
            addButton = itemView.findViewById(R.id.like_button)
            title = itemView.findViewById(R.id.title_text)
            dateText = itemView.findViewById(R.id.time)
            notes = itemView.findViewById(R.id.notes)
            numberOfPeople = itemView.findViewById(R.id.number_of_people)
            location = itemView.findViewById(R.id.location)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.mypost_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        var userpost = userPost[i]
        var post = posts[userpost.postId]?: Post()
        val likes = postLikes[userpost.postId]?: false
        var number = postNumber[userpost.postId]?: PostParticipant()

        when( post?.type!! % 5){
            0 ->  {
                viewHolder.backgroundImage.setBackgroundResource(R.drawable.post_box_corner_dinning)
                viewHolder.activityType.setImageResource(R.mipmap.home_dinning)
            }
            1 ->{
                viewHolder.backgroundImage.setBackgroundResource(R.drawable.post_box_corner_movie)
                viewHolder.activityType.setImageResource(R.mipmap.home_movie_icon)
            }
            2 -> {
                viewHolder.backgroundImage.setBackgroundResource(R.drawable.post_box_corner_trip)
                viewHolder.activityType.setImageResource(R.mipmap.home_trip_icon)
            }
            3 -> {
                viewHolder.backgroundImage.setBackgroundResource(R.drawable.post_box_corner_sport)
                viewHolder.activityType.setImageResource(R.mipmap.home_sport_icon)
            }
            else ->  {
                viewHolder.backgroundImage.setBackgroundResource(R.drawable.post_box_corner_event)
                viewHolder.activityType.setImageResource(R.mipmap.home_event_icon)
            }
        }

        viewHolder.title.text = post.title
        viewHolder.notes.text = post.note
        viewHolder.dateText.text = post.convertTimeFormat(post.timestamp!!)
        viewHolder.numberOfPeople.text = "${number.number} of ${post.person} going."
        viewHolder.location.text = post.locationString

        viewHolder.likeButton.setBackgroundResource(
                if (likes) R.mipmap.like_on_icon
                else R.mipmap.like_off_icon)
//
        if (userId == post.userId){
            viewHolder.addButton.visibility = View.GONE
        }else{
            viewHolder.addButton.visibility = View.VISIBLE
            viewHolder.addButton.setBackgroundResource(
                    if (number.isGoing) R.mipmap.minus_icon
                    else R.mipmap.plus_icon)
        }

        with(viewHolder){
            itemView.setOnClickListener { v: View  ->
              //  var position: Int = getAdapterPosition()
                listener.openDetail(post.postId)
                //       println("Click detected on item $position")
            }

            likeButton.setOnClickListener{
                println("like click")
                listener.addFavorite(post.postId)
            }

            addButton.setOnClickListener {
                listener.addJoin(post.postId)
                //       println("Click detected on item $position")
            }
        }

        listener.loadPost(userpost.postId)
        listener.loadLike(post.postId)
        listener.loadNumber(post.postId)
    }

    override fun getItemCount(): Int {
        return userPost.size
    }


    fun updatePosts(newPosts: List<UserPost>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.userPost, newPosts) { it.postId })
        this.userPost = newPosts
        postIndex = newPosts.map { it.postId }
        diffResult.dispatchUpdatesTo(this)
    }

    fun updatePostDetail(postId: String, post: Post) {
        posts += (postId to post)
        notifyItemChanged(postIndex.indexOf(postId))
    }

    fun updatePostLikes(postId: String, likes: Boolean) {
        postLikes += (postId to likes)
        notifyItemChanged(postIndex.indexOf(postId))
    }

    fun updateNumber(postId: String, number: PostParticipant) {
        postNumber += (postId to number)
        notifyItemChanged(postIndex.indexOf(postId))
    }

}