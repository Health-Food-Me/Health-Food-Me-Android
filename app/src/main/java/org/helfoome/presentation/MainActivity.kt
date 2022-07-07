package org.helfoome.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityMainBinding
import org.helfoome.presentation.restaurant.RestaurantTabAdapter
import org.helfoome.util.binding.BindingActivity

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var behavior: BottomSheetBehavior<NestedScrollView>
    private val restaurantDetailAdapter = RestaurantTabAdapter(this)
    private val tabTitles = arrayOf(
        R.string.restaurant_detail_tab_item_menu,
        R.string.restaurant_detail_tab_item_how_to_eat_out,
        R.string.restaurant_detail_tab_item_review,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@MainActivity

        initView()
        initListeners()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.bottomSheet)

        with(binding.layoutRestaurantDialog) {
            vpRestaurantDetail.adapter = restaurantDetailAdapter
            TabLayoutMediator(layoutRestaurantTabMenu, vpRestaurantDetail) { tab, position ->
                tab.text = getString(tabTitles[position])
            }.attach()
        }
    }

    private fun initListeners() {
        binding.layoutRestaurantDialog.btnScrap.setOnClickListener {
            it.isSelected = !it.isSelected
            // TODO 스크랩 상태값 업데이트 api 요청
        }
    }

    private fun initObservers() {
        viewModel.selectedRestaurant.observe(this) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onPause() {
        super.onPause()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            viewModel.setExpendedBottomSheetDialog(newState == BottomSheetBehavior.STATE_EXPANDED)
        }

        override fun onSlide(bottomSheetView: View, slideOffset: Float) {
        }
    }
}
