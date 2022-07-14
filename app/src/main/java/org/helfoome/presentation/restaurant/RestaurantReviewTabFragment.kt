package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import org.helfoome.R
import org.helfoome.databinding.FragmentReviewBinding
import org.helfoome.util.binding.BindingFragment

class RestaurantReviewTabFragment : BindingFragment<FragmentReviewBinding>(R.layout.fragment_review) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
혀
        initView()
        initObservers()
    }

    private fun initView() {
        // TODO
    }

    private fun initObservers() {
        // TODO
    }
}
