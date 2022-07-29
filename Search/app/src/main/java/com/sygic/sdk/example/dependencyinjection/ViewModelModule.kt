package com.sygic.sdk.example.dependencyinjection

import com.sygic.sdk.example.common.data.MapFragmentDataModel
import com.sygic.sdk.map.data.SimpleCameraDataModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun provideSimpleCameraDataModel(): SimpleCameraDataModel {
        return SimpleCameraDataModel()
    }

    @ViewModelScoped
    @Provides
    fun provideMapFragmentDataModel(): MapFragmentDataModel {
        return MapFragmentDataModel()
    }
}