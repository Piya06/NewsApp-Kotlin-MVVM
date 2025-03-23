package com.basic.newsapp.di

import android.content.Context
import com.basic.newsapp.api.NewsApi
import com.basic.newsapp.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideNewsRepository(newsApi: NewsApi, context: Context): NewsRepository {
        return NewsRepository(newsApi, context)
    }
}