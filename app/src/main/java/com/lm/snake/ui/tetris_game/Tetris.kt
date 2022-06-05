package com.lm.snake.ui.tetris_game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.tetris_game.figures.G
import com.lm.snake.ui.tetris_game.figures.gWork
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tetris() {
    LocalConfiguration.current.apply {
        val rotation = remember { mutableStateOf(0f) }
        val screenWidth = screenWidthDp.screenSize
        val screenHeight = screenHeightDp.screenSize
        val rectSize = screenWidth / 10
        val cardHeight = screenHeightDp - (120 - (120 % rectSize)) - rectSize
        var x by remember { mutableStateOf(5) }
        var shiftX by remember { mutableStateOf(0f) }
        var y by remember { mutableStateOf(2) }
        var shiftY by remember { mutableStateOf(0f) }
        val coroutine = rememberCoroutineScope()
        var shiftJob: Job by remember { mutableStateOf(Job()) }

        LaunchedEffect(shiftY) {
            if (shiftY.toInt() == cardHeight - 3 * rectSize) { coroutine.cancel(); shiftJob.cancel() }
        }

        Card(
            Modifier
                .height(cardHeight.dp)
                .width(screenWidth.dp),
            border = BorderStroke(0.1.dp, Black),
            shape = RoundedCornerShape(0.dp)
        ) {
            G(rectSize, x, y, rotation.value, shiftX, shiftY)
        }

        TButtons(cardHeight) { action, isHold ->
            if (action == "rotation") rotation.rotate {
                when (x) {
                    9 -> if (rotation.value == 270f) x -= 1
                    1 -> if (rotation.value == 90f) x += 1
                }
            } else if (isHold)
                shiftJob = timer(5L) { gWork(action, rotation, x, onDoneX = { x = it },
                    onDoneY = { shiftY += 1f }) }
            else shiftJob.cancel()
        }

        coroutine.DropWork { shiftY += 1f }
    }
}






