package org.helfoome.data.repository

import org.helfoome.data.model.request.RequestLogin
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseLogin
import org.helfoome.data.service.AuthService
import org.helfoome.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val authService: AuthService): LoginRepository {

    override suspend fun login(requestLogin: RequestLogin): BaseResponse<ResponseLogin> {
        return authService.login(requestLogin)
    }
}