package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.FragmentEatingOutBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuAdapter
import org.helfoome.presentation.type.EatingOutTipType
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantEatingOutTabFragment : BindingFragment<FragmentEatingOutBinding>(R.layout.fragment_eating_out) {
    private val viewModel: MainViewModel by activityViewModels()
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

        // TODO 뷰모델에서 처리하기
        viewModel.eatingOutTips.observe(viewLifecycleOwner) { tips ->
            if(tips.recommendTips == null || tips.eatingTips == null) {
                binding.layoutEmptyView.layoutContainer.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.INVISIBLE
            } else {
                binding.layoutEmptyView.layoutContainer.visibility = View.INVISIBLE
                binding.layoutContent.visibility = View.VISIBLE
                binding.viewRecommendationTipList.setTips(tips.recommendTips, EatingOutTipType.RECOMMENDATION_TIP)
                binding.viewEatingTipList.setTips(tips.eatingTips, EatingOutTipType.EATING_TIP)
            }
        }
    }

    companion object {
        private const val ARG_RESTAURANT_ID = "restaurantId"
    }
}
