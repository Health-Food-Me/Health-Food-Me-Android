package org.helfoome.presentation.restaurant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.domain.entity.EatingOutTipInfo
import org.helfoome.domain.entity.MenuInfo
import org.helfoome.domain.repository.RestaurantRepository
import javax.inject.Inject

@HiltViewModel
class RestaurantMenuViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
) : ViewModel() {
    private val _menu = MutableLiveData<List<MenuInfo>>()
    val menu: LiveData<List<MenuInfo>> = _menu
    private val _eatingOutTips = MutableLiveData<EatingOutTipInfo>()
    val eatingOutTips get() = _eatingOutTips

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
            MenuInfo(7, "할라피뇨치킨 웜랩", null, 8200, 25, 24, 18, 307, null),
            MenuInfo(8, "포테이토 피자", null, 15200, 25, 24, 18, 307, 40, isGeneralMenu = true)
        )
    }

    private fun fetchEatingOutTips() {
        // TODO 레스토랑 id 받아오기
        viewModelScope.launch(Dispatchers.IO) {
            _eatingOutTips.postValue(restaurantRepository.getEatingOutTips("62d26c9bd11146a81ef18ea6"))
        }
    }
}
