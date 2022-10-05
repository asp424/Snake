package com.lm.snake.ui.tetris_game

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lm.snake.ui.tetris_game.figures.Offsets.offsetsInt
import com.lm.snake.ui.tetris_game.figures.Offsets.sizeExt
import com.lm.snake.ui.tetris_game.figures.Offsets.sizeInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.round

fun MutableState<Float>.rotate(onRotate: () -> Unit) {
    value = when (value) {
        0f -> 90f
        90f -> 180f
        180f -> 270f
        270f -> 0f
        else -> 0f
    }
    onRotate()
}

val Int.screenSize get() = (round(toDouble()).toInt() / 10) * 10

fun timer(delay: Long, count: Int, height: Int, onTick: (Int) -> Unit) =
    CoroutineScope(Dispatchers.IO).launch {
        (2..count - height).asFlow().onEach { if (it != 2) delay(delay) }.collect {
            onTick(it)
        }
    }

fun rotation(
    rotation: MutableState<Float>,
    x: Int,
    isTransformable: Boolean,
    onRotate: (Int) -> Unit
) = if (isTransformable) rotation.rotate {
    when (x) {
        9 -> if (rotation.value == 270f) onRotate(-1)
        1 -> if (rotation.value == 90f) onRotate(1)
    }
} else Unit


fun DrawScope.drawFigure(figure: List<Offset>, rectSize: Float, border: Float = 5f) =
    rectSize.apply {
        figure.forEach {
            drawRect(Color.White, it, sizeExt)
            drawRect(Color.Blue, it.offsetsInt(border), sizeInt(border))
        }
    }

val randomFigure get() = (0..2).random()

