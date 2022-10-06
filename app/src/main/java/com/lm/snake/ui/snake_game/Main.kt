package com.lm.snake.ui.snake_game

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lm.firebasechat.FirebaseChat
import com.lm.snake.ui.Buttons
import com.lm.snake.ui.Menu
import com.lm.snake.ui.Statistic
import com.lm.snake.ui.theme.DarkGreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Main(firebaseChat: FirebaseChat) {
    LocalDensity.current.apply {
        val snakeCount = 5
        val rectSize = 30f
        val cardHeight = 500.dp
        val size = rectSize + 5f
        var lose by remember { mutableStateOf(false) }
        var job: Job by remember { mutableStateOf(Job()) }
        var level by remember { mutableStateOf(true) }
        var countFrogs by remember { mutableStateOf(0) }
        var frog by remember { mutableStateOf(size.generateFrog) }
        var side by remember { mutableStateOf("right") }
        var speed by remember { mutableStateOf(300L) }
        var menu by remember { mutableStateOf(true) }
        var replay by remember { mutableStateOf(false) }
        val width = LocalConfiguration.current.screenWidthDp.dp.toPx() / 2 - rectSize / 2
        val height = cardHeight.toPx() / 2 - rectSize / 2
        val cellsList = remember { mutableStateListOf<Offset>().resetSnake(snakeCount, size) }

        cellsList.apply {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Green)
                    .noRippleClickable { close() }
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Cells(rectSize, width, height, cardHeight)
                Buttons(side) { side = it }
            }

            Menu(menu, replay, lose, {
                menu = !menu; level = it; replay = true
                cellsList.resetSnake(snakeCount, size); lose = false
                frog = size.generateFrog
                side = "right"
                speed = 300L
                countFrogs = 0
            }) {
                menu = !menu; level = it; replay = true
                if (lose) {
                    cellsList.resetSnake(snakeCount, size); lose = false
                    frog = size.generateFrog
                    side = "right"
                    speed = 300L
                    countFrogs = 0
                }
            }

            Canvas(Modifier) { drawRect(DarkGreen, frog, Size(rectSize, rectSize)) }
            LoseCard(lose, countFrogs)
            Statistic(menu, countFrogs, level)

            LaunchedEffect(menu, lose) {
                if (!menu) {
                    job.cancel(); job = scope.launch {
                        while (true) {
                            levelWork(level, width, height, size) {
                                lose = true; cancel(); menu = true
                            }
                            addCell(side, size)
                            onAutoTouch(level) { lose = true; menu = true }
                            onFrogTouch(width, height, frog, side) {
                                frog = size.generateFrog; countFrogs++
                                if (speed in 10L..300L) speed -= 5L
                            }
                            delay(speed)
                        }
                    }
                } else job.cancel()
            }
        }
        if (counter.value == 10) load(firebaseChat)
        Notes(visibility.value, firebaseChat)
    }
}


