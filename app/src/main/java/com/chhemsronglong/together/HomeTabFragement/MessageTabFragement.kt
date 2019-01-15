package com.chhemsronglong.together.HomeTabFragement

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chhemsronglong.together.AllPostsActivity.PostDetailActivity
import com.chhemsronglong.together.R
//import kotlinx.android.synthetic.main.content_message.*
import kotlinx.android.synthetic.main.fragment_message_tab_fragement.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MessageTabFragement.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MessageTabFragement.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MessageTabFragement : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_tab_fragement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentAdapter = MessageTabAdapter(childFragmentManager)
        viewpager_main.adapter = fragmentAdapter

        tabs_main.setupWithViewPager(viewpager_main)


    }


}
