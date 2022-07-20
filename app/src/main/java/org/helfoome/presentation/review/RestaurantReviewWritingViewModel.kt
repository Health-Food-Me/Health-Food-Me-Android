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
import javax.inject.Inject

@HiltViewModel
class RestaurantReviewWritingViewModel @Inject constructor(
    private val reviewService: ReviewService,
    private val hfmSharedPreference: HFMSharedPreference,
) : ViewModel() {
    private val _review = MutableLiveData<String>()
    val review get() = _review

    val selectedTasteTag = MutableLiveData<TasteHashtagType>()
    val selectedGoodPointTags = MutableLiveData(hashMapOf(GoodPointHashtagType.NO_BURDEN to false, GoodPointHashtagType.EASY_TO_CONTROL to false, GoodPointHashtagType.FULL to false))

    private val _selectedImageList = MutableLiveData<List<Bitmap>>()
    val selectedImageList: LiveData<List<Bitmap>> get() = _selectedImageList

    private val _isEnabledWritingCompleteButton = MediatorLiveData<Boolean>()
    val isEnabledWritingCompleteButton: LiveData<Boolean> get() = _isEnabledWritingCompleteButton

    private val _isCompletedReviewUpload = MutableLiveData<Boolean>()
    val isCompletedReviewUpload: LiveData<Boolean> get() = _isCompletedReviewUpload

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
                reviewService.postHFMReview(hfmSharedPreference.id, "62d26c9bd11146a81ef18eb5", scoreRequestBody, tasteRequestBody, goodRequestBody, contentRequestBody, imageListMultipartBody)
            }.fold({
                _isCompletedReviewUpload.value = true
            }, {
                _isCompletedReviewUpload.value = false
            })
        }
    }

    private fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())
}
