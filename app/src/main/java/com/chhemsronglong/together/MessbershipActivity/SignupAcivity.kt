package com.chhemsronglong.together.MessbershipActivity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.UserModel
import com.chhemsronglong.together.DialogCustom.showAlertDialog
import com.chhemsronglong.together.DialogCustom.showNotesAlertDialog
import com.chhemsronglong.together.Firebase.database
import com.chhemsronglong.together.HomeTabFragement.ButtomNavigationActivity
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.R
import com.chhemsronglong.together.util.DiaLogType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
//import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.toast
import showVLog

class SignupAcivity : BaseActivity(), View.OnClickListener  {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    private lateinit var mViewModel: MemeberShipViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        progressBar = progress_bar
        hideProgressDialog()

        signup_button.setOnClickListener {
          //  val intent = Intent(this, ButtomNavigationActivity::class.java)
         //   startActivity(intent)
//            createAccount("test1@gmail.com","123456" )
            validateInput()
        }
//
        login_text.setOnClickListener {
            onBackPressed()
        }

        back_button.setOnClickListener {
            onBackPressed();
        }

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]
        mViewModel = initViewModel()


    }

    fun validateInput(){
        if (user_name.text.isNullOrEmpty()){
            toast("Input Username")
            return
        }

        if (email_text.text.isNullOrEmpty()){
            toast("Input Email")
            return
        }

        if (password_text.text.isNullOrEmpty()){
            toast("Input Password")
            return
        }

        if (confirm_password_text.text.isNullOrEmpty()){
            toast("Input Cofirm Password")
            return
        }


        if (!password_text.text.toString().equals(confirm_password_text.text.toString())){
            toast("Password is incorrect.")
            return
        }

        createAccount(email_text.text.toString(),password_text.text.toString() )

    }


    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    private fun createAccount(email: String, password: String) {
     //   Log.d(TAG, "createAccount:$email")
        showVLog("createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        showVLog("sendEmailVerification ${task.exception?.message}")
                        showDialog(DiaLogType.ALERT_TYPE,task.exception?.message){}
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // [START_EXCLUDE]
                    hideProgressDialog()
                    // [END_EXCLUDE]
                }
        // [END create_user_with_email]
    }



    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        // Disable button
//        verifyEmailButton.isEnabled = false

        // Send verification email
        // [START send_email_verification]
        val user = auth.currentUser
        user?.sendEmailVerification()
                ?.addOnCompleteListener(this) { task ->
                    // [START_EXCLUDE]
                    // Re-enable button
                 //   verifyEmailButton.isEnabled = true

                    if (task.isSuccessful) {
                        Toast.makeText(baseContext,
                                "Verification email sent to ${user.email} ",
                                Toast.LENGTH_SHORT).show()
                    } else {
                        showVLog("sendEmailVerification ${task.exception}")
                        Toast.makeText(baseContext,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show()
                    }
                    // [END_EXCLUDE]
                }
        // [END send_email_verification]
    }

    private fun validateForm(): Boolean {


        return true
    }

    private fun updateUI(user: FirebaseUser?) {

        if (user == null){
         //   toast("Register Error")
            return
        }
        user?.let {
            var useModel = UserModel()
            useModel.userId = user.uid
            useModel.email = email_text.text.toString()
            useModel.name = user_name.text.toString()

            MainActivity.setSharedUser(useModel)

            database.child("users").child(useModel.userId).setValue(useModel).addOnSuccessListener {
                showDialog(DiaLogType.ALERT_TYPE,"Register Done.\nWelcome, ${useModel.name}!"){
                    ButtomNavigationActivity.start(this)
                }
            }

        }

      //  toast("Register Error")

    }

     override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    companion object {
        private const val TAG = "EmailPassword"
    }

    //  showing dialog
    fun showDialog(type : DiaLogType, message: String?, callback: () -> Unit) {
        if (diaLog == null){}
        ////////////////////////////////////////////////////////////////
        //  making Alert dialog - admire beauty of kotlin
        ////////////////////////////////////////////////////////////////
        //  showNotesAlertDialog {  }

        when (type){
            DiaLogType.ALERT_TYPE ->{
                diaLog =     showAlertDialog{

                    cancelable = false
                    messageTextView.text = message ?: "Unknow Error"
                    removeButton()

                    doneIconClickListener {
                        callback.invoke()
                        showVLog("Notes Dialog done icon clicked")
                    }
                }
//               showing
                diaLog?.show()
            }
            DiaLogType.COMFIRM_TYPE ->{
                diaLog =     showNotesAlertDialog {

                    cancelable = false

                    closeIconClickListener {
                        showVLog("Notes Dialog close icon clicked")
                    }

                    doneIconClickListener {
                        showVLog("Notes Dialog done icon clicked")
                    }
                }
//               showing
                diaLog?.show()
            }
        }
    }



}
