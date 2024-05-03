package com.samuelchowi.sensorplayer.usecase.impl

import android.location.Location
import com.samuelchowi.sensorplayer.di.DefaultDispatcher
import com.samuelchowi.sensorplayer.usecase.LocationUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocationUseCaseImpl @Inject constructor(
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : LocationUseCase {

    private lateinit var _lastTrackedLocation: Location

    override suspend fun processLocation(location: Location): Flow<Float> = flow {
        if (::_lastTrackedLocation.isInitialized.not()) {
            _lastTrackedLocation = location
        } else {
            val distance = location.distanceTo(_lastTrackedLocation)
            if (distance >= 10.0f) {
                _lastTrackedLocation = location
                emit(distance)
            }
        }
    }.flowOn(coroutineDispatcher)
}