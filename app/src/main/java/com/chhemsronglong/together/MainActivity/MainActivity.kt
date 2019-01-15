package com.chhemsronglong.together.MainActivity

import android.content.Intent
import android.os.Bundle
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.HomeTabFragement.ButtomNavigationActivity
import com.chhemsronglong.together.MessbershipActivity.LoginActivity
import com.chhemsronglong.together.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import org.jetbrains.anko.toast
import showVLog

class MainActivity : BaseActivity() {


    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]

     private lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()


        // Check if Sign in
        if (auth?.currentUser == null) { // goto Login if not
            // UserModel is signed in (getCurrentUser() will be null if not signed in)
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
            LoginActivity.start(this)

        }else{ //if singed in goto Main Tab

            mViewModel = initViewModel()
            mViewModel.getUser(auth.uid!!).addOnSuccessListener {
//                toast(it?.profile.toString())
                sharedUser = it
                sharedUser?.userId = auth?.uid!!
                it?.userId = auth?.uid!!
                UserModel.setSharedUser(it!!)
               ButtomNavigationActivity.start(this)
            }

        }

    }

    companion object {
      private  var sharedUser : UserModel? = null
        fun setSharedUser(userModel : UserModel) {
            sharedUser = userModel
        }

        fun getSharedUser() : UserModel{
            return sharedUser!!
        }

    }

}

