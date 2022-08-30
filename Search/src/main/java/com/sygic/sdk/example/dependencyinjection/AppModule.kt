package com.sygic.sdk.example.dependencyinjection

import android.content.Context
import com.sygic.sdk.example.common.ktx.SdkGpsManager
import com.sygic.sdk.example.common.ktx.SdkInitManager
import com.sygic.sdk.example.common.ktx.SdkPositionManager
import com.sygic.sdk.example.common.ktx.SdkSearchManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSdkInitManager(@ApplicationContext applicationContext: Context): SdkInitManager {
        return SdkInitManager(applicationContext)
    }

    @Singleton
    @Provides
    fun provideSdkGpsManager(): SdkGpsManager {
        return SdkGpsManager()
    }

    @Singleton
    @Provides
    fun provideSdkPositionManager(): SdkPositionManager {
        return SdkPositionManager()
    }

    @Singleton
    @Provides
    fun provideSdkSearchManager(): SdkSearchManager {
        return SdkSearchManager()
    }
}
