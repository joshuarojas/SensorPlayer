package com.samuelchowi.sensorplayer.usecase

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationUseCase {

    suspend fun processLocation(location: Location): Flow<Float>
}