package org.helfoome.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.entity.*
import org.helfoome.domain.repository.MapRepository
import org.helfoome.domain.repository.ProfileRepository
import org.helfoome.domain.repository.RestaurantRepository
import org.helfoome.domain.repository.ReviewRepository
import org.helfoome.domain.usecase.ScrapListUseCase
import org.helfoome.presentation.type.ReviewType
import org.helfoome.util.Event
import org.helfoome.util.EventFlow
import org.helfoome.util.MutableEventFlow
import org.helfoome.util.ext.markerFilter
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val restaurantRepository: RestaurantRepository,
    private val reviewRepository: ReviewRepository,
    private val mapRepository: MapRepository,
    private val scrapListUseCase: ScrapListUseCase,
    private val hfmSharedPreference: HFMSharedPreference,
) : ViewModel() {
    private val _scrapList = MutableLiveData<List<MarkerInfo>>()
    val scrapList: LiveData<List<MarkerInfo>> = _scrapList
    private val _checkReview = MutableLiveData<Boolean>()
    val checkReview: LiveData<Boolean> = _checkReview

    private val _defaultLocation = mutableListOf<MarkerInfo>()
    val defaultLocation: List<MarkerInfo> = _defaultLocation
    private val _location = MutableLiveData<List<MarkerInfo>>()
    val location: LiveData<List<MarkerInfo>> = _location
    private var _fusedLocationSource: FusedLocationSource? = null
    val fusedLocationSource: FusedLocationSource? get() = _fusedLocationSource
    private val _isDietRestaurant = MutableLiveData<Boolean>()
    val isDietRestaurant: LiveData<Boolean> = _isDietRestaurant
    private val _cameraZoom = MutableLiveData<Event<Int>>()
    val cameraZoom: MutableLiveData<Event<Int>> = _cameraZoom
    private val _selectedRestaurant = MutableLiveData<RestaurantInfo>()
    val selectedRestaurant get() = _selectedRestaurant
    private val _restaurantId = MutableLiveData<String>()
    val restaurantId: LiveData<String> get() = _restaurantId

    private var _selectedRestaurantPoint = MutableLiveData<LocationPointInfo>()
    val selectedRestaurantPoint: LiveData<LocationPointInfo> get() = _selectedRestaurantPoint
    private var _currentPoint = MutableLiveData<LocationPointInfo>()
    val currentPoint: LiveData<LocationPointInfo> get() = _currentPoint

    private val _isExpandedDialog = MutableLiveData<Event<Boolean>>()
    val isExpandedDialog: LiveData<Event<Boolean>> get() = _isExpandedDialog
    private val storeIdHash = HashMap<LatLng, Int>()

    private val _nickname = MutableLiveData<String>()
    val nickname get() = _nickname

    // Review
    private val _hfmReviews = MutableLiveData<MutableList<HFMReviewInfo>>(mutableListOf())
    val hfmReviews: LiveData<MutableList<HFMReviewInfo>> = _hfmReviews
    private val _blogReviews = MutableLiveData<List<BlogReviewInfo>>()
    val blogReviews: LiveData<List<BlogReviewInfo>> = _blogReviews
    private val _isReviewTab = MutableLiveData(Event(false))
    val isReviewTab: LiveData<Event<Boolean>> get() = _isReviewTab
    private val _reviewType = MutableLiveData(Event(ReviewType.HFM_REVIEW))
    val reviewType: LiveData<Event<ReviewType>> get() = _reviewType

    // Menu
    private val _menu = MutableLiveData<List<MenuInfo>>()
    val menu: LiveData<List<MenuInfo>> = _menu
    private val _menuBoard = MutableLiveData<List<String>>()
    val menuBoard: LiveData<List<String>> = _menuBoard
    private val _isExistMenuBoard = MutableLiveData<Boolean>()
    val isExistMenuBoard: LiveData<Boolean> get() = _isExistMenuBoard

    private val _eatingOutTips = MutableLiveData<List<EatingOutTipInfo>>()
    val eatingOutTips get() = _eatingOutTips

    // Eating Out Tips
    private var _selectedFoodCategoryIdx = MutableLiveData<Int>()
    val selectedFoodCategoryIdx: LiveData<Int> get() = _selectedFoodCategoryIdx

    // Event
    private val _isReviewWriteSuccess = MutableEventFlow<Boolean>()
    val isReviewWriteSuccess: EventFlow<Boolean>
        get() = _isReviewWriteSuccess

    private val _behaviorState = MutableEventFlow<Int>()
    val behaviorState: EventFlow<Int>
        get() = _behaviorState

    private val _isDetailCollapsed = MutableEventFlow<Boolean>()
    val isDetailCollapsed: EventFlow<Boolean>
        get() = _isDetailCollapsed

    init {
        fetchHFMReviewList()
        fetchBlogReviewList()
    }

    fun getIsGuestLogin() = hfmSharedPreference.isGuestLogin

    fun setIsGuestLogin(isGuestLogin: Boolean) {
        hfmSharedPreference.isGuestLogin = isGuestLogin
    }

    fun setRestaurantId(restaurantId: String) {
        _restaurantId.value = restaurantId
    }

    fun setReviewWriteSuccess(isEnable: Boolean) {
        viewModelScope.launch {
            _isReviewWriteSuccess.emit(isEnable)
        }
    }

    fun setBehaviorState(behaviorState: Int) {
        viewModelScope.launch {
            _behaviorState.emit(behaviorState)
        }
    }

    fun setIsDetailCollapsed(isCollapsed: Boolean) {
        viewModelScope.launch {
            _isDetailCollapsed.emit(isCollapsed)
        }
    }

    fun getScrapList() {
        viewModelScope.launch {
            scrapListUseCase.execute(hfmSharedPreference.id)
                .onSuccess {
                    _scrapList.value = _location.value?.markerFilter(
                        it.map {
                            it.id
                        }
                    )
                }
                .onFailure { }
        }
    }

    fun getReviewCheck(restaurantId: String) {
        viewModelScope.launch {
            runCatching { reviewRepository.getReviewCheck(hfmSharedPreference.id, restaurantId) }
                .onSuccess {
                    _checkReview.value = it.data.hasReview
                }.onFailure {
                    Timber.d(it.message)
                }
        }
    }

    fun getMapInfo(latLng: LatLng, keyword: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { mapRepository.getMap(latLng.longitude, latLng.latitude, 1000, keyword) }
                .onSuccess {
                    _location.postValue(
                        it.data.map { marker ->
                            marker.toMakerInfo()
                        }
                    )
                    if (keyword == null) {
                        with(_defaultLocation) {
                            clear()
                            addAll(it.data.map { marker -> marker.toMakerInfo() })
                        }
                    }
                }.onFailure {
                    Timber.d(it.message)
                }
        }
    }

    fun setMapInfo(markerInfo: ArrayList<MarkerInfo>) {
        _location.value = markerInfo
    }

    fun setSelectedFoodCategoryIdx(idx: Int) {
        _selectedFoodCategoryIdx.value = idx
    }

    fun setLocationSource(locationSource: FusedLocationSource) {
        this._fusedLocationSource = locationSource
    }

    fun getProfile() {
        viewModelScope.launch {
            runCatching { profileRepository.getProfile(hfmSharedPreference.id) }
                .onSuccess {
                    _nickname.value = it.data.name
                }
                .onFailure {
                }
        }
    }

    fun markerId(position: LatLng) = storeIdHash[position]

    /** 선택된 식당 정보 불러오기 */
    fun fetchSelectedRestaurantInfo(restaurantId: String) {
        // TODO 추후 매개변수로 좌표값을 받아 해당 좌표 음식점 정보를 불러오기
        viewModelScope.launch {
            _selectedRestaurant.postValue(
                restaurantRepository.fetchRestaurantSummary(restaurantId, hfmSharedPreference.id)
            )
            _eatingOutTips.value = restaurantRepository.getEatingOutTips(restaurantId)
        }
    }

    /** @param slat : 유저의 위치좌표 중 위도
     * @param slng : 유저의 위치좌표 중 경도
     * */
    fun fetchSelectedRestaurantDetailInfo(restaurantId: String, slat: Double, slng: Double) {
        // TODO 추후 매개변수로 좌표값을 받아 해당 좌표 음식점 정보를 불러오기
        viewModelScope.launch {
            val userId = if (hfmSharedPreference.isGuestLogin) "browsing" else hfmSharedPreference.id
            val restaurantInfo =
                restaurantRepository.fetchRestaurantDetail(restaurantId, userId, slat, slng).getOrNull()
            restaurantInfo?.let {
                _selectedRestaurant.value = it
            }

            // 외식대처법 카테고리가 2개 이상인 경우, 인덱스 1이상에 해당하는 카테고리 클릭 후 새 레스토랑 핀을 클릭 할 경우, 선택된 외식대처법 카테고리 인덱스를 디폴트인 0으로 돌려놓기 위함. (새로고침)
            _selectedFoodCategoryIdx.value = 0
            _eatingOutTips.value = restaurantRepository.getEatingOutTips(restaurantId)
            _menu.value = restaurantInfo?.menuList?.sortedByDescending { it.isHealfoomePick }
            fetchHFMReviewList()
            restaurantInfo?.menuImages.let {
                if (it != null) _menuBoard.value = it
                _isExistMenuBoard.value = isExistMenuBoard(it)
            }
        }
    }

    fun isExistMenuBoard(boardList: List<String>?): Boolean {
        return !(boardList.isNullOrEmpty() || boardList == listOf(""))
    }

    fun fetchHFMReviewList() {
        viewModelScope.launch(Dispatchers.IO) {
            _hfmReviews.postValue(
                restaurantRepository.fetchHFMReview(selectedRestaurant.value?.id ?: return@launch).getOrNull()?.toMutableList()
            )
        }
    }

    fun fetchBlogReviewList() {
        viewModelScope.launch(Dispatchers.IO) {
            _blogReviews.postValue(restaurantRepository.fetchBlogReview(selectedRestaurant.value?.id ?: return@launch).getOrNull())
        }
    }

    fun updateRestaurantScrap() {
        viewModelScope.launch(Dispatchers.IO) {
            if (selectedRestaurant.value?.id == null) return@launch
            val isScrap =
                restaurantRepository.updateRestaurantScrap(selectedRestaurant.value?.id!!, hfmSharedPreference.id) ?: return@launch
            _selectedRestaurant.postValue(
                _selectedRestaurant.value?.apply {
                    this.isScrap = isScrap
                }
            )
        }
    }

    fun setExpendedBottomSheetDialog(isExpended: Boolean) {
        _isExpandedDialog.value = Event(isExpended)
    }

    fun setReviewTab(isReviewTab: Boolean) {
        _isReviewTab.value = Event(isReviewTab)
    }

    fun setReviewType(reviewType: ReviewType) {
        _reviewType.value = Event(reviewType)
    }

    /**
     * @param latitude : 선택한 레스토랑의 위치정보 중 위도
     * @param longitude : 선택한 레스토랑의 위치정보 중 경도
     * */
    fun setSelectedLocationPoint(latitude: Double, longitude: Double) {
        _selectedRestaurantPoint.value = LocationPointInfo(latitude, longitude)
    }

    fun setCurrentLocationPoint(latitude: Double, longitude: Double) {
        _currentPoint.value = LocationPointInfo(latitude, longitude)
    }

    fun addHFMReviewList(review: HFMReviewInfo) {
        _hfmReviews.value?.add(0, review)
        _hfmReviews.value = hfmReviews.value
    }
}
