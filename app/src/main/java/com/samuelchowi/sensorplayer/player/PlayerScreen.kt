package com.samuelchowi.sensorplayer.player

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.samuelchowi.sensorplayer.composables.ActionList
import com.samuelchowi.sensorplayer.composables.Controller
import com.samuelchowi.sensorplayer.composables.PermissionGrantedBox
import com.samuelchowi.sensorplayer.ui.theme.OverPlaySensorPlayerTheme


@SuppressLint("MissingPermission")
@ExperimentalPermissionsApi
@Composable
fun PlayerScreen(viewModel: MainViewModel) {
    PermissionGrantedBox {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
        fusedLocationClient.requestLocationUpdates(locationRequest, viewModel, Looper.getMainLooper())

        PlayerContent(viewModel)
    }
}

@Composable
fun PlayerContent(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    val actionsTracked by viewModel.actionsTracked.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    Controller(player = player, playerState = playerState) {
        Column {
            AndroidView(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(1.7f),
                factory = {
                    PlayerView(context).apply {
                        this.player = player
                    }
                },
            )
            ActionList(actionsTracked)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OverPlaySensorPlayerTheme {
        PlayerContent(viewModel())
    }
}