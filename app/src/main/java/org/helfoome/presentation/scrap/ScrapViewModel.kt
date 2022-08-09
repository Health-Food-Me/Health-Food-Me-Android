package org.helfoome.presentation.scrap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.entity.MarkerInfo
import org.helfoome.domain.entity.ScrapInfo
import org.helfoome.domain.repository.ScrapRepository
import org.helfoome.util.ext.markerFilter
import javax.inject.Inject

@HiltViewModel
class ScrapViewModel @Inject constructor(
    private val storage: HFMSharedPreference,
    private val scrapRepository: ScrapRepository,
) : ViewModel() {
    // TODO : 초기화 로직 바꿀필요 있음(Sealed Class로)
    private val _scrapMarkerList = MutableStateFlow<List<MarkerInfo>>(listOf())
    val scrapMarkerList: StateFlow<List<MarkerInfo>>
        get() = _scrapMarkerList.asStateFlow()

    private val _location = MutableLiveData<List<MarkerInfo>>()
    val location: LiveData<List<MarkerInfo>> = _location

    private val _scrapUiState =
        MutableStateFlow<ScrapUiState>(ScrapUiState.Loading)
    val scrapUiState: StateFlow<ScrapUiState>
        get() = _scrapUiState.asStateFlow()

    fun getScrapList() {
        viewModelScope.launch {
            scrapRepository.getScrapList(storage.id)
                .onSuccess {
                    if (it.isEmpty())
                        _scrapUiState.value = ScrapUiState.Empty
                    else {
                        _scrapUiState.value = it.toScrapUiState()
                        _location.value?.let { markerInfoList ->
                            _scrapMarkerList.value = markerInfoList.markerFilter(
                                it.map {
                                    it.id
                                }
                            )
                        }
                    }
                }
                .onFailure {
                    _scrapUiState.value = ScrapUiState.Error(it.message)
                }
        }
    }

    fun setMapInfo(markerInfo: ArrayList<MarkerInfo>) {
        _location.value = markerInfo
    }

    fun putScrap(restaurantId: String) {
        viewModelScope.launch {
            scrapRepository.updateRestaurantScrap(
                restaurantId,
                storage.id
            )
        }
    }

    private fun List<ScrapInfo>.toScrapUiState() = ScrapUiState.Success(this)

    sealed class ScrapUiState {
        object Loading : ScrapUiState()
        object Empty : ScrapUiState()
        data class Success(val data: List<ScrapInfo>) : ScrapUiState()
        class Error(val message: String?) : ScrapUiState()
    }
}
