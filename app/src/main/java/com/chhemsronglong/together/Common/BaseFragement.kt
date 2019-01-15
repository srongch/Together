package com.chhemsronglong.together.Common

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ProgressBar
import com.chhemsronglong.together.ViewLayout.TogetherApp
import com.chhemsronglong.together.Common.ViewModelFactory


open class BaseFragement : Fragment() {
    // TODO: Rename and change types of parameters
    open var progressBar : ProgressBar? = null
    lateinit var commonViewModel: CommonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        performDependencyInjection()
//        setHasOptionsMenu(false)

        commonViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
//        commonViewModel.errorMessage.observe(this,android.arch.lifecycle.Observer{
//            it?.let {
//                toast(it)
//            }
//        }


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setUp()
    }

    fun showProgressDialog() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressDialog() {
        progressBar?.visibility = View.GONE
    }

    inline fun <reified T : BaseViewModel> initViewModel(): T =
            ViewModelProviders.of(this, ViewModelFactory(
                    activity!!.application as TogetherApp,
                    commonViewModel,
                    commonViewModel)).get(T::class.java)


}
