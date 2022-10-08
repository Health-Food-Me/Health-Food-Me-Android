package org.helfoome.data.interceptor

import android.app.Application
import android.content.Intent
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import org.helfoome.BuildConfig
import org.helfoome.R
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.model.response.ResponseRefresh
import org.helfoome.util.ext.showToast
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val storage: HFMSharedPreference,
    private val gson: Gson,
    private val context: Application
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authRequest = originalRequest.newBuilder().addHeader("token", storage.accessToken).build()
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
                    val responseRefresh = gson.fromJson(
                        refreshTokenResponse.body?.string(),
                        ResponseRefresh::class.java
                    )
                    with(storage) {
                        accessToken = responseRefresh.data.accessToken
                        refreshToken = responseRefresh.data.refreshToken
                    }
                    val newRequest = originalRequest.newBuilder()
                        .addHeader("token", storage.accessToken)
                        .build()
                    return chain.proceed(newRequest)
                } else {
                    with(context) {
                        CoroutineScope(Dispatchers.Main).launch {
                            startActivity(
                                Intent.makeRestartActivityTask(
                                    packageManager.getLaunchIntentForPackage(packageName)?.component
                                )
                            )
                            showToast(getString(R.string.auto_login_fail))
                            storage.clear()
                            Runtime.getRuntime().exit(0)
                            cancel()
                        }
                    }
                }
            }
        }

        return response
    }
}
