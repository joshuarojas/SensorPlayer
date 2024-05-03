package com.samuelchowi.sensorplayer.composables

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestPermissionDialog(
    launcher: ManagedActivityResultLauncher<Array<String>, *>,
    message: String,
    permissions: Array<String>
) {
    val context = (LocalContext.current as? Activity)
    AlertDialog(
        modifier = Modifier
            .background(Color.DarkGray, RoundedCornerShape(10.dp))
            .padding(16.dp),
        onDismissRequest = { context?.finish() }
    ) {
        Column {
            Text(text = message)
            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = { launcher.launch(permissions) }) {
                Text(text = "Request Permissions")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenPermissionSettingsDialog(message: String) {
    val context = (LocalContext.current as? Activity)
    AlertDialog(
        modifier = Modifier
            .background(Color.DarkGray, RoundedCornerShape(10.dp))
            .padding(16.dp),
        onDismissRequest = { context?.finish() }
    ) {
        Column {
            Text(text = message)
            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context?.packageName, null)
                )
                context?.startActivity(intent)
            }) {
                Text(text = "Open the app settings")
            }
        }
    }
}