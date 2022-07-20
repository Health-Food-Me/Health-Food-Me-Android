package org.helfoome.presentation

import androidx.lifecycle.*
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.entity.*
import org.helfoome.domain.repository.MapRepository
import org.helfoome.domain.repository.ProfileRepository
import org.helfoome.domain.repository.RestaurantRepository
import org.helfoome.util.Event
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sharedPreferences: HFMSharedPreference,
    private val restaurantRepository: RestaurantRepository,
    private val mapRepository: MapRepository,
    private val hfmSharedPreference: HFMSharedPreference,
) : ViewModel() {
    private val _location = MutableLiveData<List<MarkerInfo>>()
    val location: LiveData<List<MarkerInfo>> = _location
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

    // Menu
    private val _menu = MutableLiveData<List<MenuInfo>>()
    val menu: LiveData<List<MenuInfo>> = _menu
    private val _eatingOutTips = MutableLiveData<EatingOutTipInfo>()
    val eatingOutTips get() = _eatingOutTips

    init {
        fetchMenuList()
        fetchHFMReviewList()
        fetchBlogReviewList()
        initVisibleReviewButton()
    }

    fun getMapInfo(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { mapRepository.getMap(latLng.latitude, latLng.longitude, 11, null) }
                .onSuccess {
                    _location.postValue(
                        it.data.map { marker ->
                            marker.toMakerInfo()
                        }
                    )
                }.onFailure {
                    Timber.d(it.message)
                }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            runCatching { profileRepository.getProfile(sharedPreferences.id) }
                .onSuccess {
                    _nickname.value = it.data.name
                }
                .onFailure {
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
            val restaurantInfo = restaurantRepository.fetchRestaurantDetail(restaurantId, hfmSharedPreference.id, latitude, longitude).getOrNull() ?: return@launch
            _selectedRestaurant.postValue(restaurantInfo)
          //  _menu.postValue(restaurantInfo.menuList ?: return@launch)
            _eatingOutTips.value = restaurantRepository.getEatingOutTips(restaurantId)
        }
    }

    fun fetchHFMReviewList() {
        viewModelScope.launch(Dispatchers.IO) {
            _hfmReviews.postValue(restaurantRepository.fetchHFMReview(selectedRestaurant.value?.id ?: return@launch).getOrNull())
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
            val scrappedRestaurantList =
                restaurantRepository.updateRestaurantScrap(selectedRestaurant.value?.id!!, hfmSharedPreference.id) ?: return@launch
            _selectedRestaurant.postValue(_selectedRestaurant.value?.apply {
                this.isScrap = scrappedRestaurantList.contains(selectedRestaurant.value?.id!!)
            })
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

    private fun fetchMenuList() {
        // TODO remote에서 특정 식당 메뉴정보 불러오기
        _menu.value = listOf(
            MenuInfo("1", "연어 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_Xhu5dfOk_695bba70202d8f821fc2862641575ddced9316e9.jpg", 8400, 245, 26, true),
            MenuInfo("2", "탄단지 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_BfKg76Zq_efc7ededf088253bbceff22901cd7cf02baac553.jpg", 7600, 270, 27, true),
            MenuInfo("3", "리코타 치즈 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_zB4Hvtl7_a207fa684b30ebe9b56981dc1d88a1e5ae47322f.jpg", 8100, 462, 26, true),
            MenuInfo("4", "콥 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_1epsjcm2_d79e57ca53061076a729c03d00aa123312ca9d0f.jpg", 6300, 23, 22),
            MenuInfo("5", "치킨토마토 샌드위치", "https://salady.com/superboard/data/product/thumb/3731617857_P6qao2j4_1e3ff731c9a1d91414ac0bbc9ea101dc885d6bd5.jpg", 8200, 24, 23),
            MenuInfo("6", "맥시칸 랩", "https://salady.com/superboard/data/product/thumb/3731617857_gzSGH7wP_0faa0c8288336e920f21d8502e96c302a2b0c3f2.png", 8200, 25, 24),
            MenuInfo("7", "할라피뇨치킨 웜랩", null, 8200, 25, 24),
            MenuInfo("8", "포테이토 피자", null, 15200, 25, 24)
        )
    }

//    private fun fetchMenuList() {
//        // TODO remote에서 특정 식당 메뉴정보 불러오기
//        _menu.value = listOf(
//            MenuInfo(1, "연어 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_Xhu5dfOk_695bba70202d8f821fc2862641575ddced9316e9.jpg", 8400, 24, 26, 15, null, 50, true),
//            MenuInfo(2, "탄단지 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_BfKg76Zq_efc7ededf088253bbceff22901cd7cf02baac553.jpg", 7600, 27, 27, 18, 371, 60, true),
//            MenuInfo(3, "리코타 치즈 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_zB4Hvtl7_a207fa684b30ebe9b56981dc1d88a1e5ae47322f.jpg", 8100, 25, 26, 24, 462, 70, true),
//            MenuInfo(4, "콥 샐러디", "https://salady.com/superboard/data/product/thumb/3731617857_1epsjcm2_d79e57ca53061076a729c03d00aa123312ca9d0f.jpg", 6300, 23, 22, 15, 291, 70, true),
//            MenuInfo(5, "치킨토마토 샌드위치", "https://salady.com/superboard/data/product/thumb/3731617857_P6qao2j4_1e3ff731c9a1d91414ac0bbc9ea101dc885d6bd5.jpg", 8200, 24, 23, 16, 307, 60),
//            MenuInfo(6, "맥시칸 랩", "https://salady.com/superboard/data/product/thumb/3731617857_gzSGH7wP_0faa0c8288336e920f21d8502e96c302a2b0c3f2.png", 8200, 25, 24, 18, 307, 40),
//            MenuInfo(7, "할라피뇨치킨 웜랩", null, 8200, 25, 24, 18, 307, null),
//            MenuInfo(8, "포테이토 피자", null, 15200, 25, 24, 18, 307, 40, isGeneralMenu = true)
//        )
//    }

//    fun fetchEatingOutTips(restaurantId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _eatingOutTips.postValue(restaurantRepository.getEatingOutTips(restaurantId))
//        }
//    }
}
