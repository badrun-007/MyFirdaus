package com.badrun.my259firdaus.adapter

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.badrun.my259firdaus.fragment.BookmarkFragment
import com.badrun.my259firdaus.fragment.ListBookFragment
import com.badrun.my259firdaus.fragment.PeminjamanFragment


class ViewPagerAdapter(fa: FragmentActivity?) : FragmentStateAdapter(fa!!) {

    private val fragmentList = mutableListOf<Fragment>()
    init {
        fragmentList.add(ListBookFragment())
        fragmentList.add(BookmarkFragment())
        fragmentList.add(PeminjamanFragment())
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun updateFragment(position: Int, fragment: Fragment) {
        fragmentList[position] = fragment
        notifyDataSetChanged()
    }
}