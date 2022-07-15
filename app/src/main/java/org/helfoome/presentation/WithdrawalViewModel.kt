package org.helfoome.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WithdrawalViewModel : ViewModel() {

    private val _nickname = MutableLiveData<String>()
    val nickname get() = _nickname
}
