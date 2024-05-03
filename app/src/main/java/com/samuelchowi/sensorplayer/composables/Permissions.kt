package com.samuelchowi.sensorplayer.composables

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionGrantedBox(body: @Composable () -> Unit) {
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
        body()
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