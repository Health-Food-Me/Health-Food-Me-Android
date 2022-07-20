package org.helfoome.presentation.drawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.entity.MyReviewListInfo
import org.helfoome.domain.repository.ReviewRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val hfmSharedPreference: HFMSharedPreference
) : ViewModel() {
    private val _myReviewInfo = MutableLiveData<List<MyReviewListInfo>>()
    val myReviewInfo: LiveData<List<MyReviewListInfo>> = _myReviewInfo

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
                it.data.map { review -> review.toMyReviewListInfo() }
            }.onFailure {
                Timber.d(it.message)
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

                }
        }
    }
}
