package org.helfoome.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.helfoome.data.repository.*
import org.helfoome.domain.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl,
    ): SearchRepository

    @Binds
    @Singleton
    fun bindAuthRepository(
        loginRepositoryImpl: LoginRepositoryImpl,
    ): LoginRepository

    @Singleton
    @Binds
    fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl,
    ): ProfileRepository

    @Binds
    @Singleton
    fun bindRestaurantRepository(restaurantRepositoryImpl: RestaurantRepositoryImpl): RestaurantRepository

    @Binds
    @Singleton
    fun bindProfileModifyRepository(profileModifyRepositoryImpl: ProfileModifyRepositoryImpl): ProfileModifyRepository

    @Binds
    @Singleton
    fun bindMapRepository(mapRepositoryImpl: MapRepositoryImpl): MapRepository

    @Binds
    @Singleton
    fun bindReviewRepository(reviewRepositoryImpl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    fun bindWithdrawalRepository(withdrawalRepositoryImpl: WithdrawalRepositoryImpl): WithdrawalRepository

    @Binds
    @Singleton
    fun bindScrapRepository(scrapRepositoryImpl: ScrapRepositoryImpl): ScrapRepository
}
