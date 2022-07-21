package org.helfoome.presentation.review

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.service.ReviewService
import org.helfoome.presentation.type.GoodPointHashtagType
import org.helfoome.presentation.type.TasteHashtagType
import org.helfoome.util.ContentUriRequestBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestaurantReviewWritingViewModel @Inject constructor(
    private val reviewService: ReviewService,
    private val hfmSharedPreference: HFMSharedPreference,
) : ViewModel() {
    private val _isReviewModify = MutableLiveData<Boolean>()
    val isReviewModify: LiveData<Boolean> = _isReviewModify

    private val _reviewId = MutableLiveData<String>()
    val reviewId get() = _reviewId

    private val _review = MutableLiveData<String>()
    val review get() = _review

    private val _isEditMode = MutableLiveData<Boolean>()
    val isEditMode get() = _isEditMode

    val selectedTasteTag = MutableLiveData<TasteHashtagType>()
    val selectedGoodPointTags = MutableLiveData(
        hashMapOf(
            GoodPointHashtagType.NO_BURDEN to false,
            GoodPointHashtagType.EASY_TO_CONTROL to false,
            GoodPointHashtagType.FULL to false
        )
    )

    private val _selectedImageList = MutableLiveData<List<Bitmap>>()
    val selectedImageList: LiveData<List<Bitmap>> get() = _selectedImageList

    private val _isEnabledWritingCompleteButton = MediatorLiveData<Boolean>()
    val isEnabledWritingCompleteButton: LiveData<Boolean> get() = _isEnabledWritingCompleteButton

    private val _isCompletedReviewUpload = MutableLiveData<Boolean>()
    val isCompletedReviewUpload: LiveData<Boolean> get() = _isCompletedReviewUpload

    fun setReviewId(reviewId: String) {
        _reviewId.value = reviewId
    }

    fun setEditMode(editMode: Boolean) {
        _isEditMode.value = editMode
    }

    fun setSelectedTasteTag(tagType: TasteHashtagType) {
        selectedTasteTag.value = tagType
    }

    fun setSelectedGoodPointTag(tagType: GoodPointHashtagType) {
        if (selectedGoodPointTags.value?.get(tagType) == null) return
        val isSelected = selectedGoodPointTags.value!![tagType] ?: return
        selectedGoodPointTags.value!![tagType] = !isSelected
        selectedGoodPointTags.value = selectedGoodPointTags.value
    }

    fun checkReviewCompletion() {
        _isEnabledWritingCompleteButton.value = selectedTasteTag.value != null && selectedGoodPointTags.value?.containsValue(true) == true
    }

    // TODO delete
    fun setSelectedGalleryImages(uriList: List<Bitmap>) {
        _selectedImageList.value = uriList
    }

    fun uploadReview(
        context: Context,
        score: Float,
        image: List<Uri?>,
    ) {
        Timber.d("${_isEditMode.value}")
        if (_isEditMode.value == true) {
            editReview(context, score, image)
        } else {
            writeReview(context, score, image)
        }
    }

    private fun editReview(
        context: Context,
        score: Float,
        image: List<Uri?>,
    ) {
        val scoreRequestBody = score.toString().toPlainRequestBody()
        val contentRequestBody = review.value.toPlainRequestBody()
        val tasteRequestBody = context.getString(selectedTasteTag.value?.strRes ?: return).replace("# ", "").toPlainRequestBody()
        val goodRequestBody = selectedGoodPointTags.value?.filter { it.value }?.keys?.map {
            context.getString(it.strRes).replace("# ", "")
        }.toString().toPlainRequestBody()
        val nameRequestBody = listOf("테스트", "테스트2").toString().toPlainRequestBody()
        val imageListMultipartBody = mutableListOf<MultipartBody.Part>()

        for (element in image) {
            val imageMultipartBody: MultipartBody.Part =
                ContentUriRequestBody(context, element ?: continue).toFormData()
            imageListMultipartBody.add(imageMultipartBody)
        }

        viewModelScope.launch {
            runCatching {
                reviewService.putMyReviewEdit(
                    _reviewId.value.toString(),
                    scoreRequestBody,
                    tasteRequestBody,
                    goodRequestBody,
                    contentRequestBody,
                    nameRequestBody,
                    imageListMultipartBody
                )
            }.fold({
                _isReviewModify.value = true
                _isCompletedReviewUpload.value = true
            }, {
                _isCompletedReviewUpload.value = false
            })
        }
    }

    private fun writeReview(
        context: Context,
        score: Float,
        image: List<Uri?>,
    ) {
        val scoreRequestBody = score.toString().toPlainRequestBody()
        val contentRequestBody = review.value.toPlainRequestBody()
        val tasteRequestBody = context.getString(selectedTasteTag.value?.strRes ?: return).replace("# ", "").toPlainRequestBody()
        val goodRequestBody = selectedGoodPointTags.value?.filter { it.value }?.keys?.map {
            context.getString(it.strRes).replace("# ", "")
        }.toString().toPlainRequestBody()
        val imageListMultipartBody = mutableListOf<MultipartBody.Part>()

        for (element in image) {
            val imageMultipartBody: MultipartBody.Part =
                ContentUriRequestBody(context, element ?: continue).toFormData()
            imageListMultipartBody.add(imageMultipartBody)
        }

        viewModelScope.launch {
            runCatching {
                reviewService.postHFMReview(
                    hfmSharedPreference.id,
                    "62d26c9bd11146a81ef18eb5",
                    scoreRequestBody,
                    tasteRequestBody,
                    goodRequestBody,
                    contentRequestBody,
                    imageListMultipartBody
                )
            }.fold({
                _isReviewModify.value = true
                _isCompletedReviewUpload.value = true
            }, {
                _isCompletedReviewUpload.value = false
            })
        }
    }

    private fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())
}
