package com.lm.snake.ui.tetris

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.tetris.figures.G
import com.lm.snake.ui.tetris.figures.gWork
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tetris() {
    LocalConfiguration.current.apply {
        val rotation = remember { mutableStateOf(0f) }
        val screenWidth = screenWidthDp.screenSize
        val screenHeight = screenHeightDp.screenSize
        val rectSize = screenWidth / 10
        val cardHeight = screenWidth * 1.5
        var positionX by remember { mutableStateOf(0) }
        var positionY by remember { mutableStateOf(0) }
        var x by remember { mutableStateOf(5) }
        var y by remember { mutableStateOf(2) }

        LaunchedEffect(x, y) { positionX = x; positionY = y }

        Column(Modifier.width(screenWidth.dp).fillMaxHeight().background(Color.Yellow)) {
            Card(
                Modifier.height(cardHeight.dp).width(screenWidth.dp),
                border = BorderStroke(0.1.dp, Color.Black),
                shape = RoundedCornerShape(0.dp)
            ) { G(rectSize, x, y, rotation.value) }

            TButtons() { action ->
                gWork(action, rotation, x, y, positionX, positionY, screenHeight, rectSize){ s, v ->
                    if (s == 0) x = v else y = v
                }
            }
        }
    }
}

private val Int.screenSize get() = (round(toDouble()).toInt() / 10) * 10

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


