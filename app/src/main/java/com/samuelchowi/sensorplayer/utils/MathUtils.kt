package com.samuelchowi.sensorplayer.utils

fun Double?.minus(num: Double?) =
    this.getOrZero() - num.getOrZero()

fun Double?.getOrZero() = this ?: 0.0