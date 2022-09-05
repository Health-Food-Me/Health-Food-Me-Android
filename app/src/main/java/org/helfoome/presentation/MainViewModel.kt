package org.helfoome.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
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
    private val _eatingOutTips = MutableLiveData<EatingOutTipInfo>()
    val eatingOutTips get() = _eatingOutTips

    init {
        fetchHFMReviewList()
        fetchBlogReviewList()
    }

    fun setRestaurantId(restaurantId: String) {
        _restaurantId.value = restaurantId
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
            runCatching { mapRepository.getMap(latLng.latitude, latLng.longitude, 12, keyword) }
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

    fun fetchSelectedRestaurantDetailInfo(restaurantId: String, latitude: Double, longitude: Double) {
        // TODO 추후 매개변수로 좌표값을 받아 해당 좌표 음식점 정보를 불러오기
        viewModelScope.launch {
            val restaurantInfo =
                restaurantRepository.fetchRestaurantDetail(restaurantId, hfmSharedPreference.id, latitude, longitude).getOrNull()
            restaurantInfo?.let {
                _selectedRestaurant.postValue(it)
            }
            _eatingOutTips.value = restaurantRepository.getEatingOutTips(restaurantId)
            _menu.value = restaurantInfo?.menuList?.sortedByDescending { it.isHealfoomePick }
        }
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
            _blogReviews.postValue(restaurantRepository.fetchBlogReview(selectedRestaurant.value?.name ?: return@launch).getOrNull())
        }
    }

    fun updateRestaurantScrap() {
        viewModelScope.launch(Dispatchers.IO) {
            if (selectedRestaurant.value?.id == null) return@launch
            val scrappedRestaurantList =
                restaurantRepository.updateRestaurantScrap(selectedRestaurant.value?.id!!, hfmSharedPreference.id) ?: return@launch
            _selectedRestaurant.postValue(
                _selectedRestaurant.value?.apply {
                    this.isScrap = scrappedRestaurantList.contains(selectedRestaurant.value?.id!!)
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
