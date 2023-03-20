package com.lxquyen.instabooster.di

import com.google.gson.Gson
import com.lxquyen.instabooster.data.mock.MockInterceptor
import com.lxquyen.instabooster.utils.Constants
import com.lxquyen.instabooster.utils.interceptor.ModifyRequestInterceptor
import com.ohayo.core.helper.NetworkHelper
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

/**
 * Created by Furuichi on 07/07/2022
 */
@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = NetworkHelper.gson

    @Singleton
    @Provides
    fun provideLogger(): HttpLoggingInterceptor = NetworkHelper.logger

    @Singleton
    @Provides
    fun retrofit(
        gson: Gson,
        loggingInterceptor: HttpLoggingInterceptor,
        mockInterceptor: MockInterceptor,
        modifyRequestInterceptor: ModifyRequestInterceptor
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
//            .addInterceptor(mockInterceptor)
            .addInterceptor(modifyRequestInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL + "instagram/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

}