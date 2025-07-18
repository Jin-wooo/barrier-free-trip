package com.triply.barrierfreetrip.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.triply.barrierfreetrip.PickListFragment

class PickPageAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {
    private var fragments: ArrayList<PickListFragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): PickListFragment {
        return fragments[position]
    }

    fun addFragment(fragment: PickListFragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size-1)
    }

    fun removeFragment() {
        fragments.removeAt(fragments.lastIndex)
        notifyItemRemoved(fragments.size)
    }
}