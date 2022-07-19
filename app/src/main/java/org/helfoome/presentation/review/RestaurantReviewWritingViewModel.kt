package org.helfoome.presentation.review

import android.net.Uri
import androidx.lifecycle.LiveData
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
    private val _selectedImageList = MutableLiveData<List<Uri>>()
    val selectedImageList: LiveData<List<Uri>> get() = _selectedImageList
    fun setSelectedTasteTag(tagType: TasteHashtagType) {
        selectedTasteTag.value = tagType
    }

    fun setSelectedGoodPointTag(tagType: GoodPointHashtagType) {
        if (selectedGoodPointTags.value?.get(tagType) == null) return
        val isSelected = selectedGoodPointTags.value!![tagType] ?: return
        selectedGoodPointTags.value!![tagType] = !isSelected
        selectedGoodPointTags.value = selectedGoodPointTags.value
    }

    fun setSelectedGalleryImages(uriList: List<Uri>) {
        _selectedImageList.value = uriList
    }
}
