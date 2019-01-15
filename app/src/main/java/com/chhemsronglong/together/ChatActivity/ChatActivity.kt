package com.chhemsronglong.together.ChatActivity

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.BaseMmsColumns.MESSAGE_ID
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.content_chat.*
import android.widget.Toast
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.Message
import com.chhemsronglong.together.HomeTabFragement.ButtomNavigationActivity
import com.chhemsronglong.together.R
import kotlinx.android.synthetic.main.chat_message_sent.*
import org.jetbrains.anko.sdk27.coroutines.textChangedListener
import org.jetbrains.anko.toast
import showDLog
import showVLog


class ChatActivity : BaseActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: ChatListAdapter? = null

    private lateinit var fromUser : String
    private lateinit var toUser : String
    private lateinit var mainMessageId : String

    private lateinit var mViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        fromUser = intent.getStringExtra(FROM_USER)
        toUser = intent.getStringExtra(TO_USER)
        mainMessageId = intent.getStringExtra(MESSAGE_ID)


        var newlayoutManager = LinearLayoutManager(this)
        newlayoutManager.reverseLayout = true
//        newlayoutManager.stackFromEnd = true

        layoutManager = newlayoutManager
        chat_recycle_view.layoutManager = layoutManager

//        profilePageRecycleView.layoutma

        adapter = ChatListAdapter(fromUser) {
            //   partItemClicked(it)
        }
        chat_recycle_view.adapter = adapter

        back_button.setOnClickListener {
            onBackPressed()
        }

        send_button.setOnClickListener {
            addMessage()
        }

        mesage_text.onChange {
           if (it.isNullOrEmpty()) {
               send_button.isEnabled = false}
            else {
               send_button.isEnabled = true}
        }

        subscribeUI()

    }
    fun addMessage (){
        if (!mesage_text.text.isNullOrEmpty()){

            var message = Message(fromUser,toUser,mesage_text.text.toString(),mainMessageId)

            mViewModel.addMessage(message).addOnCompleteListener {
                toast("Message send")

                // Clean text box
                mesage_text.text.clear()
                // Hide keyboard
                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }else{
            toast("Input text")
        }
    }

    fun subscribeUI(){
        mViewModel = initViewModel()
        mViewModel.init(fromUser,toUser,mainMessageId)

        mViewModel.chatList.observe(this, Observer {
            adapter?.updateMessage(it!!)
            chat_recycle_view.scrollToPosition(0)
        })

        mViewModel.user.observe(this, Observer {
            tool_bar_title.text = it!!.name
            adapter?.updateProfile(it.profile)

        })
    }

//    https://medium.com/@bharathkumarbachina/avoid-boilerplate-code-with-kotlin-extensions-in-android-6a66e74787f2
        fun EditText.onChange(cb: (String) -> Unit) {
            this.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) { cb(s.toString()) }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }




    companion object {
        const val FROM_USER = "From_User"
        const val TO_USER = "To_User"
        const val MESSAGE_ID = "Main_Message_Id"

        fun start (context : Context, fromUser : String, toUser : String, mainMessageId : String){
            var intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(FROM_USER,fromUser)
            intent.putExtra(TO_USER,toUser)
            intent.putExtra(MESSAGE_ID,mainMessageId)
            context.startActivity(intent)
        }
    }



}
