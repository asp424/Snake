package com.lm.snake.ui.tetris_game.figures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

object OffsetsStick {
    val Float.offsetsExt get() = listOf(
            Offset(-this, -this), Offset(-this, -2 * this),
            Offset(-this, 0f), Offset(-this, this)
        )
}