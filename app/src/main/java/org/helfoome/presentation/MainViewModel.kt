package org.helfoome.presentation

import androidx.lifecycle.*
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.domain.entity.ReviewInfo
import org.helfoome.domain.repository.RestaurantRepository
import org.helfoome.util.Event
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val restaurantRepository: RestaurantRepository) : ViewModel() {
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

    private val _reviews = MutableLiveData<List<ReviewInfo>>()
    val reviews: LiveData<List<ReviewInfo>> = _reviews
    private val _blogReviews = MutableLiveData<List<BlogReviewInfo>>()
    val blogReviews: LiveData<List<BlogReviewInfo>> = _blogReviews
    private val isGeneralReview = MutableLiveData(true)
    private val isReviewTab = MutableLiveData(false)
    private val _isVisibleReviewButton = MediatorLiveData<Event<Boolean>>()
    val isVisibleReviewButton get() = _isVisibleReviewButton

    init {
        // TODO 지도 뷰 구현 후 마커 클릭 시 해당 함수 호출하는 것으로 변경 예정
        fetchSelectedRestaurantInfo()
        fetchReviewList()
        initVisibleReviewButton()
    }

    private fun initVisibleReviewButton() {
        _isVisibleReviewButton.addSource(isGeneralReview) { isGeneral ->
            _isVisibleReviewButton.value = Event(combineVisibleReviewButton(isGeneral, isReviewTab.value ?: return@addSource))
        }
        _isVisibleReviewButton.addSource(isReviewTab) { isReviewTab ->
            _isVisibleReviewButton.value = Event(combineVisibleReviewButton(isGeneralReview.value ?: return@addSource, isReviewTab))
        }
    }

    private fun combineVisibleReviewButton(isGeneralReview: Boolean, isVisibleReviewButton: Boolean): Boolean =
        isGeneralReview && isVisibleReviewButton

    fun markerId(position: LatLng) = storeIdHash[position]

    /** 선택된 식당 정보 불러오기 */
    fun fetchSelectedRestaurantInfo() {
        // TODO 추후 매개변수로 좌표값을 받아 해당 좌표 음식점 정보를 불러오기
        viewModelScope.launch(Dispatchers.IO) {
            _selectedRestaurant.postValue(restaurantRepository.fetchRestaurantSummary("62d26c9bd11146a81ef18ea6",
                "62cf2c468ae4a7cda10f4f4f"))
        }
    }

    fun fetchHealFoodRestaurantLocation() {
        // TODO fetch location data from remote
        _location.value = listOf(
            LatLng(37.7523167, 127.0711813),
            LatLng(37.7509459, 127.0733813),
            LatLng(37.7516837, 127.0749479),
            LatLng(37.7513456, 127.0678897),
            LatLng(37.7488591, 127.0677059),
            LatLng(37.5915564, 127.0215812),
            LatLng(37.7523167, 127.0711813),
            LatLng(37.5683199, 126.9789914),
            LatLng(37.5735465, 126.9843782),
            LatLng(37.5721776, 126.9907629),
            LatLng(37.5713466, 126.9755946),
            LatLng(37.5755447, 126.9732612),
            LatLng(37.5221133, 126.9258818),
            LatLng(37.5159831, 126.9207751),
            LatLng(37.5217754, 126.9068775),
            LatLng(37.5175996, 126.9132172),
            LatLng(37.5150195, 126.9089220),
            LatLng(37.5168713, 126.9035354),
            LatLng(37.5160904, 126.8963763)
        )
    }

    fun fetchReviewList() {
        _reviews.value = listOf(ReviewInfo(1, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(2, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(3, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(4, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(5, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(6, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(7, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(8, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
        )
    }

    fun fetchBlogReviewList() {
        _blogReviews.value = listOf(
            BlogReviewInfo(1, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "),
            BlogReviewInfo(2, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "),
            BlogReviewInfo(3, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "),
            BlogReviewInfo(4, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "),
            BlogReviewInfo(5, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … ")
        )
    }

    fun setExpendedBottomSheetDialog(isExpended: Boolean) {
        _isExpandedDialog.value = Event(isExpended)
    }

    fun setGeneralReview(isGeneral: Boolean) {
        isGeneralReview.value = isGeneral
    }

    fun setReviewTab(isReviewTab: Boolean) {
        this.isReviewTab.value = isReviewTab
    }
}
