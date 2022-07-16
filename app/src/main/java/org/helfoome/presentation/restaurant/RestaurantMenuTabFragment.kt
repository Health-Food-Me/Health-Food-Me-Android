package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import org.helfoome.R
import org.helfoome.databinding.FragmentMenuBinding
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuAdapter
import org.helfoome.presentation.restaurant.viewmodel.RestaurantMenuViewModel
import org.helfoome.util.binding.BindingFragment

class RestaurantMenuTabFragment : BindingFragment<FragmentMenuBinding>(R.layout.fragment_menu) {
    private val viewModel: RestaurantMenuViewModel by viewModels()
    private val restaurantMenuAdapter = RestaurantMenuAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObservers()
    }

    private fun initView() {
        binding.menuList.adapter = restaurantMenuAdapter
    }

    private fun initObservers() {
        viewModel.menu.observe(viewLifecycleOwner) { menuList ->
            restaurantMenuAdapter.menuList = menuList
        }
    }
}
