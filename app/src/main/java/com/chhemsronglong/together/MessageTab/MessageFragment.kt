package com.chhemsronglong.together.MessageTab

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chhemsronglong.together.ChatActivity.ChatActivity
import com.chhemsronglong.together.Common.BaseFragement
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.fragment_message.*
import reobserve
import showVLog




class MessageFragment : BaseFragement(),MessageListAdapter.Listener {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: MessageListAdapter? = null

    private lateinit var mViewModel: MessageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

       // View rootView = inflater.inflate(R.layout.fragment_colors, container, false);

        return inflater!!.inflate(R.layout.fragment_message, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(context)

        message_fragment_recycleview.layoutManager = layoutManager

        adapter = MessageListAdapter(UserModel.getSharedUser().userId,this)

        message_fragment_recycleview.adapter = adapter

        subscribeUI()

    }

    fun subscribeUI(){

        mViewModel = initViewModel()
        mViewModel.init(MainActivity.getSharedUser()!!.userId)
        mViewModel.chatList.reobserve(this, oberserveMainChat)
    }

    override fun openChat(mainChat: MainChat) {

        var fromUserId = MainActivity.getSharedUser().userId
        var toUserId = if (fromUserId == mainChat.user1Id) mainChat.user2Id else mainChat.user1Id

        ChatActivity.start(context!!,fromUserId,toUserId,mainChat.mainChatId)
    }

    private val oberserveMainChat = Observer<List<MainChat>> {
        showVLog("updated")
        adapter?.updatePosts(it!!)

    }
}



