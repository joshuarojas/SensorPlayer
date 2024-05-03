package com.samuelchowi.sensorplayer.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun ActionList(actionsTracked: List<String>) {
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