package com.samuelchowi.sensorplayer.usecase

import kotlinx.coroutines.flow.Flow

interface RotationUseCase {

    suspend fun getRotationByAxis(timeStamp: Long, acceleration: FloatArray): Flow<Pair<Double?, Double?>>
}