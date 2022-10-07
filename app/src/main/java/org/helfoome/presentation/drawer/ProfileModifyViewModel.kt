package org.helfoome.presentation.drawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.model.request.RequestProfileModify
import org.helfoome.domain.repository.ProfileModifyRepository
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ProfileModifyViewModel @Inject constructor(
    private val profileModifyRepository: ProfileModifyRepository,
    private val sharedPreferences: HFMSharedPreference
) : ViewModel() {
    private val _isProfileModify = MutableLiveData<Boolean>()
    val isProfileModify: LiveData<Boolean> = _isProfileModify
    val nickname = MutableLiveData<String>()
    private val _isValidNickname = MutableLiveData<Boolean>()
    val isValidNickname: LiveData<Boolean> = _isValidNickname
    private val _isOverlapNickname = MutableLiveData<Boolean>()
    val isOverlapNickName: LiveData<Boolean> = _isOverlapNickname

    fun profileModify() {
        checkNicknameFormat()
        if (_isValidNickname.value == true) {
            viewModelScope.launch {
                runCatching {
                    profileModifyRepository.modifyProfile(
                        RequestProfileModify(nickname.value.toString()),
                        sharedPreferences.id,
                    )
                }.onSuccess {
                    sharedPreferences.nickname = it.data.name
                    _isProfileModify.value = true
                }.onFailure {
                    Timber.i(it.message)
                    _isOverlapNickname.value = false
                }
            }
        }
    }

    private fun checkNicknameFormat() {
        if (!nickname.value.isNullOrBlank()) {
            val nicknamePattern =
                Pattern.compile("^[가-힣ㄱ-ㅎa-zA-Z0-9 ]+\$")
            _isValidNickname.value = nicknamePattern.matcher(nickname.value).matches()
        }
    }
}
