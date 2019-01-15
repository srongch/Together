package com.chhemsronglong.together.MessbershipActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.Firebase.database
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.DialogCustom.showNotesAlertDialog
import com.chhemsronglong.together.HomeTabFragement.ButtomNavigationActivity
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.toast
import showVLog

class LoginActivity : BaseActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    private lateinit var mViewModel: MemeberShipViewModel
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = progress_bar_login
        hideProgressDialog()

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        login_button.setOnClickListener {
            validatedLogin()
        }

        singup_text.setOnClickListener {
            val intent = Intent(this, SignupAcivity::class.java)
            startActivity(intent)
        }

        login_email_text.setText("")
        login_password.setText("")

        mViewModel = initViewModel()

    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")

        showProgressDialog()

        mViewModel.logIn(email,password){isSuccss, user ->
            hideProgressDialog()
            if (isSuccss){
                updateUI(user)
            }else{
                toast("Login Error, Please try again.")
            }
        }

    }


    fun updateUI (user : FirebaseUser?) {
        //GET DEVICE TOKEN

        if (user == null) return
        user?.let {
            mViewModel.getUser(it.uid).addOnSuccessListener {

                var userModel = UserModel()
                if (it == null){
                    userModel.email = login_email_text.text.toString()
                    userModel.userId = user.uid

                }else{
                    userModel = it
                }

                MainActivity.setSharedUser(userModel!!)
                UserModel.setSharedUser(userModel)
                mViewModel.getToken {isSuccess, tokenId ->
                    if (isSuccess){
                        userModel.devideToken = tokenId
                        database.child("users").child(userModel?.userId!!).setValue(userModel);
                        ButtomNavigationActivity.start(this)
                    }else{
                        toast("Login Error")
                    }
                }

            }.addOnFailureListener {

            }
        }

    }

    private fun validatedLogin(){
        if (login_email_text.text.isEmpty()){
            toast("Input Email")
            return
        }

        if (login_password.text.isEmpty()){
            toast("Input Password")
            return
        }
        signIn(login_email_text.text.toString(),login_password.text.toString())
    }


//    override fun onBackPressed() {
//        Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
//    }

    companion object {
        private const val TAG = "Loginpage"

//        https://stackoverflow.com/questions/6330260/finish-all-previous-activities
        fun start (context : Context){
            var intent = Intent(context,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)

        }
    }

}
