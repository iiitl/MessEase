package com.theayushyadav11.MessEase.ui.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theayushyadav11.MessEase.ui.MessCommittee.fragments.MsgFragment
import com.theayushyadav11.MessEase.ui.MessCommittee.fragments.PollsFragment


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PollsFragment()
            else -> MsgFragment()
        }

    }
}