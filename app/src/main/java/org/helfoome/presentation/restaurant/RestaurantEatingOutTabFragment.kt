package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.FragmentEatingOutBinding
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuAdapter
import org.helfoome.presentation.restaurant.viewmodel.RestaurantMenuViewModel
import org.helfoome.presentation.type.EatingOutTipType
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantEatingOutTabFragment : BindingFragment<FragmentEatingOutBinding>(R.layout.fragment_eating_out) {
    private val viewModel: RestaurantMenuViewModel by viewModels()
    private val restaurantMenuAdapter = RestaurantMenuAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        initObservers()
    }

    private fun initObservers() {
        viewModel.menu.observe(viewLifecycleOwner) { menuList ->
            restaurantMenuAdapter.menuList = menuList
        }

        viewModel.eatingOutTips.observe(viewLifecycleOwner) { tips ->
            binding.viewRecommendationTipList.setTips(tips.recommendTips, EatingOutTipType.RECOMMENDATION_TIP)
            binding.viewEatingTipList.setTips(tips.eatingTips, EatingOutTipType.EATING_TIP)
        }
    }
}
