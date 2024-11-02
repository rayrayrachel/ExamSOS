package com.example.examsos.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.examsos.FragmentHome
import com.example.examsos.FragmentLevels
import com.example.examsos.FragmentNotes
import com.example.examsos.FragmentNotifications

class TabsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val myTag = "Rachel'sTag"

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> FragmentHome().also { Log.i(myTag, "*** Home Fragment: Creating FragmentHome") }
            1 -> FragmentLevels().also { Log.i(myTag, "*** Home Fragment: Creating FragmentLevels") }
            2 -> FragmentNotes().also { Log.i(myTag, "*** Home Fragment: Creating FragmentNotes") }
            3 -> FragmentNotifications().also { Log.i(myTag, "*** Home Fragment: Creating FragmentNotifications") }
            else -> FragmentHome().also { Log.i(myTag, "*** Home Fragment: Defaulting to FragmentHome") }
        }
        Log.i(myTag, "*** Home Fragment: createFragment: Position $position, Fragment: ${fragment::class.java.simpleName}")
        return fragment
    }
}
