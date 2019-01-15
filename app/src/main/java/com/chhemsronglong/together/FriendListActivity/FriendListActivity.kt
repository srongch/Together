package com.chhemsronglong.together.FriendListActivity

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.EditorInfo
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.OtherUserProfile.FriendProfileActivity
import com.chhemsronglong.together.OtherUserProfile.FriendProfileActivity.Companion.USER_ID
import com.chhemsronglong.together.R

import kotlinx.android.synthetic.main.content_friend_list.*
import kotlinx.android.synthetic.main.tool_back_text.*
import org.jetbrains.anko.sdk27.coroutines.textChangedListener
import org.jetbrains.anko.toast
import showVLog

class FriendListActivity : BaseActivity(),FriendListAdapter.Listener {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: FriendListAdapter? = null
    private lateinit var userId : String
    private lateinit var mViewModel: FriendListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        layoutManager = LinearLayoutManager(this)

        friend_list_recycleview.layoutManager = layoutManager

        adapter = FriendListAdapter (this)
        friend_list_recycleview.adapter = adapter

        search_friend.clearFocus()
        search_friend.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchAction()
            }
            true
        }
        search_friend.textChangedListener {
            afterTextChanged {
                // Do something here...
                searchAction()
            }
        }

        back_button.setOnClickListener {
            onBackPressed()
        }

        userId = intent.getStringExtra(USER_ID)
//        toast("userID  : $userId")

        subscribeUI()

    }

    fun subscribeUI(){
        mViewModel = initViewModel()
        mViewModel.init(userId)

        mViewModel.myFriends.observe(this, Observer {
           // adapter?.updateFriend(it!!)
            searchAction()
        })
    }

    private fun searchAction(){
        //if (!search_friend.text.isNullOrEmpty()){
            adapter?.updateFriend(mViewModel.filterName(search_friend.text.toString()))
       // }
    }

    override fun deleteFrind(userModel: UserModel) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
       mViewModel.removeFriend(userId,userModel.userId).addOnSuccessListener {
           toast(R.string.friend_unfollowed)
       }
    }

    override fun viewUser(userModel: UserModel) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        FriendProfileActivity.start(this,userModel.userId)
    }


    companion object {
        const val USER_ID = "userId"

        fun start (context : Context, userId : String){
            var intent = Intent(context, FriendListActivity::class.java)
            intent.putExtra(USER_ID,userId)
            context.startActivity(intent)
        }
    }

}
