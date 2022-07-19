package org.helfoome.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.helfoome.data.local.HFMSharedPreference
import javax.inject.Inject

@HiltViewModel
class WithdrawalViewModel @Inject constructor(private val sharedHFMSharedPreference: HFMSharedPreference) : ViewModel() {

    private val _nickname = MutableLiveData<String>()
    val nickname get() = _nickname
}
