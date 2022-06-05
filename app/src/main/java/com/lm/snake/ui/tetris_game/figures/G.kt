package com.lm.snake.ui.tetris_game.figures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.tetris_game.figures.OffsetsG.offsetsExt
import com.lm.snake.ui.tetris_game.figures.OffsetsG.offsetsInt
import com.lm.snake.ui.tetris_game.figures.OffsetsG.sizeExt
import com.lm.snake.ui.tetris_game.figures.OffsetsG.sizeInt
import com.lm.snake.ui.tetris_game.rotate

@Composable
fun G(
    rectSize: Int,
    x: Int,
    y: Int,
    rotation: Float,
    shiftX: Float,
    shiftY: Float,
    border: Float = 5f
) {
    Canvas(
        Modifier
            .offset(
                (x * rectSize).dp + shiftX.dp,
                (y * rectSize).dp + shiftY.dp
            )
            .rotate(rotation)
    ) {
        rectSize.dp.toPx().apply {
            offsetsExt.forEach {
                drawRect(White, it, sizeExt)
                drawRect(Blue, it.offsetsInt(border), sizeInt(border))
            }
        }
    }
}

object OffsetsG {
    val Float.sizeExt get() = Size(this, this)
    fun Float.sizeInt(border: Float) = Size(this - border, this - border)
    val Float.offsetsExt
        get() =
            listOf(Offset(-this, -this), Offset(0f, -this), Offset(-this, 0f), Offset(-this, this))

    fun Offset.offsetsInt(border: Float) = Offset(x + border / 2, y + border / 2)
}

fun gWork(
    action: String,
    rotation: MutableState<Float>,
    x: Int,
    onDoneX: (Int) -> Unit,
    onDoneY: (Float) -> Unit
) {
    x.apply {
        when (action) {
            "right" -> onDoneX(moveRightG(rotation.value, x))
            "left" -> onDoneX(moveLeftG(rotation.value, x))
            "down" -> onDoneY(1f)
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
