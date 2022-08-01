package org.helfoome.presentation.withdrawal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.repository.WithdrawalRepository
import javax.inject.Inject

@HiltViewModel
class WithdrawalViewModel @Inject constructor(
    private val sharedHFMSharedPreference: HFMSharedPreference,
    private val withdrawalRepository: WithdrawalRepository
) : ViewModel() {

    private val _isCorrectedNickname = MutableLiveData<Boolean>()
    val isCorrectedNickname: LiveData<Boolean> get() = _isCorrectedNickname
    private val _withdrawSuccess = MutableLiveData<Boolean>()
    val withdrawSuccess: LiveData<Boolean> get() = _withdrawSuccess
    val nickname = MutableLiveData<String>()

    fun deleteUser() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { withdrawalRepository.withdrawal(sharedHFMSharedPreference.id) }
                .onSuccess {
                    _withdrawSuccess.postValue(true)
                }
                .onFailure {
                }
        }
    }

    fun compareNickname(nickname: String) {
        _isCorrectedNickname.value = sharedHFMSharedPreference.nickname == nickname
    }
}
