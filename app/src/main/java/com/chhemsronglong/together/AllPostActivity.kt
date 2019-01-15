package com.chhemsronglong.together

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import com.chhemsronglong.together.HomeTabFragement.ButtomNavigationActivity

class AllPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_post)


        val intent = Intent(this, ButtomNavigationActivity::class.java)
        val message = "Goto Buttom"

        intent.putExtra(AlarmClock.EXTRA_MESSAGE, message)
        // startActivityForResult(intent, TEXT_REQUEST)
        startActivity(intent)
    }
}
