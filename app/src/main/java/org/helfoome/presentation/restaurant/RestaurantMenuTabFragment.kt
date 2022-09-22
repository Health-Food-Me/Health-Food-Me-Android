package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.FragmentMenuBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuAdapter
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuBoardAdapter
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantMenuTabFragment : BindingFragment<FragmentMenuBinding>(R.layout.fragment_menu) {
    private val viewModel: MainViewModel by activityViewModels()
    private val restaurantMenuAdapter = RestaurantMenuAdapter()
    private val restaurantMenuBoardAdapter = RestaurantMenuBoardAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        initView()
        initObservers()
    }

    private fun initView() {
        binding.menuList.adapter = restaurantMenuAdapter
        binding.rvMenuBoardList.adapter = restaurantMenuBoardAdapter
    }

    private fun initObservers() {
        viewModel.menu.observe(viewLifecycleOwner) { menuList ->
            if (menuList == null) return@observe
            restaurantMenuAdapter.menuList = menuList
        }

        viewModel.menuBoard.observe(viewLifecycleOwner) { menuBoardList ->
            if (menuBoardList.isNullOrEmpty()) return@observe
            restaurantMenuBoardAdapter.menuBoardList = menuBoardList
        }
    }
}
