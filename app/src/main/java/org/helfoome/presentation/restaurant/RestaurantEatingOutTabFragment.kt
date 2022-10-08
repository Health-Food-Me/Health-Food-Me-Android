package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.helfoome.R
import org.helfoome.databinding.FragmentEatingOutBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantEatingOutTabFragment : BindingFragment<FragmentEatingOutBinding>(R.layout.fragment_eating_out) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        viewModel.isDetailCollapsed
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { isCollapsed ->
                if (isCollapsed)
                    binding.svEatingOut.fullScroll(ScrollView.FOCUS_UP)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
