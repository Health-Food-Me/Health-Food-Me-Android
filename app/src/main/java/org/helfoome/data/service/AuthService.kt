package org.helfoome.data.service

import org.helfoome.data.model.request.RequestLogin
import org.helfoome.data.model.request.RequestProfileModify
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseLogin
import org.helfoome.data.model.response.ResponseProfile
import org.helfoome.data.model.response.ResponseProfileModify
import retrofit2.http.*

interface AuthService {

    @POST("/auth")
    suspend fun login(@Body requestLogin: RequestLogin): BaseResponse<ResponseLogin>

    @DELETE("/auth/withdrawal/{userId}")
    suspend fun withdrawal(@Path("userId") userId: String): BaseResponse<Unit>

    @GET("/user/{userId}/profile")
    suspend fun getProfile(@Path("userId") userId: String): BaseResponse<ResponseProfile>

    @PUT("/user/{userId}/profile")
    suspend fun modifyProfile(@Body requestProfileModify: RequestProfileModify, @Path("userId") userId: String): BaseResponse<ResponseProfileModify>
}
