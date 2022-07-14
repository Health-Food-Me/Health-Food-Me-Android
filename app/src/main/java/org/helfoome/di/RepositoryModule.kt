package org.helfoome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.helfoome.data.local.dao.SearchDao
import org.helfoome.data.repository.SearchRepositoryImpl
import org.helfoome.domain.repository.SearchRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSearchRepository(searchDao: SearchDao): SearchRepository =
        SearchRepositoryImpl(searchDao)
}
