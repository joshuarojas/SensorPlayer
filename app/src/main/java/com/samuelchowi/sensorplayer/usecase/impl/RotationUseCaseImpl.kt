package com.samuelchowi.sensorplayer.usecase.impl

import com.samuelchowi.sensorplayer.di.DefaultDispatcher
import com.samuelchowi.sensorplayer.usecase.RotationUseCase
import com.samuelchowi.sensorplayer.utils.minus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RotationUseCaseImpl @Inject constructor(
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : RotationUseCase {

    private var lastTimeStamp: Long = 0L
    private val orientationRadians: FloatArray = FloatArray(3)
    private var initialXAxisDegree: Double? = null
    private var initialZAxisDegree: Double? = null

    override suspend fun getRotationByAxis(timeStamp: Long, acceleration: FloatArray): Flow<Pair<Double?, Double?>> =
        flow {
            var rotationX: Double? = null
            var rotationZ: Double? = null
            if (lastTimeStamp != 0L) {
                val dT: Float = (lastTimeStamp - timeStamp) * NS2S

                val dx = acceleration[0] * dT
                val dy = acceleration[1] * dT
                val dz = acceleration[2] * dT

                orientationRadians[0] += dx
                orientationRadians[1] += dy
                orientationRadians[2] += dz

                with(Math.toDegrees(orientationRadians[0].toDouble())) {
                    if (initialXAxisDegree == null) {
                        initialXAxisDegree = this
                    }
                    if (this > initialXAxisDegree!! + THRESHOLD || this < initialXAxisDegree!! - THRESHOLD) {
                        rotationX = this.minus(initialXAxisDegree)
                    }
                }

                with(Math.toDegrees(orientationRadians[2].toDouble())) {
                    if (initialZAxisDegree == null) {
                        initialZAxisDegree = this
                    }
                    if (this > initialZAxisDegree!! + THRESHOLD || this < initialZAxisDegree!! - THRESHOLD) {
                        rotationZ = this.minus(initialZAxisDegree)
                    }
                }
            }
            lastTimeStamp = timeStamp

            if (rotationX != null || rotationZ != null) {
                emit(Pair(rotationX, rotationZ))
            }
        }.flowOn(coroutineDispatcher)

    companion object {
        private const val NS2S = 1.0f / 1000000000.0f
        private const val THRESHOLD = 20.0
    }
}