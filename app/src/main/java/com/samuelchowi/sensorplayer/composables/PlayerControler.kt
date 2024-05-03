package com.samuelchowi.sensorplayer.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.samuelchowi.sensorplayer.player.PlayerState

@Composable
fun Controller(player: Player, playerState: PlayerState, content: @Composable () -> Unit) {
    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        if (player.isPlaying) {
            player.pause()
        }
    }

    LaunchedEffect(playerState) {
        when (playerState) {
            is PlayerState.Restart -> {
                player.setMediaItem(MediaItem.fromUri(playerState.track))
                player.prepare()
                player.play()
            }

            is PlayerState.PausePlay -> {
                if (player.isPlaying) player.pause()
                else player.play()
            }

            is PlayerState.SeekTo -> {
                player.seekTo(player.currentPosition + playerState.time)
            }

            is PlayerState.Volume -> {
                player.volume += playerState.volume
            }
        }
    }

    content()
}