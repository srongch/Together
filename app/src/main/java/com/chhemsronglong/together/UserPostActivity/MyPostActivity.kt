package com.chhemsronglong.together.UserPostActivity

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chhemsronglong.together.AllPostsActivity.AllPostsViewModel
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.PostDetail.DetailPostActivity
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.activity_my_post.*
import kotlinx.android.synthetic.main.tool_back_text.*

class MyPostActivity : BaseActivity(),MyPostRecyclerAdapter.Listener{

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: MyPostRecyclerAdapter? = null


    private lateinit var userId  : String
    private lateinit var activityType : String

    private lateinit var mViewModel: AllPostsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_post)


        userId = intent.getStringExtra(USER_ID)
        activityType = intent.getStringExtra(TYPE)

        layoutManager = LinearLayoutManager(this)
        recycler_view.layoutManager = layoutManager

        adapter = MyPostRecyclerAdapter(userId,this)
        recycler_view.adapter = adapter

        toolbar_title.text = intent.getStringExtra(NAV_TITLE)

        back_button.setOnClickListener {
            onBackPressed()
        }

        mViewModel = initViewModel()
        mViewModel.init(userId)
        mViewModel.getDataByType(activityType).observe(this, Observer {
            adapter?.updatePosts(it!!)
        })

    }

    override fun addFavorite(postId: String) {
        mViewModel.addLike(postId)
    }

    override fun addJoin(postId: String) {
        mViewModel.addParticipant(postId,UserModel.getSharedUser()?.profile)
    }

    override fun loadLike(postId: String) {
        if (mViewModel.getLikes(postId) == null) {
            mViewModel.loadLikes(postId).observe(this, Observer {
                it?.let { postLikes ->
                    adapter?.updatePostLikes(postId, postLikes)
                }
            })
        }
    }

    override fun loadNumber(postId: String) {
        if (mViewModel.getNumber(postId) == null) {
            mViewModel.loadNumber(postId).observe(this, Observer {
                it?.let { postNumber ->
                    adapter?.updateNumber(postId, postNumber)
                }
            })
        }
    }

    override fun loadPost(postId: String) {
        if (mViewModel.getPost(postId) == null) {
            mViewModel.loadPost(postId).observe(this, Observer {
                it?.let { post ->
                    adapter?.updatePostDetail(postId, post)
                }
            })
        }
    }

    override fun openDetail(postId: String) {
        DetailPostActivity.start(this, postId)
    }


    companion object {
        const val USER_ID = "user_id"
        const val NAV_TITLE = "title"
        const val TYPE = "type"

        const val POST_CONTENT = "POSTS"
        const val LIKED_CONTENT = "FAVORITE"
        const val GOING_CONTENT = "GOINGS"

        fun start (context : Context, userId : String, title:String, type : String){
            var intent = Intent(context, MyPostActivity::class.java)
            intent.putExtra(USER_ID,userId)
            intent.putExtra(NAV_TITLE,title)
            intent.putExtra(TYPE,type)
            context.startActivity(intent)
        }
    }

}
