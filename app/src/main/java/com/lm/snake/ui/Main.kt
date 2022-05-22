package com.lm.snake.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.snake.ui.theme.DarkGreen
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    LocalDensity.current.apply {
        val snakeCount = 5
        val rectSize = 30f
        val cardHeight = 500.dp
        val size = rectSize + 5f
        var lose by remember { mutableStateOf(false) }
        var countFrogs by remember { mutableStateOf(0) }
        var frog by remember { mutableStateOf(size.generateFrog) }
        var side by remember { mutableStateOf("right") }
        var speed by remember { mutableStateOf(300L) }
        var menu by remember { mutableStateOf(true) }
        val width = LocalConfiguration.current.screenWidthDp.dp.toPx() / 2 - rectSize / 2
        val height = cardHeight.toPx() / 2 - rectSize / 2
        val cellsList = remember { mutableStateListOf<Offset>().reset(snakeCount, size) }

        cellsList.apply {
            Column(
                Modifier.fillMaxSize().background(Yellow),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Card(
                    Modifier
                        .height(cardHeight)
                        .fillMaxWidth()
                        .padding(10.dp), border = BorderStroke(2.dp, Black),
                    shape = RoundedCornerShape(10.dp)
                )
                { onEach { Cell(it, rectSize, width, height) } }
                Text(
                    text = "Score: $countFrogs",
                    modifier = Modifier.padding(top = 3.dp, start = 20.dp),
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp, color = Color.Black
                )
                Buttons(side) { side = it }
                LaunchedEffect(menu, lose) {
                    if (!menu && !lose) scope.launch {
                        while (true) {
                            removeAt(0)
                            when (side) {
                                "right" -> add(Offset(lastX + size, lastY))
                                "left" -> add(Offset(lastX - size, lastY))
                                "up" -> add(Offset(lastX, lastY - size))
                                "down" -> add(Offset(lastX, lastY + size))
                            }
                            if (lastX !in -width + size..width - size ||
                                lastY !in -height + size..height - size
                            ) {
                                lose = true; cancel()
                            }
                            withContext(IO) {
                                onEachIndexed { i, it ->
                                    if (lastX == it.x && lastY == it.y && i != lastIndex
                                    ) {
                                        lose = true; cancel()
                                    }
                                }
                            }

                            if (lastX + width == frog.x && lastY + height == frog.y) {
                                frog = size.generateFrog
                                countFrogs++
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

            Canvas(Modifier) { drawRect(DarkGreen, frog, Size(rectSize, rectSize)) }
            Menu(menu) { menu = false }
            LoseCard(lose, countFrogs) {
                cellsList.reset(snakeCount, size); lose = false
                frog = size.generateFrog
                side = "right"
                speed = 300L
                countFrogs = 0
            }
        }
    }
}


private val Float.generateFrog
    get() = Offset((2..28).random() * this, (2..40).random() * this)
private val SnapshotStateList<Offset>.lastX get() = get(lastIndex).x
private val SnapshotStateList<Offset>.lastY get() = get(lastIndex).y
private val SnapshotStateList<Offset>.zeroX get() = get(0).x
private val SnapshotStateList<Offset>.zeroY get() = get(0).y
private val scope get() = CoroutineScope(IO)
private fun SnapshotStateList<Offset>.reset(snakeCount: Int, size: Float) = apply {
    clear(); (0..snakeCount).onEach { add(Offset(it * size - snakeCount * size, 0f)) }
}


