package org.helfoome.domain.repository

import org.helfoome.data.model.request.RequestLogin
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseLogin

interface LoginRepository {
    suspend fun login(requestLogin: RequestLogin): BaseResponse<ResponseLogin>
}
