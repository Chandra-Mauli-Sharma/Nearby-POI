package com.example.nearbypoi.di

import com.example.nearbypoi.repository.NearbyRepository
import com.example.nearbypoi.util.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class, SingletonComponent::class)
abstract class NearbyModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient?): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.api_base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client!!)
            .build()
    }

    @Provides
    fun provideNearbyRepository(retrofit: Retrofit): NearbyRepository {
        return retrofit.create(NearbyRepository::class.java)
    }
}