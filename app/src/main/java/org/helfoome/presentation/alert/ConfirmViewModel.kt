package org.helfoome.presentation.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.helfoome.presentation.type.ConfirmType

class ConfirmViewModel : ViewModel() {

    private val _confirmType = MutableLiveData<ConfirmType>()
    val confirmType: LiveData<ConfirmType> get() = _confirmType

    fun setConfirmType(confirmType: ConfirmType) {
        _confirmType.value = confirmType
    }
}
