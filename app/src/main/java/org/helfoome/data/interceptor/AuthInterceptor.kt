package org.helfoome.data.interceptor

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.helfoome.BuildConfig
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.model.response.ResponseRefresh
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val storage: HFMSharedPreference,
    private val gson: Gson
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authRequest = if (isLogin(originalRequest)) originalRequest else
            originalRequest.newBuilder().addHeader("token", storage.accessToken).build()
        val response = chain.proceed(authRequest)

        when (response.code) {
            401 -> {
                val refreshTokenRequest = originalRequest.newBuilder().get()
                    .url("${BuildConfig.HFM_BASE_URL}auth/token")
                    .addHeader("accesstoken", storage.accessToken)
                    .addHeader("refreshtoken", storage.refreshToken)
                    .build()
                val refreshTokenResponse = chain.proceed(refreshTokenRequest)

                if (refreshTokenResponse.isSuccessful) {
                    val refreshToken = gson.fromJson(
                        refreshTokenResponse.body?.string(),
                        ResponseRefresh::class.java
                    )
                    with(storage) {
                        accessToken = refreshToken.data.accessToken
                        this.refreshToken = refreshToken.data.refreshToken
                    }
                    val newRequest = originalRequest.newBuilder()
                        .addHeader("token", storage.accessToken)
                        .build()
                    return chain.proceed(newRequest)
                }
            }
        }
        return response
    }

    private fun isLogin(originalRequest: Request) =
        !originalRequest.url.encodedPath.contains("token") && originalRequest.url.encodedPath.contains("auth")
}
