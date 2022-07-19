package org.helfoome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.helfoome.data.datasource.RemoteSearchDataSource
import org.helfoome.data.local.dao.SearchDao
import org.helfoome.data.repository.*
import org.helfoome.data.service.AuthService
import org.helfoome.data.service.RestaurantService
import org.helfoome.data.service.ReviewService
import org.helfoome.domain.repository.*
import org.helfoome.domain.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSearchRepository(
        searchDao: SearchDao,
        searchDataSource: RemoteSearchDataSource
    ): SearchRepository =
        SearchRepositoryImpl(searchDao, searchDataSource)

    @Singleton
    @Provides
    fun provideAuthRepository(
        authService: AuthService,
    ): LoginRepository {
        return LoginRepositoryImpl(authService)
    }

    @Singleton
    @Provides
    fun provideProfileRepository(
        authService: AuthService
    ): ProfileRepository {
        return ProfileRepositoryImpl(authService)
    }

    @Provides
    @Singleton
    fun provideRestaurantRepository(service: RestaurantService): RestaurantRepository =
        RestaurantRepositoryImpl(service)

    @Provides
    @Singleton
    fun putProfileModifyRepository(service: AuthService): ProfileModifyRepository =
        ProfileModifyRepositoryImpl(service)

    @Provides
    @Singleton
    fun provideReviewRepository(service: ReviewService): ReviewRepository =
        ReviewRepositoryImpl(service)
}
