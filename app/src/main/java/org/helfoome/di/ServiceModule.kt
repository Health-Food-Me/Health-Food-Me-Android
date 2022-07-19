package org.helfoome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.helfoome.data.service.AuthService
import org.helfoome.data.service.RestaurantService
import org.helfoome.data.service.ReviewService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideAuthAPIService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideRestaurantService(retrofit: Retrofit): RestaurantService =
        retrofit.create(RestaurantService::class.java)

    @Provides
    @Singleton
    fun provideReviewService(retrofit: Retrofit): ReviewService =
        retrofit.create(ReviewService::class.java)
}
