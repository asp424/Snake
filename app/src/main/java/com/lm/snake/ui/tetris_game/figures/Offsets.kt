package com.lm.snake.ui.tetris_game.figures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

object Offsets {
    val Float.sizeExt get() = Size(this, this)
    fun Float.sizeInt(border: Float) = Size(this - border, this - border)
    fun Offset.offsetsInt(border: Float) = Offset(x + border / 2, y + border / 2)
}
