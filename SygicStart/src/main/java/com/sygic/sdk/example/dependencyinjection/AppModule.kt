package com.sygic.sdk.example.com.sygic.sdk.example.dependencyinjection

import android.content.Context
import com.sygic.sdk.example.common.ktx.SdkGpsManager
import com.sygic.sdk.example.common.ktx.SdkInitManager
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
}
