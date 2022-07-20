package org.helfoome.presentation.review

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.helfoome.presentation.type.GoodPointHashtagType
import org.helfoome.presentation.type.TasteHashtagType
import javax.inject.Inject

@HiltViewModel
class RestaurantReviewWritingViewModel @Inject constructor() : ViewModel() {
    private val _review = MutableLiveData<String>()
    val review get() = _review
    val selectedTasteTag = MutableLiveData<TasteHashtagType>()
    val selectedGoodPointTags = MutableLiveData(hashMapOf(GoodPointHashtagType.NO_BURDEN to false, GoodPointHashtagType.EASY_TO_CONTROL to false, GoodPointHashtagType.FULL to false))
    private val _selectedImageList = MutableLiveData<List<Bitmap>>()
    val selectedImageList: LiveData<List<Bitmap>> get() = _selectedImageList
    private val _isEnabledWritingCompleteButton = MediatorLiveData<Boolean>()
    val isEnabledWritingCompleteButton: LiveData<Boolean> get() = _isEnabledWritingCompleteButton

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
}
