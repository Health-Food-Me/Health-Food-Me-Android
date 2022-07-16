package org.helfoome.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.util.Event
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _location = MutableLiveData<List<LatLng>>()
    val location: LiveData<List<LatLng>> = _location
    private val _isDietRestaurant = MutableLiveData<Boolean>()
    val isDietRestaurant: LiveData<Boolean> = _isDietRestaurant
    private val _cameraZoom = MutableLiveData<Event<Int>>()
    val cameraZoom: MutableLiveData<Event<Int>> = _cameraZoom
    private val _selectedRestaurant = MutableLiveData<RestaurantInfo>()
    val selectedRestaurant get() = _selectedRestaurant
    private val _isExpandedDialog = MutableLiveData<Event<Boolean>>()
    val isExpandedDialog: LiveData<Event<Boolean>> get() = _isExpandedDialog
    private val storeIdHash = HashMap<LatLng, Int>()

    fun markerId(position: LatLng) = storeIdHash[position]

    init {
        // TODO 지도 뷰 구현 후 마커 클릭 시 해당 함수 호출하는 것으로 변경 예정
        fetchSelectedRestaurantInfo()
    }

    /** 선택된 식당 정보 불러오기 */
    fun fetchSelectedRestaurantInfo() {
        // TODO 추후 매개변수로 좌표값을 받아 해당 좌표 음식점 정보를 불러오기
        _selectedRestaurant.value = RestaurantInfo(
            id = 1,
            image = "https://salady.com/superboard/data/siteconfig/2021021809004816136064486235.jpg",
            name = "샐러디",
            score = 4.8f,
            tags = listOf("샐러드", "샌드위치", "랩"),
            category = "샐러드",
            location = "서울특별시 중랑구 상봉동",
            time = listOf(
                "화요일 10:00 ~ 22:00",
                "수요일 10:00 ~ 22:00",
                "목요일 10:00 ~ 22:00",
                "금요일 10:00 ~ 22:00",
                "토요일 10:00 ~ 22:00",
                "일요일 10:00 ~ 22:00",
                "월요일 10:00 ~ 22:00"
            ),

            number = "02-123-123"
        )
    }

    fun setExpendedBottomSheetDialog(isExpended: Boolean) {
        _isExpandedDialog.value = Event(isExpended)
    }
}
