package com.lm.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Controller(onLose: () -> Unit, onFrogEat: () -> Unit) {
    LocalDensity.current.apply {
        val snakeCount = 5
        val rectSize = 30f
        val padding = 5f
        val size = rectSize + padding
        var frog by remember { mutableStateOf(size.generateFrog) }
        var side by remember { mutableStateOf("right") }
        var speed by remember { mutableStateOf(300L) }
        val coroutine = rememberCoroutineScope()
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp.toPx() / 2
        val height = 500.dp.toPx() / 2 - size / 2 - 10.dp.toPx()
        remember {
            mutableStateListOf<Offset>().apply {
                (0..snakeCount).onEach { add(Offset(it * size, 0f)) }
            }
        }.apply {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ElevatedCard(
                    Modifier
                        .height(500.dp)
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                { onEach { Cell(it) } }

                Buttons(side) { side = it }

                LaunchedEffect(true) {
                    coroutine.launch(Dispatchers.IO) {
                        while (true) {
                            removeAt(0)
                            when (side) {
                                "right" -> add(Offset(lastX + size, lastY))
                                "left" -> add(Offset(lastX - size, lastY))
                                "up" -> add(Offset(lastX, lastY - size))
                                "down" -> add(Offset(lastX, lastY + size))
                            }
                            if (lastX !in size -screenWidth .. screenWidth - size
                                || lastY !in -height .. height
                            ) { onLose(); cancel(); break }

                            if (lastX + screenWidth - size / 2
                                in frog.x - padding .. frog.x + padding && lastY
                                + height in frog.y - padding .. frog.y + padding
                            ) {
                                frog = size.generateFrog
                                onFrogEat()
                                when (side) {
                                    "right" -> add(0, Offset(zeroX + size, zeroY))
                                    "left" -> add(0, Offset(zeroX - size, zeroY))
                                    "up" -> add(0, Offset(zeroX, zeroY - size))
                                    "down" -> add(0, Offset(zeroX, zeroY + size))
                                }
                                speed -= 10L
                            }
                            delay(speed)
                        }
                    }
                }
            }
            Canvas(Modifier) { drawRect(Black, frog, Size(rectSize, rectSize)) }
        }
    }
}

    private val Float.generateFrog get() =
    Offset((2..28).random() * this, (2..40).random() * this)
    private val SnapshotStateList<Offset>.lastX get() = get(lastIndex).x
    private val SnapshotStateList<Offset>.lastY get() = get(lastIndex).y
    private val SnapshotStateList<Offset>.zeroX get() = get(0).x
    private val SnapshotStateList<Offset>.zeroY get() = get(0).y