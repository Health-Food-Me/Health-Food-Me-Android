package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import org.helfoome.R
import org.helfoome.databinding.FragmentEatingOutBinding
import org.helfoome.presentation.type.EatingOutTipType
import org.helfoome.util.binding.BindingFragment

class RestaurantEatingOutTabFragment : BindingFragment<FragmentEatingOutBinding>(R.layout.fragment_eating_out) {
    private val viewModel: RestaurantMenuViewModel by viewModels()
    private val restaurantMenuAdapter = RestaurantMenuAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
    }

    private fun initObservers() {
        viewModel.menu.observe(viewLifecycleOwner) { menuList ->
            restaurantMenuAdapter.menuList = menuList
        }

        viewModel.recommendationTips.observe(viewLifecycleOwner) { recommendationTips ->
            binding.viewRecommendationTipList.setTips(recommendationTips, EatingOutTipType.RECOMMENDATION)
        }

        viewModel.eatingTips.observe(viewLifecycleOwner) { eatingTips ->
            binding.viewEatingTipList.setTips(eatingTips, EatingOutTipType.HOW_TO_EAT)
        }
    }
}
