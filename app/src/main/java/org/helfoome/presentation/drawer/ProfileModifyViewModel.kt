package org.helfoome.presentation.drawer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern

class ProfileModifyViewModel : ViewModel() {

    val nickname = MutableLiveData<String>()
    private val _isValidNickname = MutableLiveData<Boolean>()
    val isValidNickname = _isValidNickname
    // 추후에 서버 연결시 중복 체크
    private val _isOverlapNickname = MutableLiveData<Boolean>()
    val isOverlapNickName = _isOverlapNickname

    fun checkNicknameFormat(): Boolean {
        if (!nickname.value.isNullOrBlank()) {
            val nicknamePattern =
                Pattern.compile("^[가-힣ㄱ-ㅎa-zA-Z0-9._ -]+\$")

            _isValidNickname.value = nicknamePattern.matcher(nickname.value).matches()
            return true
        }
        return false
    }
}
