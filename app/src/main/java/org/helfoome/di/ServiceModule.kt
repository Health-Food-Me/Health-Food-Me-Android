package org.helfoome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.helfoome.data.service.AuthService
import org.helfoome.data.service.MapService
import org.helfoome.data.service.SearchService
import org.helfoome.data.service.RestaurantService
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

    @Singleton
    @Provides
    fun provideSearchAPIService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Singleton
    @Provides
    fun provideMapAPIService(retrofit: Retrofit): MapService {
        return retrofit.create(MapService::class.java)
    }

    @Provides
    @Singleton
    fun provideRestaurantService(retrofit: Retrofit): RestaurantService =
        retrofit.create(RestaurantService::class.java)
}
