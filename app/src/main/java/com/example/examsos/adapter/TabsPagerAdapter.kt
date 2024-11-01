package com.example.examsos.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.examsos.HomeFragment
import com.example.examsos.LevelFragment
import com.example.examsos.NotesFragment
import com.example.examsos.NotificationFragment

class TabsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> LevelFragment()
            2 -> NotesFragment()
            3 -> NotificationFragment()
            else -> HomeFragment()
        }
    }
}
