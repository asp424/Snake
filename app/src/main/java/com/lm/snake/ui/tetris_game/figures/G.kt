package com.lm.snake.ui.tetris_game.figures

import androidx.compose.runtime.snapshots.SnapshotStateList

object G{
    fun List<SnapshotStateList<Int>>.drawY(shiftY: Int, shiftX: Int) = this.apply {
        get(shiftY - 1)[5 + shiftX] = 0;
        get(shiftY)[5 + shiftX] = 1;
        //get(shiftY - 1)[4 + shiftX] = 0;
        //get(shiftY)[4 + shiftX] = 1;
        //get(shiftY - 1)[4 + shiftX] = 0;
        //get(shiftY + 1)[4 + shiftX] = 1
       // get(shiftY - 1)[4 + shiftX] = 0;
       // get(shiftY + 2)[4 + shiftX] = 1
    }

    fun List<SnapshotStateList<Int>>.drawX(shift: Int) = this.apply {
        get(shift)[5] = 1
        get(shift)[4] = 1
        get(shift + 1)[4] = 1
        get(shift + 2)[4] = 1
    }
}