package com.lm.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.unit.Dp

@Composable
fun Cell(offset: Offset, rectSize: Float, width: Float, height: Float) {
    Canvas(Modifier) {
        drawRect(
            Blue,
            Offset(width - rectSize + offset.x, height - rectSize + offset.y),
            Size(rectSize, rectSize)
        )
    }
}
