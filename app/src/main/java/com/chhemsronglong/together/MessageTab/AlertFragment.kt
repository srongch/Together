package com.chhemsronglong.together.MessageTab

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chhemsronglong.together.AllPostsActivity.PostDetailActivity
import com.chhemsronglong.together.Common.BaseFragement
import com.chhemsronglong.together.DataModel.MainChat
import com.chhemsronglong.together.DataModel.UserAlertModel
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.MessageTab.AlertListAdapter
import com.chhemsronglong.together.OtherUserProfile.FriendProfileActivity
import com.chhemsronglong.together.PostDetail.DetailPostActivity
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.fragment_alert.*
import reobserve
import showVLog


class AlertFragment : BaseFragement(),AlertListAdapter.Listener{

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: AlertListAdapter? = null

    private lateinit var mViewModel: AlertViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_alert, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(context)

        alert_recycle_view.layoutManager = layoutManager

        adapter = AlertListAdapter(this)
        alert_recycle_view.adapter = adapter

        subscribeUI()

    }

    fun subscribeUI(){

        mViewModel = initViewModel()
        mViewModel.init(MainActivity.getSharedUser()!!.userId)
        mViewModel.alertList.reobserve(this,oberserveAlertList)
    }

    //Get User Alert List
    private val oberserveAlertList = Observer<List<UserAlertModel>> {
        showVLog("updated")
        adapter?.updatePosts(it!!)

    }


    override fun loadNotification(notificationId: String, position: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (mViewModel.getNotificaiton(notificationId) == null) {
            mViewModel.loadNofitication(notificationId).observe(this, Observer {
                it?.let { alertModel ->
                    showVLog(alertModel.toString())
                    adapter?.updateNotification(position,notificationId, alertModel)
                }
            })
        }
    }

    override fun gotoProfile(userId: String) {
        FriendProfileActivity.start(context!!,userId)
    }

    override fun gotoPostDetail(postId: String) {
        DetailPostActivity.start(context!!,postId)
    }


}