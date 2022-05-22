package com.lm.snake.ui

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset

@Composable
fun Main() {
    var countFrogs by remember { mutableStateOf(0) }
    var lose by remember { mutableStateOf(false) }

    Controller({ lose = true }) { countFrogs++ }
    LoseCard(lose, countFrogs)
}
