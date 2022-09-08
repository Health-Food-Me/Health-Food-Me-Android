package org.helfoome.presentation.review

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.toRequestBody
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.service.ReviewService
import org.helfoome.domain.entity.HFMReviewInfo
import org.helfoome.domain.entity.MyReviewListInfo
import org.helfoome.presentation.type.GoodPointHashtagType
import org.helfoome.presentation.type.TasteHashtagType
import org.helfoome.util.ContentUriRequestBody
import javax.inject.Inject

@HiltViewModel
class RestaurantReviewWritingViewModel @Inject constructor(
    private val reviewService: ReviewService,
    private val hfmSharedPreference: HFMSharedPreference,
) : ViewModel() {
    private val _isReviewModify = MutableLiveData<Boolean>()
    val isReviewModify: LiveData<Boolean> = _isReviewModify

    private var _restaurantTitle: String? = null
    val restaurantTitle: String? get() = _restaurantTitle

    private val _reviewInfo = MutableLiveData<MyReviewListInfo?>()
    val reviewInfo get(): LiveData<MyReviewListInfo?> = _reviewInfo

    private val _restaurantId = MutableLiveData<String>()
    val restaurantId get() = _restaurantId

    private val _reviewContent = MutableLiveData<String>()
    val reviewContent get() = _reviewContent

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

    private val _hfmReviews = MutableLiveData<HFMReviewInfo>()
    val hfmReviews: LiveData<HFMReviewInfo> = _hfmReviews

    fun setReviewInfo(review: MyReviewListInfo, tasteTag: TasteHashtagType?, goodTags: List<GoodPointHashtagType?>) { // TODO need refactoring
        _reviewInfo.value = review
        _reviewContent.value = review.description
        setSelectedTasteTag(tasteTag ?: return)
        goodTags.forEach { setSelectedGoodPointTag(it ?: return) }
    }

    fun setRestaurantName(name: String) {
        _restaurantTitle = name
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
        val review = reviewContent.value?.trim()
        _isEnabledWritingCompleteButton.value =
            selectedTasteTag.value != null && selectedGoodPointTags.value?.containsValue(true) == true && !(review.isNullOrBlank())
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
        if (isEditMode.value == true) {
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
        val contentRequestBody = reviewContent.value.toPlainRequestBody()
        val tasteRequestBody = context.getString(selectedTasteTag.value?.strRes ?: return).replace("# ", "").toPlainRequestBody()
        val goodListMultipartBody = mutableListOf<MultipartBody.Part>()
        val goodList = selectedGoodPointTags.value?.filter { it.value }?.keys?.map {
            context.getString(it.strRes)
        } ?: return
        for (good in goodList) {
            goodListMultipartBody.add(createFormData("good", good))
        }
        val nameRequestBody = mutableListOf<String>()
        val imageListMultipartBody = mutableListOf<MultipartBody.Part>()

        for (element in image) {
            if (!element.toString().startsWith("content")) { // 기존 이미지
                nameRequestBody.add(element.toString())
            }
            else { // 새로 업로드할 이미지
                val imageMultipartBody: MultipartBody.Part =
                    ContentUriRequestBody(context, element ?: continue).toFormData()
                imageListMultipartBody.add(imageMultipartBody)
            }
        }

        viewModelScope.launch {
            runCatching {
                reviewService.putMyReviewEdit(
                    reviewInfo.value?.id.toString(),
                    scoreRequestBody,
                    tasteRequestBody,
                    goodListMultipartBody,
                    contentRequestBody,
                    nameRequestBody.toString().toPlainRequestBody(),
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
        val contentRequestBody = reviewContent.value.toPlainRequestBody()
        val tasteRequestBody = context.getString(selectedTasteTag.value?.strRes ?: return).replace("# ", "").toPlainRequestBody()
        val imageListMultipartBody = mutableListOf<MultipartBody.Part>()

        for (element in image) {
            val imageMultipartBody: MultipartBody.Part =
                ContentUriRequestBody(context, element ?: continue).toFormData()
            imageListMultipartBody.add(imageMultipartBody)
        }

        val goodListMultipartBody = mutableListOf<MultipartBody.Part>()
        val goodList = selectedGoodPointTags.value?.filter { it.value }?.keys?.map {
            context.getString(it.strRes)
        } ?: return
        for (good in goodList) {
            goodListMultipartBody.add(createFormData("good", good))
        }

        viewModelScope.launch {
            runCatching {
                reviewService.postHFMReview(
                    hfmSharedPreference.id,
                    restaurantId.value ?: return@launch,
                    scoreRequestBody,
                    tasteRequestBody,
                    goodListMultipartBody,
                    contentRequestBody,
                    imageListMultipartBody
                )
            }.fold({
                _hfmReviews.value = it.data.toReviewInfo()
                _isReviewModify.value = true
                _isCompletedReviewUpload.value = true
            }, {
                _isCompletedReviewUpload.value = false
            })
        }
    }

    fun setRestaurantId(restaurantId: String) {
        _restaurantId.value = restaurantId
    }

    private fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())
}
