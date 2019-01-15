package com.chhemsronglong.together.AllPostsActivity

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.chhemsronglong.together.ActivitiePosting.PostingActivity
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.Condition
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.OtherUserProfile.FriendProfileActivity
import com.chhemsronglong.together.PostDetail.DetailPostActivity
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.activity_post_detail.view.*
import kotlinx.android.synthetic.main.tool_back_text.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import showVLog
import java.time.Year

class PostDetailActivity : BaseActivity(), PostListRecyclerAdapter.Listener {


    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: PostListRecyclerAdapter? = null

    private lateinit var mViewModel: AllPostsViewModel

    private  var longTitude : Double = 0.0
    private  var latitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
//        setSupportActionBar(toolbar)

        setupOptionButton()

        layoutManager = LinearLayoutManager(this)
        recycler_view1.layoutManager = layoutManager

        adapter = PostListRecyclerAdapter(UserModel.getSharedUser().userId,this)
        recycler_view1.adapter = adapter

        addPost.setOnClickListener{
            //     IntentUtil.st
            val intent = Intent(this, PostingActivity::class.java)
            startActivity(intent)
        }

        toolbar_title.text = "together"
        back_button.setOnClickListener {
            onBackPressed()
        }

        button2.setOnClickListener {
            searchAction()
        }

        search_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchAction()
            }
            true
        }

         longTitude = intent.getDoubleExtra(LONGTITUDE,0.0)
         latitude  = intent.getDoubleExtra(LATITUDE,0.0)

//        toast("location : $longTitude / $latitude")
        subscribeUI()

    }

    private fun searchAction(){
        applyCondition()
        // Hide keyboard
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

    }

    fun subscribeUI(){
        mViewModel = initViewModel()
        mViewModel.init(MainActivity.getSharedUser().userId,longTitude,latitude)


        mViewModel.allPost.observe(this, Observer {
            adapter?.updatePost(mViewModel.filterDataWithCondition(createCondition()))

        })

        //get action status
        mViewModel.events.observe(this, Observer {
//            toast(it!!)
            showVLog(it!!)
        })

    }

    override fun clickOnPost(post: Post) {
        DetailPostActivity.start(this,post.postId)
    }

    // setup 6 filter buttons onclick and status
    private fun setupOptionButton() {

        all_button.setOnClickListener {
            if (!all_button.isSelected){
            all_button.setImageResource(R.mipmap.filter_all_color)
                all_button.isSelected = true
                dining_button.isSelected = true
                movie_button.isSelected = true
                trip_button.isSelected = true
                sport_button.isSelected = true
                event_button.isSelected = true
            }
            setImageToButton(0)

        }

        dining_button.setOnClickListener {

            dining_button.isSelected = !dining_button.isSelected
            dining_button.setImageResource(if (dining_button.isSelected) R.mipmap.filter_dining_color else R.mipmap.filter_dining_bw)
            setImageToButton(1)
        }

        movie_button.setOnClickListener {
            movie_button.isSelected = !movie_button.isSelected
            movie_button.setImageResource( if (movie_button.isSelected) R.mipmap.filter_movie_color else R.mipmap.filter_movie_bw)
            setImageToButton(2)
        }

        trip_button.setOnClickListener {
            trip_button.isSelected = !trip_button.isSelected
            trip_button.setImageResource(if (trip_button.isSelected) R.mipmap.filter_trip_color else R.mipmap.filter_travel_bw)

            setImageToButton(3)
        }

        sport_button.setOnClickListener {
            sport_button.isSelected = !sport_button.isSelected
            sport_button.setImageResource(if (sport_button.isSelected) R.mipmap.filter_sport_color else R.mipmap.filter_sport_bw)

            setImageToButton(4)
        }

        event_button.setOnClickListener {
            event_button.isSelected = !event_button.isSelected
            event_button.setImageResource(if (event_button.isSelected) R.mipmap.filter_event_color else R.mipmap.filter_event_bw)

            setImageToButton(5)
        }

        //initail button click

        setImageToButton(0)


    }

    protected fun setImageToButton(buttonIndex: Int){

        if (buttonIndex == 0) {

            dining_button.isSelected = true
            movie_button.isSelected = true
            trip_button.isSelected = true
            sport_button.isSelected = true
            event_button.isSelected = true

            dining_button.setImageResource(if (dining_button.isSelected) R.mipmap.filter_dining_color else R.mipmap.filter_dining_bw)
            movie_button.setImageResource(if (movie_button.isSelected) R.mipmap.filter_movie_color else R.mipmap.filter_movie_bw)
            trip_button.setImageResource(if (trip_button.isSelected) R.mipmap.filter_trip_color else R.mipmap.filter_travel_bw)
            sport_button.setImageResource(if (sport_button.isSelected) R.mipmap.filter_sport_color else R.mipmap.filter_sport_bw)
            event_button.setImageResource(if (event_button.isSelected) R.mipmap.filter_event_color else R.mipmap.filter_event_bw)

        }else{
            all_button.setImageResource(R.mipmap.filter_all_bw)
            all_button.isSelected = false
        }

        applyCondition()

    }

    // create condition for filter and search.
    fun createCondition():Condition{
        var condition = Condition()
        condition.all = all_button.isSelected
        condition.dinning = dining_button.isSelected
        condition.movie = movie_button.isSelected
        condition.trip = trip_button.isSelected
        condition.sport = sport_button.isSelected
        condition.event = event_button.isSelected
        condition.searchString = search_text.text.toString()
        return condition
    }

    // update filter list.
    fun applyCondition(){
        adapter?.updatePost(mViewModel.filterDataWithCondition(createCondition()))

    }

    //Handle Adapter Listener

    override fun addFavorite(postId: String) {
        mViewModel.addLike(postId)
    }

    override fun openProfile(userId: String) {
        if (UserModel.getSharedUser().userId == userId) toast("Please goto profile tab for your profile")
        else FriendProfileActivity.start(this,userId)
    }

    override fun addJoin(postId: String) {
        mViewModel.addParticipant(postId,MainActivity.getSharedUser()?.profile)
    }

    override fun loadLikes(postId: String) {
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

    companion object {
        const val LONGTITUDE = "longtitue"
        const val LATITUDE = "latitue"

        fun start (context : Context, longtitude : Double, latitude : Double){
            var intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra(LONGTITUDE,longtitude)
            intent.putExtra(LATITUDE,latitude)
            context.startActivity(intent)
        }
    }

}
