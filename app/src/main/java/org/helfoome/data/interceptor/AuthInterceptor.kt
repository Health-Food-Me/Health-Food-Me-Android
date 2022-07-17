package org.helfoome.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import org.helfoome.data.local.HFMSharedPreference
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val storage: HFMSharedPreference
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        storage.accessToken.let {
            requestBuilder.addHeader("token", it)
        }
        return chain.proceed(requestBuilder.build())
    }
}
