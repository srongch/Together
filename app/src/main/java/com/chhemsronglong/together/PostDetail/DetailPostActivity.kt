package com.chhemsronglong.together.PostDetail

import activityByType
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_post_detail1.*
import bindImageFromUrlHideWhenEror
import bindImageFromUrlWithPlaceHolder
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DataModel.PostNewsModel
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.FeedPostLike
import com.chhemsronglong.together.OtherUserProfile.FriendProfileActivity
import com.chhemsronglong.together.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.edit_name_layout.view.*
import kotlinx.android.synthetic.main.tool_back_text.*
import org.jetbrains.anko.toast
import showVLog


class DetailPostActivity : BaseActivity() ,ProfileOnlyAdapter.Listener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var layoutManager: RecyclerView.LayoutManager? = null // for participant
    private var layoutManager1: RecyclerView.LayoutManager? = null // for news update

    private var adapter: ProfileOnlyAdapter? = null // adapter for people going
    private var adapter1: PostNewUpdateAdapter? = null // adapter for news updates

    private lateinit var mViewModel: DetailPostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail1)

        layoutManager = GridLayoutManager(this,5)
        recycler_view2.layoutManager = layoutManager

        adapter = ProfileOnlyAdapter(this)
        recycler_view2.adapter = adapter
        recycler_view2.setNestedScrollingEnabled(false);

        layoutManager1 = LinearLayoutManager(this)
        recycler_view3.layoutManager = layoutManager1

        adapter1 = PostNewUpdateAdapter()
        recycler_view3.adapter = adapter1
        recycler_view3.setNestedScrollingEnabled(false);



        // go back click
        back_button.setOnClickListener {
            onBackPressed()
        }

        //open news update dialog
        add_news_button.setOnClickListener {
            showVLog("add news")
            openDialog()
        }

        //join dialog
        join_button.setOnClickListener {
            showVLog("join")
            mViewModel.addParticipant(mViewModel.post.value?.postId!!,UserModel.getSharedUser().profile)
        }

        // favorite button click
        like_button_status.setOnClickListener {
            mViewModel.addLike(mViewModel.post.value?.postId!!)
        }


        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        toast("postId : " + intent.getStringExtra(DetailPostActivity.KEY_POST))

        subscribeUI()
   }

    private fun subscribeUI(){

        var postId =  intent.getStringExtra(DetailPostActivity.KEY_POST)

        mViewModel = initViewModel()
        mViewModel.init(UserModel.getSharedUser().userId,postId)
        mViewModel.post.observe(this, Observer {
            it?.let {
                updateUI(it)
            }
        })


        mViewModel.participiants.observe(this, Observer {
            it?.let {
                updateParticipiant(it)
            }
        })

        mViewModel.news.observe(this, Observer {
            it?.let {
                adapter1?.updatePosts(it)
            }
        })

        mViewModel.loadLikes(postId).observe(this, Observer {
            like_button_status.setImageResource(
                    if (it!!) R.mipmap.like_on_icon
                    else R.mipmap.like_off_icon
            )
        })

        mViewModel.loadNumber(postId).observe(this, Observer {
            join_button.text =
                    if (it!!.isGoing) "Leave Activity"
                    else "Let's do this."
            number_of_people1.text = "${it.number} of ${mViewModel?.post?.value?.person} going."

        })

        mViewModel.events.observe(this, Observer {
            toast(it!!)
            showVLog(it!!)
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun gotoProfile(userId: String) {
        openProfile(userId)
    }

    fun openProfile(userId: String){
        if (UserModel.getSharedUser().userId == userId) toast("Please goto profile tab for your profile")
        else FriendProfileActivity.start(this,userId)
    }

    fun setPointOnTheMap(latLng: LatLng, locationName: String) {
        // Add a marker in Sydney and move the camera
        mMap.addMarker(MarkerOptions().position(latLng).title(locationName))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) )
    }

    fun updateUI(post: Post){
        toolbar_title.text = activityByType(post.type)
        bindImageFromUrlWithPlaceHolder(profile_image,post.userprofile,R.mipmap.profile_image)
        profile_image.clipToOutline = true
        user_name.text = post.username
        title_text.text = post.title
        time.text = post.convertTimeFormat(post.timestamp!!)
        number_of_people1.text = "0 of ${post.person} people"
        location.text = post.locationString
        notes.text = post.note
        bindImageFromUrlHideWhenEror(imageView3,post.imageId)
        setPointOnTheMap(LatLng(post.latitute,post.longitude),post.locationName)

        if (UserModel.getSharedUser().userId == post.userId){
            join_button.visibility = View.GONE
        }else{
            add_news_button.visibility = View.GONE
        }

        //profile image click
        profile_image.setOnClickListener {
            gotoProfile(post.userId!!)
        }

    }

    fun updateParticipiant(participants:  List<FeedPostLike>){
        adapter?.updatePosts(participants)
    }

    fun openDialog (){
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_name_layout, null)
        mDialogView.name_text_edit.hint = "Enter news"
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Add News Update")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        mDialogView.done_button.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
            val news = mDialogView.name_text_edit.text.toString()

            mViewModel.addNews(mViewModel.post.value?.postId!!, PostNewsModel(news))
                        .addOnSuccessListener {
                            toast("success") }

        }
//            //cancel button click of custom layout
        mDialogView.close_button.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }


    companion object {
        const val KEY_POST = "POST"

        fun start (context : Context, postId : String){
            var intent = Intent(context, DetailPostActivity::class.java)
            intent.putExtra(KEY_POST,postId)
            context.startActivity(intent)
        }
    }
}

