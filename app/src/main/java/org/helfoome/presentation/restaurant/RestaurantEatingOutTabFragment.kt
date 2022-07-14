package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import org.helfoome.R
import org.helfoome.databinding.FragmentEatingOutBinding
import org.helfoome.presentation.type.EatingOutAdviseType
import org.helfoome.util.binding.BindingFragment

class RestaurantEatingOutTabFragment : BindingFragment<FragmentEatingOutBinding>(R.layout.fragment_eating_out) {
    private val viewModel: RestaurantMenuViewModel by viewModels()
    private val restaurantMenuAdapter = RestaurantMenuAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObservers()
    }

    private fun initView() {
        binding.viewEatingOutAdviseRecommendation.setAdviseList(listOf("먹는 속도가 자연스럽게 느려져요",
            "다양한 채소와 단백질 섭취가 가능해요",
            "채소, 버섯, 고기, 해산물 충분히 드실 수 있어요\n" +
                    "아니 못 참지 ㅋㅋ"), EatingOutAdviseType.RECOMMENDATION)
        binding.viewEatingOutAdviseHowToEat.setAdviseList(
            listOf("소스 섭취를 최소화 하세요", "떡, 어묵, 만두, 단호박은 투입을 자제하세요", "칼국수와 죽, 볶음밥 주문은 잠시 참아봐요"), EatingOutAdviseType.HOW_TO_EAT)
    }

    private fun initObservers() {
        viewModel.menu.observe(viewLifecycleOwner) { menuList ->
            restaurantMenuAdapter.menuList = menuList
        }
    }
}
