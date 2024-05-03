package com.samuelchowi.sensorplayer.usecase.impl

import com.samuelchowi.sensorplayer.di.DefaultDispatcher
import com.samuelchowi.sensorplayer.usecase.ShakeDetectorUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.math.sqrt

class ShakeDetectorUseCaseImpl @Inject constructor(
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ShakeDetectorUseCase {

    private var accelCurrent = 0f
    private var accel = 0f

    override suspend fun detectShake(acceleration: FloatArray): Flow<Float> = flow {
        val x = acceleration[0]
        val y = acceleration[1]
        val z = acceleration[2]

        val accelLast = accelCurrent

        accelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta: Float = accelCurrent - accelLast
        accel = accel * 0.9f + delta
        if (accel > 12 && accel != accelCurrent) {
            emit(accel)
        }
    }.flowOn(coroutineDispatcher)
}