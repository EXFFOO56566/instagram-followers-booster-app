package com.lxquyen.instabooster.di

import com.lxquyen.instabooster.data.api.UserService
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.data.repository.impl.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by Furuichi on 07/07/2022
 */
@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Singleton
    @Provides
    fun provideUserRepos(userRepositoryImpl: UserRepositoryImpl): UserRepository {
        return userRepositoryImpl
    }

}