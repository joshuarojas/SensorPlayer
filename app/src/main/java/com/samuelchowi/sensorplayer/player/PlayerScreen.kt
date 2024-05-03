package com.samuelchowi.sensorplayer.player

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.samuelchowi.sensorplayer.composables.OpenPermissionSettingsDialog
import com.samuelchowi.sensorplayer.composables.RequestPermissionDialog
import com.samuelchowi.sensorplayer.ui.theme.OverPlaySensorPlayerTheme


@SuppressLint("MissingPermission")
@ExperimentalPermissionsApi
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    LaunchedEffect(key1 = locationPermissionState) {
        if (locationPermissionState.allPermissionsGranted.not() && locationPermissionState.shouldShowRationale.not()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    if (locationPermissionState.allPermissionsGranted) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
        fusedLocationClient.requestLocationUpdates(locationRequest, viewModel, Looper.getMainLooper())

        PlayerContent(viewModel)
    } else if (locationPermissionState.shouldShowRationale) {
        RequestPermissionDialog(
            requestPermissionLauncher,
            message = "The app needs to be granted permissions to the GPS",
            permissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    } else {
        OpenPermissionSettingsDialog(message = "The app needs to be granted GPS permissions, we can take you to the app settings to enable this permissions")
    }
}

@Composable
fun PlayerContent(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    val actionsTracked by viewModel.actionsTracked.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    LaunchedEffect(playerState) {
        when (playerState) {
            is PlayerState.Restart -> {
                player.setMediaItem(MediaItem.fromUri((playerState as PlayerState.Restart).track))
                player.prepare()
                player.play()
            }

            is PlayerState.PausePlay -> {
                if (player.isPlaying) player.pause()
                else player.play()
            }

            is PlayerState.SeekTo -> {
                player.seekTo(player.currentPosition + (playerState as PlayerState.SeekTo).time)
            }

            is PlayerState.Volume -> {
                player.volume = player.volume + (playerState as PlayerState.Volume).volume
            }
        }
    }

    Column {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1.7f),
            factory = { PlayerView(context).apply { this.player = player } },
        )
        LazyColumn(contentPadding = PaddingValues(10.dp)) {
            if (actionsTracked.isEmpty()) {
                item {
                    Text(text = "No Actions Recorded Yet", fontStyle = FontStyle.Italic)
                }
            } else {
                items(actionsTracked.size) {
                    Text(text = actionsTracked[it])
                }
            }
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