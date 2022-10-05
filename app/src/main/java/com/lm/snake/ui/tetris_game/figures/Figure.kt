package com.lm.snake.ui.tetris_game.figures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.tetris_game.buttonEvents
import com.lm.snake.ui.tetris_game.drawFigure
import com.lm.snake.ui.tetris_game.timer
import kotlinx.coroutines.Job

@Composable
fun Figure(
    figure: Int,
    cardHeight: Int,
    rectSize: Int, x: Int,
    rotation: Float,
    shiftValue: Float,
    isTransformable: Boolean,
    onBottom: () -> Unit
) {
    var shiftJob: Job by remember { mutableStateOf(Job()) }
    var shiftYa by remember { mutableStateOf(0f) }
    var shiftValueY by remember { mutableStateOf(1f) }
    var isTransformableLocal by remember { mutableStateOf(isTransformable) }

    Canvas(
        Modifier
            .offset((x * rectSize).dp, (2 * rectSize).dp + shiftYa.dp)
            .rotate(rotation)
    ) {
        rectSize.dp.toPx().also { rS ->
            when (figure) {
                //0 -> OffsetsG.apply { drawFigure(rS.offsetsExt, rS) }
                1 -> OffsetsStick.apply { drawFigure(rS.offsetsExt, rS) }
                2 -> OffsetsRect.apply { drawFigure(rS.offsetsExt, rS) }
            }
        }
    }
    LaunchedEffect(shiftYa) {
        if (shiftYa.toInt() > (cardHeight - 3 * rectSize)) {
            shiftJob.cancel(); isTransformableLocal = false; onBottom()
        }
    }

    LaunchedEffect(true, isTransformableLocal) {
        //shiftJob = timer(10) { if (isTransformableLocal) shiftYa += shiftValueY }
    }

    LaunchedEffect(shiftValue) { shiftValueY = shiftValue }

    LaunchedEffect(isTransformable) { isTransformableLocal = isTransformable }
}


fun shiftX(
    action: String,
    rotation: MutableState<Float>,
    x: Int,
    isTransformable: Boolean,
    onDoneX: (Int) -> Unit
) = if (isTransformable) with(buttonEvents) {
    when (action) {
        RIGHT -> onDoneX(x.moveRight(rotation.value, x))
        LEFT -> onDoneX(x.moveLeft(rotation.value, x))
    }
} else Unit

fun shiftY(
    cardHeight: Int, isHold: Boolean,
    rotation: MutableState<Float>,
    shiftY: Float,
    isTransformable: Boolean
) = if (isTransformable) if (isHold) 4f else 1f else 0f


private fun Int.moveRight(rotation: Float, x: Int): Int {
    if (rotation == 270f) {
        if (!stopPosition(7)) return x.plus(1)
    } else if (!stopPosition(8)) return x.plus(1)
    return x
}

private fun Int.moveLeft(rotation: Float, x: Int): Int {
    if (rotation == 90f) {
        if (stopPosition(2)) return x.minus(1)
    } else if (stopPosition(1)) return x.minus(1)
    return x
}

private fun Int.stopPosition(k: Int) = this > k
