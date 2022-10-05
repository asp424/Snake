package com.lm.snake.ui.tetris_game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.tetris_game.figures.G.drawY
import com.lm.snake.ui.tetris_game.figures.tCell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tetris() {
    LocalConfiguration.current.apply {
        val screenWidth = screenWidthDp.screenSize
        val screenHeight = screenHeightDp.screenSize
        val rectSize = screenWidth / 10
        val cardHeight = screenHeightDp - (120 - (120 % rectSize)) - rectSize
        val listOfCells = remember {
            mutableListOf<SnapshotStateList<Int>>()
                .apply { (0..cardHeight / rectSize).onEach { add(row) } }
        }
        var x by remember { mutableStateOf(5) }
        var shiftValue by remember { mutableStateOf(1f) }
        var isTransformable by remember { mutableStateOf(true) }
        var shiftX by remember { mutableStateOf(0) }

        Card(
            Modifier
                .height(cardHeight.dp)
                .width(screenWidth.dp),
            border = BorderStroke(0.1.dp, Black),
            shape = RoundedCornerShape(0.dp)
        ) {

            Canvas(Modifier) {
                tCell(rectSize, listOfCells)
            }
        }

        TButtons(
            cardHeight,
            shiftX = {
                if (it == "right") shiftX += 1 else shiftX = -1
            },
            shiftY = {

            },
            rotation = {

            }
        )

        LaunchedEffect(true) {
            timer(700, listOfCells.size, 3) { listOfCells.drawY(it, shiftX) }
        }
    }
}

val row get() = mutableStateListOf<Int>().apply { (0..9).onEach { add(0) } }









