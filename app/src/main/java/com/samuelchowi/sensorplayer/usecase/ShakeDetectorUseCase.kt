package com.samuelchowi.sensorplayer.usecase

import kotlinx.coroutines.flow.Flow

interface ShakeDetectorUseCase {

    suspend fun detectShake(acceleration: FloatArray): Flow<Float>
}