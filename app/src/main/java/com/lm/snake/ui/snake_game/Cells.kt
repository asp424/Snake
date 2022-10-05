package com.lm.snake.ui.snake_game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotStateList<Offset>.Cells(
    rectSize: Float,
    width: Float,
    height: Float,
    cardHeight: Dp
) {
    Card(
        Modifier
            .height(cardHeight)
            .fillMaxWidth()
            .padding(10.dp)
            .noRippleClickable { counter.value = counter.value + 1 }
        , border = BorderStroke(2.dp, Color.Black),

        shape = RoundedCornerShape(10.dp)
    )
    {
        onEach {
            Cell(it, rectSize, width, height)
        }
    }
}



