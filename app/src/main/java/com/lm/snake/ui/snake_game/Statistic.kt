package com.lm.snake.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.snake.ui.snake_game.Visibility

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Statistic(menu: Boolean, countFrogs: Int, level: Boolean ) {
    Visibility(visible = !menu) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 315.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Score: $countFrogs",
                modifier = Modifier.padding(top = 3.dp, start = 20.dp),
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp, color = Color.Black
            )
            Text(
                text = "Level: ${if (level) "Hard" else "Easy"}",
                modifier = Modifier.padding(top = 3.dp, start = 20.dp),
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp, color = Color.Black
            )
        }
    }
}