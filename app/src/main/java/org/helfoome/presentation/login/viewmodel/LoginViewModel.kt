package org.helfoome.presentation.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.helfoome.data.model.request.RequestLogin
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.repository.LoginRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val sharedPreferences: HFMSharedPreference
) : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess get() = _loginSuccess

    fun loginNetwork(social: String) {
        // 성공시 loginSuccess true
        viewModelScope.launch {
            loginRepository.login(
                RequestLogin(
                    social, sharedPreferences.accessToken
                )
            )
        }
    }
}