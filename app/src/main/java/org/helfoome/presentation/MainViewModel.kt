package org.helfoome.presentation

import androidx.lifecycle.*
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.domain.entity.HFMReviewInfo
import org.helfoome.domain.entity.MarkerInfo
import org.helfoome.domain.entity.RestaurantInfo
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

    init {
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
            runCatching { profileRepository.getProfile(sharedPreferences.nickname) }
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
        viewModelScope.launch(Dispatchers.IO) {
            _selectedRestaurant.postValue(
                restaurantRepository.fetchRestaurantSummary(
                    "62d26c9bd11146a81ef18ea6",
                    "62cf2c468ae4a7cda10f4f4f"
                )
            )
        }
    }

    fun fetchHFMReviewList() {
        viewModelScope.launch(Dispatchers.IO) {
            _hfmReviews.postValue(restaurantRepository.fetchHFMReview("62d26c9bd11146a81ef18ea6").getOrNull())
        }
    }

    fun fetchBlogReviewList() {
        viewModelScope.launch(Dispatchers.IO) {
            _blogReviews.postValue(restaurantRepository.fetchBlogReview("62d26c9bd11146a81ef18ea6").getOrNull())
        }
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
