package com.mobileni.gamehunt.data.di

import com.mobileni.gamehunt.data.network.RawgApiService
import com.mobileni.gamehunt.data.repository.GameDetailRepositoryImpl
import com.mobileni.gamehunt.data.repository.GameRepositoryImpl
import com.mobileni.gamehunt.data.repository.GenreRepositoryImpl
import com.mobileni.gamehunt.domain.repository.GameDetailRepository
import com.mobileni.gamehunt.domain.repository.GameRepository
import com.mobileni.gamehunt.domain.repository.GenreRepository
import com.mobileni.gamehunt.utils.Constants.RAWG_API_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGenreRepository(apiService: RawgApiService): GenreRepository =
        GenreRepositoryImpl(apiService, RAWG_API_KEY)

    @Provides
    @Singleton
    fun provideGameRepository(apiService: RawgApiService): GameRepository =
        GameRepositoryImpl(apiService, RAWG_API_KEY)

    @Provides
    @Singleton
    fun provideGameDetailRepository(apiService: RawgApiService): GameDetailRepository =
        GameDetailRepositoryImpl(apiService, RAWG_API_KEY)
}
