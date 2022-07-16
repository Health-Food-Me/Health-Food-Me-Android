package org.helfoome.presentation.restaurant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.helfoome.domain.entity.MenuInfo
import javax.inject.Inject

@HiltViewModel
class RestaurantMenuViewModel @Inject constructor() : ViewModel() {
    private val _menu = MutableLiveData<List<MenuInfo>>()
    val menu: LiveData<List<MenuInfo>> = _menu
    private val _recommendationTips = MutableLiveData<List<String>>()
    val recommendationTips: LiveData<List<String>> = _recommendationTips
    private val _eatingTips = MutableLiveData<List<String>>()
    val eatingTips: LiveData<List<String>> = _eatingTips

    init {
        fetchMenuList()
        fetchEatingOutTips()
    }

    private fun fetchMenuList() {
        // TODO remote에서 특정 식당 메뉴정보 불러오기
        _menu.value = listOf(
            MenuInfo(1, "연어 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_Xhu5dfOk_695bba70202d8f821fc2862641575ddced9316e9.jpg", 8400, 24, 26, 15, null, 50, true),
            MenuInfo(2, "탄단지 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_BfKg76Zq_efc7ededf088253bbceff22901cd7cf02baac553.jpg", 7600, 27, 27, 18, 371, 60, true),
            MenuInfo(3, "리코타 치즈 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_zB4Hvtl7_a207fa684b30ebe9b56981dc1d88a1e5ae47322f.jpg", 8100, 25, 26, 24, 462, 70, true),
            MenuInfo(4, "콥 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_1epsjcm2_d79e57ca53061076a729c03d00aa123312ca9d0f.jpg", 6300, 23, 22, 15, 291, 70, true),
            MenuInfo(5, "치킨토마토 샌드위치", "https://salady.com/superboard/data/product/thumb/3731617857_P6qao2j4_1e3ff731c9a1d91414ac0bbc9ea101dc885d6bd5.jpg", 8200, 24, 23, 16, 307, 60),
            MenuInfo(6, "맥시칸 랩", "https://salady.com/superboard/data/product/thumb/3731617857_gzSGH7wP_0faa0c8288336e920f21d8502e96c302a2b0c3f2.png", 8200, 25, 24, 18, 307, 40),
            MenuInfo(7, "할라피뇨치킨 웜랩", null, 8200, 25, 24, 18, 307, 40)
        )
    }

    private fun fetchEatingOutTips() {
        _recommendationTips.value = listOf("먹는 속도가 자연스럽게 느려져요", "다양한 채소와 단백질 섭취가 가능해요", "채소, 버섯, 고기, 해산물 충분히 드실 수 있어요 아니 못 참지 ㅋㅋ")
        _eatingTips.value = listOf("소스 섭취를 최소화 하세요", "떡, 어묵, 만두, 단호박은 투입을 자제하세요", "칼국수와 죽, 볶음밥 주문은 잠시 참아봐요")
    }
}
