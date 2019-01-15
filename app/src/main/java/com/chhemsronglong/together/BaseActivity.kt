package com.chhemsronglong.together

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import com.chhemsronglong.together.Common.BaseViewModel
import com.chhemsronglong.together.Common.CommonViewModel
import com.chhemsronglong.together.DialogCustom.showAlertDialog
import com.chhemsronglong.together.DialogCustom.showNotesAlertDialog
import com.chhemsronglong.together.ViewLayout.TogetherApp
import com.chhemsronglong.together.Common.ViewModelFactory
import com.chhemsronglong.together.util.DiaLogType
import org.jetbrains.anko.toast
import showVLog

open class BaseActivity: AppCompatActivity() {

    lateinit var commonViewModel: CommonViewModel

    open var progressBar : ProgressBar? = null
    open var diaLog: android.app.AlertDialog? = null


    fun showProgressDialog() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressDialog() {
        progressBar?.visibility = View.GONE
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
        commonViewModel.errorMessage.observe(this, Observer {
            it?.let {
                toast(it)
            }
        })
    }

    inline fun <reified T : BaseViewModel> initViewModel(): T =
            ViewModelProviders.of(this, ViewModelFactory(
                    application as TogetherApp,
                    commonViewModel,
                    commonViewModel)).get(T::class.java)


}