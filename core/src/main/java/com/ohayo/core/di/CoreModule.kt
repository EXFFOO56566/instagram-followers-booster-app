package com.ohayo.core.di

import com.ohayo.core.helper.preferences.DataStoreManager
import com.ohayo.core.helper.preferences.DataStoreManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Created by Furuichi on 07/07/2022
 */
@InstallIn(SingletonComponent::class)
@Module
object CoreModule {

    @Singleton
    @ApplicationScope
    @Provides
    fun providesCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

    @Singleton
    @Provides
    fun provideDataStoreManager(dataStoreManagerImpl: DataStoreManagerImpl): DataStoreManager {
        return dataStoreManagerImpl
    }

    //region #CoroutineDispatcher
    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @MainImmediateDispatcher
    @Provides
    fun providesMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
    //endregion

}