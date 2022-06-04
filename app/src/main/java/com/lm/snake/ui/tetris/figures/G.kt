package com.lm.snake.ui.tetris.figures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.tetris.rotate

@Composable
fun G(rectSize: Int, x: Int, y: Int, rotation: Float, border: Float = 5f) {
    Canvas(
        Modifier
            .offset((x * rectSize).dp, (y * rectSize).dp)
            .rotate(rotation)
    ) {
        rectSize.dp.toPx().also { rs ->
            listOf(Offset(-rs, -rs), Offset(0f, -rs), Offset(-rs, 0f), Offset(-rs, rs))
                .forEach {
                    drawRect(Color.White, Offset(it.x, it.y), Size(rs, rs))
                    drawRect(
                        Color.Blue, Offset(it.x + border / 2, it.y + border / 2),
                        Size(rs - border, rs - border)
                    )
                }
        }
    }
}

fun gWork(
    action: String,
    rotation: MutableState<Float>,
    x: Int,
    y: Int,
    positionX: Int,
    positionY: Int,
    screenHeight: Int,
    rectSize: Int,
    onDone: (Int, Int) -> Unit
) {
    if (action == "rotation") rotation.rotate {
        x.apply {
            when (positionX) {
                9 -> if (rotation.value == 270f) onDone(0, x - 1)
                1 -> if (rotation.value == 90f) onDone(0, x + 1)
            }
            if (rotation.value == 0f && screenHeight == 0) onDone(1, y - 1)
        }
    }
    else positionX.apply {
        x.apply {
            when (action) {
                "right" -> onDone(0, moveRightG(rotation.value, x))
                "left" -> onDone(0, moveLeftG(rotation.value, x))
                "down" ->
                    if (rotation.value == 0f) {
                        if (screenHeight > rectSize) onDone(1, y + 1)
                    } else if (screenHeight > 0) onDone(1, y + 1)
            }
        }
    }
}


private fun Int.moveRightG(rotation: Float, x: Int): Int {
    if (rotation == 270f) {
        if (!stopPositionG(7)) return x.plus(1)
    } else if (!stopPositionG(8)) return x.plus(1)
    return x
}

private fun Int.moveLeftG(rotation: Float, x: Int): Int {
    if (rotation == 90f) {
        if (stopPositionG(2)) return x.minus(1)
    } else if (stopPositionG(1)) return x.minus(1)
    return x
}

private fun Int.stopPositionG(k: Int) = this > k
