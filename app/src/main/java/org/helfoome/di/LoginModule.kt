package org.helfoome.di

import android.content.Context
import com.kakao.sdk.user.UserApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.service.AuthService
import org.helfoome.data.service.KakaoAuthService
import org.helfoome.data.service.NaverAuthService

@Module
@InstallIn(ActivityComponent::class)
object LoginModule {
    @Provides
    @ActivityScoped
    fun provideUserApiClient(): UserApiClient = UserApiClient.instance

    @Provides
    fun provideKakaoAuthService(
        @ActivityContext context: Context,
        client: UserApiClient
    ) = KakaoAuthService(context, client)

    @Provides
    fun provideNaverAuthService(
        @ApplicationContext context: Context,
        sharedPreferences: HFMSharedPreference,
        authService: AuthService
    ) = NaverAuthService(context, sharedPreferences, authService)
}
