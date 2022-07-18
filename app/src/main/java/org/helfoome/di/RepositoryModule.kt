package org.helfoome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.helfoome.data.local.dao.SearchDao
import org.helfoome.data.repository.SearchRepositoryImpl
import org.helfoome.data.repository.LoginRepositoryImpl
import org.helfoome.data.repository.ProfileRepositoryImpl
import org.helfoome.data.service.AuthService
import org.helfoome.domain.repository.SearchRepository
import org.helfoome.domain.repository.LoginRepository
import org.helfoome.domain.repository.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSearchRepository(searchDao: SearchDao): SearchRepository =
        SearchRepositoryImpl(searchDao)

    @Singleton
    @Provides
    fun provideAuthRepository(
        authService: AuthService
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
}
