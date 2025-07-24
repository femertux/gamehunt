package com.mobileni.gamehunt.data.di

import com.mobileni.gamehunt.data.network.RawgApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.rawg.io/api/"

    @Singleton
    @Provides
    fun providesOkhttp(
    ): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            //if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient().newBuilder()
        httpClient.readTimeout(50, TimeUnit.SECONDS)
        httpClient.connectTimeout(50, TimeUnit.SECONDS)
        httpClient.retryOnConnectionFailure(true)

        httpClient.addInterceptor(logInterceptor)
        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideRawgApiService(retrofit: Retrofit): RawgApiService =
        retrofit.create(RawgApiService::class.java)
}
