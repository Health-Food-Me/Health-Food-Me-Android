package org.helfoome.data.service

import org.helfoome.data.model.request.RequestLogin
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseLogin
import org.helfoome.data.model.response.ResponseProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {

    @POST("/auth")
    suspend fun login(@Body requestLogin: RequestLogin): BaseResponse<ResponseLogin>

    @GET("/user/{userId}/profile")
    suspend fun getProfile(@Path("userId") userId: String): BaseResponse<ResponseProfile>
}
