package com.samuelchowi.sensorplayer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.samuelchowi.sensorplayer.player.PlayerScreen
import com.samuelchowi.sensorplayer.player.MainViewModel
import com.samuelchowi.sensorplayer.ui.theme.OverPlaySensorPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OverPlaySensorPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreen(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(viewModel, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(viewModel, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStop() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.unregisterListener(viewModel)
        sensorManager.unregisterListener(viewModel)
        super.onStop()
    }
}
