package com.lm.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun Cell(offset: Offset) {
    LocalDensity.current.apply {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp.toPx()
        val rectSize = Size(30f, 30f)
        Canvas(Modifier) {
            drawRect(
                Color.Black,
                Offset(
                    screenWidth / 2 - 15f - 10.dp.toPx() + offset.x,
                    500.dp.toPx() / 2 - 15f - 10.dp.toPx() + offset.y
                ),
                rectSize
            )
        }
    }
}