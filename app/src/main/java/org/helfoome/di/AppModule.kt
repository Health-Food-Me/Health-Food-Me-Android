package org.helfoome.di

import android.app.Application
import android.util.DisplayMetrics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.helfoome.data.local.HFMSharedPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDisplayMetrics(app: Application): DisplayMetrics = app.resources.displayMetrics

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext app: Application): HFMSharedPreference = HFMSharedPreference(app)
}
