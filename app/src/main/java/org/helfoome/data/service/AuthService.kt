package org.helfoome.data.service

import org.helfoome.data.model.request.RequestLogin
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/auth")
    suspend fun login(@Body requestLogin: RequestLogin): BaseResponse<ResponseLogin>
}