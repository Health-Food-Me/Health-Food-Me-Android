package org.helfoome.presentation.restaurant

import android.content.Intent
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
import org.helfoome.databinding.FragmentMenuBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.common.ImageViewerActivity
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuAdapter
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuBoardAdapter
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantMenuTabFragment : BindingFragment<FragmentMenuBinding>(R.layout.fragment_menu) {
    private val viewModel: MainViewModel by activityViewModels()
    private val restaurantMenuAdapter = RestaurantMenuAdapter()
    private val restaurantMenuBoardAdapter = RestaurantMenuBoardAdapter(::moveToImageViewer)

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
            if (viewModel.isExistMenuBoard(menuBoardList)) {
                restaurantMenuBoardAdapter.menuBoardList = menuBoardList
            }
        }

        viewModel.isDetailCollapsed
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { isCollapsed ->
                if (isCollapsed)
                    binding.svMenu.fullScroll(ScrollView.FOCUS_UP)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun moveToImageViewer(menuBoardList: List<String>, position: Int) {
        Intent(requireContext(), ImageViewerActivity::class.java).apply {
            putExtra(ARG_IMAGE_LIST, menuBoardList.toTypedArray())
            putExtra(ARG_IMAGE_POSITION, position)
        }.also {
            startActivity(it)
        }
    }

    companion object {
        private const val ARG_IMAGE_LIST = "imageList"
        private const val ARG_IMAGE_POSITION = "imagePosition"
    }
}
