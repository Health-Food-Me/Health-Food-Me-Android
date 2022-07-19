package org.helfoome.presentation

import androidx.lifecycle.*
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.domain.entity.HFMReviewInfo
import org.helfoome.domain.repository.ProfileRepository
import org.helfoome.domain.repository.RestaurantRepository
import org.helfoome.util.Event
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sharedPreferences: HFMSharedPreference,
    private val restaurantRepository: RestaurantRepository,
) : ViewModel() {
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

    private val _nickname = MutableLiveData<String>()
    val nickname get() = _nickname

    // Review
    private val _hfmReviews = MutableLiveData<List<HFMReviewInfo>>()
    val hfmReviews: LiveData<List<HFMReviewInfo>> = _hfmReviews
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

    fun getProfile() {
        viewModelScope.launch {
            runCatching { profileRepository.getProfile(sharedPreferences.id) }
                .onSuccess {
                    _nickname.value = it.data.name
                    cancel()
                }
                .onFailure {
                    cancel()
                }
        }
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
            _selectedRestaurant.postValue(
                restaurantRepository.fetchRestaurantSummary(
                    "62d26c9bd11146a81ef18ea6",
                    "62cf2c468ae4a7cda10f4f4f"
                )
            )
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
        viewModelScope.launch(Dispatchers.IO) {
            _hfmReviews.postValue(restaurantRepository.fetchHFMReview("62d26c9bd11146a81ef18ea6").getOrNull())
        }
    }

    fun fetchBlogReviewList() {
        _blogReviews.value = listOf(
            BlogReviewInfo(
                1,
                "샐러디 안암점 나쵸가 씹히는 멕시칸랩",
                "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "
            ),
            BlogReviewInfo(
                2,
                "샐러디 안암점 나쵸가 씹히는 멕시칸랩",
                "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "
            ),
            BlogReviewInfo(
                3,
                "샐러디 안암점 나쵸가 씹히는 멕시칸랩",
                "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "
            ),
            BlogReviewInfo(
                4,
                "샐러디 안암점 나쵸가 씹히는 멕시칸랩",
                "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "
            ),
            BlogReviewInfo(
                5,
                "샐러디 안암점 나쵸가 씹히는 멕시칸랩",
                "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … "
            )
        )
    }

    fun updateRestaurantScrap() {
        viewModelScope.launch(Dispatchers.IO) {
            val isScrap =
                restaurantRepository.updateRestaurantScrap(selectedRestaurant.value?.id ?: return@launch, "62cf2c468ae4a7cda10f4f4f")
                    ?: return@launch
            _selectedRestaurant.postValue(_selectedRestaurant.value?.apply { this.isScrap = isScrap })
        }
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
