package com.chhemsronglong.together.HomeTabFragement

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.chhemsronglong.together.MessageTab.AlertFragment
import com.chhemsronglong.together.MessageTab.MessageFragment

class MessageTabAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MessageFragment()
            }
            else -> {
                return AlertFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Messages"
            else -> {
                return "Alerts"
            }
        }
    }
}