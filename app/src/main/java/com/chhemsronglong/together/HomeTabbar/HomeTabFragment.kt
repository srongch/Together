package com.chhemsronglong.together.HomeTabbar

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home_tab.*
import android.content.Intent
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import com.chhemsronglong.together.ActivitiePosting.PostingActivity
import com.chhemsronglong.together.Common.BaseFragement
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.Firebase.auth
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.PostDetail.DetailPostActivity
import com.chhemsronglong.together.R
import com.chhemsronglong.together.ViewLayout.TogetherApp
import com.chhemsronglong.together.util.getAddress
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.support.v4.toast
import reobserve
import showVLog
import com.chhemsronglong.together.AllPostsActivity.PostDetailActivity
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.HomeTabFragement.ButtomNavigationActivity
import com.chhemsronglong.together.OtherUserProfile.FriendProfileActivity
import observeOnce
import org.jetbrains.anko.toast


class HomeTabFragment: BaseFragement(),RecyclerAdapter.Listener {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerAdapter? = null

    private lateinit var mViewModel: HomeViewModel

    private var lastLocation: LatLng? = null

    protected var searchLocation = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view: View = inflater!!.inflate(R.layout.fragment_home_tab, container,
                false)

        layoutManager = LinearLayoutManager(context)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = layoutManager

        adapter = RecyclerAdapter(MainActivity.getSharedUser().userId,this)
        recyclerView.adapter = adapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        location_text.text = searchLocation

        addPost.setOnClickListener{
            //     IntentUtil.st
            val intent = Intent(activity, PostingActivity::class.java)
            startActivityForResult(intent, POSTING_ACTIVITY_RESULT);
        }

        more_button.setOnClickListener{
            PostDetailActivity.start(context!!,lastLocation?.longitude?: 0.0,lastLocation?.latitude?: 0.0)
        }

        ButtomNavigationActivity.sharedLocation?.let {
            lastLocation = LatLng(it.latitude,it.longitude)
        }
    }

    override fun addFavorite(postId: String) {
//        Toast.makeText(context,
//                "Join post on position:  $position ", Toast.LENGTH_SHORT).show()
        mViewModel.addLike(postId)
  }

    override fun addJoin(postId: String) {
//        Toast.makeText(context,
//                "Join post on position:  $position ", Toast.LENGTH_SHORT).show()
       mViewModel.addParticipant(postId,UserModel.getSharedUser()?.profile)
    }

    override fun openDetail(postId: String) {
       DetailPostActivity.start(context!!,postId)
   }


    override fun openProfile(userId: String) {
        if (UserModel.getSharedUser().userId == userId) toast("Please goto profile tab for your profile")
        else FriendProfileActivity.start(context!!,userId)
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

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == POSTING_ACTIVITY_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("result")
                toast(result)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    fun subscribeUI(){
        val factory = HomeViewModelFactory(activity!!.application as TogetherApp,commonViewModel)
        mViewModel = ViewModelProviders.of(this, factory)
                .get(HomeViewModel::class.java)

        mViewModel.init(auth.currentUser?.uid!!)
        mViewModel.events.reobserve(this, messageObserver)
        mViewModel.feedPosts.reobserve(this,observer)
    }

    private val observer = Observer<List<Post>> {  it?.let {

//        toast("observe")

        if (lastLocation != null){
         //   toast(lastLocation.toString())
            showVLog("list updated")
            calculatDistance(it)
        }else{
            adapter!!.updatePosts(it)
        }
    }
    }


    private val observer1 = Observer<List<Post>> {  it?.let {

        if (lastLocation != null){
          //  toast("observer 1 ${lastLocation.toString()}")
            calculatDistance(it)
        }else{
            adapter!!.updatePosts(it)
        }
    }
    }


    // filter location within 2KM and filter only 5 items.
    private fun calculatDistance(posts : List<Post>){
        val locationA = Location("point A")

        lastLocation?.let {
            locationA.latitude = it.latitude
            locationA.longitude = it.longitude

            var filterList = posts.filter {
                var distance = getDistanceKm(it,locationA)
            //    showVLog("distance : $distance")
              distance < 4
            }
            if (filterList.size > 5) filterList = filterList.take(5)
            adapter?.updatePosts(filterList)
        }

    }

    fun getDistanceKm(it: Post, withLocation : Location): Float{
        val locationB = Location("point B")

        locationB.latitude = it.latitute
        locationB.longitude = it.longitude

        showVLog(it.locationString)

        return withLocation.distanceTo(locationB) / 1000
    }

    private val messageObserver = Observer<String> {
        toast(it!!)
        showVLog(it!!)
    }

    fun objectUpdated (location: Location){
        var getAddress =getAddress(LatLng(location.latitude, location.longitude),context!!)
        searchLocation = getAddress.second?.locality!!
        location_text.text = searchLocation
        this.lastLocation  = LatLng(location.latitude,location.longitude)
        mViewModel.filterByLocation(searchLocation.toLowerCase()!!).observeOnce(this,observer1)

}

    companion object {
        var POSTING_ACTIVITY_RESULT = 1
    }

}
