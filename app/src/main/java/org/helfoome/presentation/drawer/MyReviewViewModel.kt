package org.helfoome.presentation.drawer

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.service.ReviewService
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.domain.repository.ReviewRepository
import org.helfoome.presentation.type.GoodPointHashtagType
import org.helfoome.presentation.type.TasteHashtagType
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val reviewService: ReviewService,
    private val hfmSharedPreference: HFMSharedPreference
) : ViewModel() {
    private val _myReviewInfo = MutableLiveData<List<MyReviewInfo>>()
    val myReviewInfo: LiveData<List<MyReviewInfo>> = _myReviewInfo

    private val _review = MutableLiveData<String>()
    val review get() = _review

    private val _isEnabledWritingCompleteButton = MediatorLiveData<Boolean>()
    val isEnabledWritingCompleteButton: LiveData<Boolean> get() = _isEnabledWritingCompleteButton

    private val _isCompletedReviewUpload = MutableLiveData<Boolean>()
    val isCompletedReviewUpload: LiveData<Boolean> get() = _isCompletedReviewUpload

    val selectedTasteTag = MutableLiveData<TasteHashtagType>()
    val selectedGoodPointTags = MutableLiveData(
        hashMapOf(
            GoodPointHashtagType.NO_BURDEN to false,
            GoodPointHashtagType.EASY_TO_CONTROL to false,
            GoodPointHashtagType.FULL to false
        )
    )

    fun getMyReviewList() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                reviewRepository.getMyReviewList(hfmSharedPreference.id)
            }.onSuccess {
                _myReviewInfo.postValue(
                    it.data.map {
                        it.toMyReviewListInfo()
                    }
                )
                Timber.d("success ${_myReviewInfo.value}")
            }.onFailure {
                Timber.d("failure ${it.message}")
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(reviewId)
                .onSuccess {
                    getMyReviewList()
                }
                .onFailure {
                    Timber.d(it.message)
                }
        }
    }
}
