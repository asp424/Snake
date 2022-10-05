package com.lm.snake.ui.tetris_game.figures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

object OffsetsRect {
    val Float.offsetsExt
        get() = listOf(
            Offset(-this, -this), Offset(0f, 0f),
            Offset(-this, 0f), Offset(0f, -this)
        )
}