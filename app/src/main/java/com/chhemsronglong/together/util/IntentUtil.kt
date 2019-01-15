package com.chhemsronglong.together.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.chhemsronglong.together.R

class IntentUtil {

    public fun startNewIntentActivity( context : Context, className : Class<Any>){
        val intent = Intent(context, className::class.java)
//        startActivity(intent)
    }



}

