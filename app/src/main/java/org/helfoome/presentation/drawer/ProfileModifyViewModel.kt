package org.helfoome.presentation.drawer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern

class ProfileModifyViewModel : ViewModel() {

    val nickname = MutableLiveData<String>()
    private val _success = MutableLiveData<Boolean>()
    val success = _success
    private val _isValidNickname = MutableLiveData<Boolean>()
    val isValidPassword = _isValidNickname

    fun onNicknameTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        _success.value = count > 0
    }

    fun checkPasswordFormat(): Boolean {
        if (nickname.value != null) {
            val nicknamePattern =
                Pattern.compile("^[가-힣ㄱ-ㅎa-zA-Z0-9._ -]{2,}\$")
            _isValidNickname.value = nicknamePattern.matcher(nickname.value).matches()
            return true
        }
        return false
    }
}