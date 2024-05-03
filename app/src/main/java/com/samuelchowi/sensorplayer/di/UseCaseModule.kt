package com.samuelchowi.sensorplayer.di

import com.samuelchowi.sensorplayer.usecase.LocationUseCase
import com.samuelchowi.sensorplayer.usecase.RotationUseCase
import com.samuelchowi.sensorplayer.usecase.ShakeDetectorUseCase
import com.samuelchowi.sensorplayer.usecase.impl.LocationUseCaseImpl
import com.samuelchowi.sensorplayer.usecase.impl.RotationUseCaseImpl
import com.samuelchowi.sensorplayer.usecase.impl.ShakeDetectorUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun providesRotationUseCase(rotationUseCase: RotationUseCaseImpl): RotationUseCase

    @Binds
    abstract fun providesLocationUseCase(locationUseCase: LocationUseCaseImpl): LocationUseCase

    @Binds
    abstract fun providesShakeDetectorUseCase(shakeDetectorUseCase: ShakeDetectorUseCaseImpl): ShakeDetectorUseCase
}