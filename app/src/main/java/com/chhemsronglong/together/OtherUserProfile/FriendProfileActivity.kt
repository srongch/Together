package com.chhemsronglong.together.OtherUserProfile

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import bindImageFromUrlWithPlaceHolder
import com.chhemsronglong.together.AllPostsActivity.PostDetailActivity
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.ChatActivity.ChatActivity
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.FriendListActivity.FriendListActivity
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.PostDetail.DetailPostActivity
import com.chhemsronglong.together.ProfileTab.ProfilePostListAdatper
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.activity_friend_profile.*
import org.jetbrains.anko.toast
import showVLog

class FriendProfileActivity : BaseActivity(),ProfilePostListAdatper.Listener {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: ProfilePostListAdatper? = null
    private lateinit var userId : String
    private lateinit var mViewModel: FriendProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        userId = intent.getStringExtra(USER_ID)
//        toast("userID  : $userId")

        layoutManager = GridLayoutManager(this,2)

        recycleview6.layoutManager = layoutManager

//        profilePageRecycleView.layoutma

        adapter = ProfilePostListAdatper(true,this)
        recycleview6.adapter = adapter


        chat_button.setOnClickListener {

            // Main chat between users
            var mainChat = MainChat(
                    MainActivity.getSharedUser().userId,
                    MainActivity.getSharedUser().name,
                    MainActivity.getSharedUser().profile,
                    userId,
                    mViewModel.user.value!!.name,
                    mViewModel.user.value!!.profile,
                    "${MainActivity.getSharedUser().userId}${userId}|${userId}${MainActivity.getSharedUser().userId}"
            )

            mViewModel.getMainMessage(mainChat).addOnSuccessListener {
                showVLog(it)
                ChatActivity.start(this,mainChat.user1Id,mainChat.user2Id,it)
            }


        }

        add_friend_button.setOnClickListener {
            if (it.tag == ADD_FRIEND_TAG){
                var tagetUserModel = mViewModel?.user.value
                tagetUserModel?.userId = userId
                mViewModel.addFriend(MainActivity.getSharedUser().userId,tagetUserModel!!).addOnSuccessListener {
                    toast("Friend added")
                }
            }else{
                mViewModel.removeFriend(MainActivity.getSharedUser().userId,userId).addOnSuccessListener {
                    toast("Friend removed")
                }
            }
        }

        friend_list_button.setOnClickListener {
            //no more action need.
        }

        post_button.setOnClickListener {
            // no more action need.
        }

        back_button.setOnClickListener { onBackPressed() }


        subscribeUI()

    }

    fun subscribeUI(){
        mViewModel = initViewModel()
        mViewModel.init(userId)
        mViewModel.user.observe(this, Observer {
            it?.let {
                profile_name.text = it.name
                profile_image.clipToOutline = true
                bindImageFromUrlWithPlaceHolder(profile_image,it.profile,R.mipmap.default_profile_pic)
            }
        })
        mViewModel.myPosts.observe(this, Observer {
            post_num.text ="${it?.size}"
            adapter?.updatePosts(it!!)
        })
        mViewModel.myFriends.observe(this, Observer {
            showVLog("updated")

            friend_num.text ="${it?.size}"
        })

        mViewModel.checkIfWeAreFriend(MainActivity.getSharedUser().userId,userId)
        mViewModel.ifFriend.observe(this, Observer {
            showVLog("result")
            if (it!! ){
                add_friend_button.setImageResource(R.mipmap.friend_remove_icon)
                add_friend_button.tag = REMOVE_FRIEND_TAG
            }else{
                add_friend_button.setImageResource(R.mipmap.friend_add_icon)
                add_friend_button.tag = ADD_FRIEND_TAG
            }
        })

    }

    override fun loadPost(postId: String, isMyPost: Boolean) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (mViewModel.getPost(postId,isMyPost) == null) {
            mViewModel.loadsPost(postId,isMyPost).observe(this, Observer {
                it?.let {
                        adapter?.updatePost( it)
                        recycleview6.post(Runnable { adapter?.notifyDataSetChanged() })

                }
            })
        }
    }

    override fun ifNeedExtraRow(): Boolean = false
    override fun addCellAction(isMyPost: Boolean) {
        //no action need.
    }

    override fun openDetail(postId: String) {
        DetailPostActivity.start(this,postId)
    }

    companion object {
        const val USER_ID = "userId"
        const val ADD_FRIEND_TAG = 1
        const val REMOVE_FRIEND_TAG = 2

        fun start (context : Context, userId : String){
            var intent = Intent(context, FriendProfileActivity::class.java)
            intent.putExtra(USER_ID,userId)
            context.startActivity(intent)
        }
    }

}
