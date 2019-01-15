package com.chhemsronglong.together.ProfileTab

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bindImageFromUrlWithPlaceHolder
import com.chhemsronglong.together.ActivitiePosting.PostingActivity
import com.chhemsronglong.together.AllPostActivity
import com.chhemsronglong.together.AllPostsActivity.PostDetailActivity
import com.chhemsronglong.together.Common.BaseFragement
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.Firebase.UserPost
import com.chhemsronglong.together.Firebase.auth
import com.chhemsronglong.together.FriendListActivity.FriendListActivity
import com.chhemsronglong.together.HomeTabFragement.ButtomNavigationActivity
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.UserPostActivity.MyPostActivity
import com.chhemsronglong.together.PostDetail.DetailPostActivity
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.edit_name_layout.view.*
import kotlinx.android.synthetic.main.fragment_profile_tab.*
import org.jetbrains.anko.support.v4.toast
import reobserve
import showVLog
import java.io.IOException

class ProfileTabFragment : BaseFragement(), ProfilePostListAdatper.Listener {

    private var layoutManager: RecyclerView.LayoutManager? = null //going to layout
    private var adapter: ProfilePostListAdatper? = null //going to adapter

    private var layoutManager1: RecyclerView.LayoutManager? = null //recent post layout
    private var adapter1: ProfilePostListAdatper? = null //recent adapter

    private lateinit var mViewModel: ProfileTabViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = progress_bar
        hideProgressDialog()

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager1 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recycleview4.layoutManager = layoutManager
        recycleview5.layoutManager = layoutManager1

        adapter = ProfilePostListAdatper (false,this)
        adapter1 = ProfilePostListAdatper (true,this)
        recycleview4.adapter = adapter // Going To activity
        recycleview5.adapter = adapter1 // My Posts

        profile_image.clipToOutline = true

        profile_camera_button.setOnClickListener {
            showPictureDialog()
        }

        editor_name_button.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.edit_name_layout, null)
            mDialogView.name_text_edit.setText(MainActivity.getSharedUser().name)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(requireContext())
                    .setView(mDialogView)
                    .setTitle("Edit Name")
            //show dialog
            val  mAlertDialog = mBuilder.show()
            //login button click of custom layout
            mDialogView.done_button.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                val name = mDialogView.name_text_edit.text.toString()
                if (MainActivity.getSharedUser().name != name){
                    mViewModel.updateUserName(name).addOnSuccessListener {
                        toast("User name updated.")
                    }
                }

            }
//            //cancel button click of custom layout
            mDialogView.close_button.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }

        }

        friend_list_button.setOnClickListener {
            FriendListActivity.start(this.context!!,MainActivity.getSharedUser().userId)
        }

        post_button.setOnClickListener {
            MyPostActivity.start(context!!,MainActivity.getSharedUser().userId,"Post List",MyPostActivity.POST_CONTENT)

        }

        favorite_button.setOnClickListener {
            MyPostActivity.start(context!!,MainActivity.getSharedUser().userId,"Favorite List",MyPostActivity.LIKED_CONTENT)
        }

        activity_button.setOnClickListener {
            MyPostActivity.start(context!!,MainActivity.getSharedUser().userId,"Activity List",MyPostActivity.GOING_CONTENT)
        }

        acitivity_more.setOnClickListener {
            MyPostActivity.start(context!!,MainActivity.getSharedUser().userId,"Activity List",MyPostActivity.GOING_CONTENT)
        }

        post_more.setOnClickListener {
            MyPostActivity.start(context!!,MainActivity.getSharedUser().userId,"Post List",MyPostActivity.POST_CONTENT)
        }

        moreButton.setOnClickListener {
            openMenu()
        }

    }

    // Logout popup
    private fun openMenu(){
        val popupMenu = PopupMenu(context!!, moreButton)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId){
                R.id.logout -> {
                    toast("logout click")
                    auth.signOut()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        popupMenu.inflate(R.menu.setting)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
        } catch (e: Exception){
            Log.e("Main", "Error showing menu icons.", e)
        } finally {
            popupMenu.show()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeUI()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                   var imageUpload = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, contentURI)
                   uploadPhoto(imageUpload)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    //  Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
          var  imageUpload = data!!.extras!!.get("data") as Bitmap
           uploadPhoto(imageUpload)

        }
    }

    fun uploadPhoto(bitmap: Bitmap){
        showProgressDialog()
        mViewModel.uploadUserImage(bitmap) {
            if (it){
                toast("Profile uploaded")
            }else{
                toast("Upload failed ")
            }
            hideProgressDialog()
        }
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context!!)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    fun subscribeUI(){

        mViewModel = initViewModel()
        mViewModel.init(MainActivity.getSharedUser()!!.userId)
        mViewModel.user.reobserve(this, userObserver)
        mViewModel.myFavarite.reobserve(this,numberOfFavoriteOberserver)
        mViewModel.myActivity.reobserve(this,numberOfActivityOberserver)
        mViewModel.myPosts.reobserve(this,numberOfPostOberserver)
        mViewModel.myFriends.reobserve(this,numberOfFriendOberserver)
    }

    private val numberOfFavoriteOberserver = Observer<List<UserPost>> {
        showVLog("updated")
        fav_number.text ="${it?.size}"
    }

    private val numberOfActivityOberserver = Observer<List<UserPost>> {
        showVLog("updated")
        activity_num.text ="${it?.size}"
        if (it!!.size > 2){
            adapter?.updatePosts(it!!.take(2))
        }else{
            adapter?.updatePosts(it!!)
        }

    }

    private val numberOfPostOberserver = Observer<List<UserPost>> {
        showVLog("updated")
        post_num.text ="${it?.size}"
        if (it!!.size > 2){
            adapter1?.updatePosts(it!!.take(2))
        }else{
            adapter1?.updatePosts(it!!)
        }


    }

    private val numberOfFriendOberserver = Observer<List<UserModel>> {
        showVLog("updated")
        friend_num.text ="${it?.size}"
    }

    private val userObserver = Observer<UserModel> {
        it?.let {
            MainActivity.setSharedUser(it)
            profile_name.text = it.name
            bindImageFromUrlWithPlaceHolder(profile_image,it.profile,R.mipmap.default_profile_pic)
        }
    }



    private val messageObserver = Observer<String> {
        toast(it!!)
//        showVLog(it!!)
    }


    override fun loadPost(postId: String, isMyPost: Boolean) {
        if (mViewModel.getPost(postId,isMyPost) == null) {
            mViewModel.loadsPost(postId,isMyPost).observe(this, Observer {
                it?.let {
                        if (isMyPost){
                            adapter1?.updatePost(it)
                            recycleview4.post(Runnable { adapter1?.notifyDataSetChanged() })
                        }else{
                            adapter?.updatePost(it)
                            recycleview4.post(Runnable { adapter?.notifyDataSetChanged() })
                        }
                }
            })
        }
    }

    // For profile tab, show extra cell with Plus button.
    override fun ifNeedExtraRow(): Boolean = true

    override fun addCellAction(isMyPost: Boolean) {
      if (!isMyPost){
          PostDetailActivity.start(context!!,ButtomNavigationActivity.sharedLocation?.longitude!!,ButtomNavigationActivity.sharedLocation?.latitude!!)
      }else{
          PostingActivity.start(context!!)
      }
    }

    override fun openDetail(postId: String) {
        DetailPostActivity.start(context!!, postId)
    }


    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
    }


}
