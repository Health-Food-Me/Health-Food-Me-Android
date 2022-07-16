package org.helfoome.di

import android.app.Application
import android.util.DisplayMetrics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDisplayMetrics(app: Application): DisplayMetrics = app.resources.displayMetrics
}
