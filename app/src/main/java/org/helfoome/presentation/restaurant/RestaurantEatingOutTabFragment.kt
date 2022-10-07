package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.FragmentEatingOutBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuAdapter
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantEatingOutTabFragment : BindingFragment<FragmentEatingOutBinding>(R.layout.fragment_eating_out) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }
}
