package com.lm.snake.ui.tetris_game.figures

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.tetris_game.figures.Offsets.offsetsInt
import com.lm.snake.ui.tetris_game.figures.Offsets.sizeExt
import com.lm.snake.ui.tetris_game.figures.Offsets.sizeInt

fun DrawScope.tCell(rectSize: Int, listOfCells: List<SnapshotStateList<Int>>) {
    listOfCells.forEachIndexed { index, ints ->
        ints.forEachIndexed { ind, i ->
            if (i == 1) {
                    rectSize.dp.toPx().apply {
                        Offset(ind * this, index * this).also { offset ->
                            drawRect(Color.White, offset, sizeExt)
                        drawRect(Color.Blue, offset.offsetsInt(5f), sizeInt(5f))
                    }
                }
            }
        }
    }
}