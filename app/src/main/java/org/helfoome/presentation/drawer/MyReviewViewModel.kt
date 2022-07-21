package org.helfoome.presentation.drawer

import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.model.request.RequestReviewEdit
import org.helfoome.data.service.ReviewService
import org.helfoome.domain.entity.MyReviewListInfo
import org.helfoome.domain.repository.ReviewRepository
import org.helfoome.presentation.type.GoodPointHashtagType
import org.helfoome.presentation.type.TasteHashtagType
import org.helfoome.util.ContentUriRequestBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val reviewService: ReviewService,
    private val hfmSharedPreference: HFMSharedPreference
) : ViewModel() {
    private val _myReviewInfo = MutableLiveData<List<MyReviewListInfo>>()
    val myReviewInfo: LiveData<List<MyReviewListInfo>> = _myReviewInfo

    private val _review = MutableLiveData<String>()
    val review get() = _review

    private val _isEnabledWritingCompleteButton = MediatorLiveData<Boolean>()
    val isEnabledWritingCompleteButton: LiveData<Boolean> get() = _isEnabledWritingCompleteButton

    private val _isCompletedReviewUpload = MutableLiveData<Boolean>()
    val isCompletedReviewUpload: LiveData<Boolean> get() = _isCompletedReviewUpload

    val selectedTasteTag = MutableLiveData<TasteHashtagType>()
    val selectedGoodPointTags = MutableLiveData(hashMapOf(GoodPointHashtagType.NO_BURDEN to false, GoodPointHashtagType.EASY_TO_CONTROL to false, GoodPointHashtagType.FULL to false))

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
