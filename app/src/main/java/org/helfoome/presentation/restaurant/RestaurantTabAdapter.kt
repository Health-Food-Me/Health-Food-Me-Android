package org.helfoome.presentation.restaurant

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RestaurantTabAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3

    // TODO 뷰 확정된 이후 변경 예정
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RestaurantMenuTabFragment()
            1 -> RestaurantMenuTabFragment()
            else -> RestaurantMenuTabFragment()
        }
    }
}
