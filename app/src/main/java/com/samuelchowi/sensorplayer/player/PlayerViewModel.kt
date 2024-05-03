package com.samuelchowi.sensorplayer.player

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationListener
import com.samuelchowi.sensorplayer.usecase.LocationUseCase
import com.samuelchowi.sensorplayer.usecase.RotationUseCase
import com.samuelchowi.sensorplayer.usecase.ShakeDetectorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationUseCase: LocationUseCase,
    private val rotationUseCase: RotationUseCase,
    private val shakeDetectorUseCase: ShakeDetectorUseCase,
) : ViewModel(), LocationListener, SensorEventListener {

    private var _actionsTracked = MutableStateFlow(listOf<String>())
    val actionsTracked = _actionsTracked.asStateFlow()

    private var _playerState = MutableStateFlow<PlayerState>(PlayerState.Restart(TRACK))
    val playerState = _playerState.asStateFlow()

    override fun onLocationChanged(location: Location) {
        viewModelScope.launch {
            locationUseCase.processLocation(location).collect { distance ->
                _playerState.emit(PlayerState.Restart(TRACK))
                _actionsTracked.update { it + "Distance: $distance. Restarting Video" }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        viewModelScope.launch {
            when (event?.sensor?.type) {
                Sensor.TYPE_GYROSCOPE -> {
                    rotationUseCase.getRotationByAxis(event.timestamp, event.values).collect { rotation ->
                        if (rotation.first != null) {
                            val volume = (rotation.first!! * 0.001).toFloat()
                            _playerState.emit(PlayerState.Volume(volume))
                            _actionsTracked.update { it + "Rotating phone on x axis. Updating audio volume by $volume." }
                        }

                        if (rotation.second != null) {
                            val milliseconds = (rotation.second!! * 9).toInt()
                            _playerState.emit(PlayerState.SeekTo(milliseconds))
                            _actionsTracked.update { it + "Rotating phone on z axis. Updating video by $milliseconds ms." }
                        }
                    }
                }

                Sensor.TYPE_ACCELEROMETER -> {
                    shakeDetectorUseCase.detectShake(event.values).collect { accel ->
                        _playerState.emit(PlayerState.PausePlay(accel))
                        _actionsTracked.update { it + "Phone shaking. Pausing video" }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // no-op
    }

    companion object {
        private const val TRACK =
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
    }
}

sealed class PlayerState {
    data class Restart(val track: String) : PlayerState()
    data class PausePlay(val shake: Float) : PlayerState()
    data class SeekTo(val time: Int) : PlayerState()
    data class Volume(val volume: Float) : PlayerState()
}