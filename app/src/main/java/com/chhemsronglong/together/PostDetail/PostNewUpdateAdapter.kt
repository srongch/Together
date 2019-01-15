package com.chhemsronglong.together.PostDetail

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chhemsronglong.together.Common.SimpleCallback
import com.chhemsronglong.together.DataModel.PostNewsModel
import com.chhemsronglong.together.R


class PostNewUpdateAdapter  : RecyclerView.Adapter<PostNewUpdateAdapter.ViewHolder>() {


    private var news = listOf<PostNewsModel>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var dateText: TextView
        var contentText: TextView

        init {
            dateText = itemView.findViewById(R.id.date_text)
            contentText = itemView.findViewById(R.id.content)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.post_update_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        var new = news[i]
        viewHolder.dateText.text = new.convertTimeFormat(new.timestamp)
        viewHolder.contentText.text = new.content
    }

    override fun getItemCount(): Int {
        return news.size
    }

    fun updatePosts(newNews: List<PostNewsModel>) {

        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.news, newNews) { it.id })
        this.news = newNews
        diffResult.dispatchUpdatesTo(this)

    }

}