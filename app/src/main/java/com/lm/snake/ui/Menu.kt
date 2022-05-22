package com.lm.snake.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lm.snake.presentation.MainActivity

@Composable
fun Menu(menu: Boolean, onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(
            animateDpAsState(
            if (menu) 60.dp else 0.dp, tween(500)
        ).value)) {
            (LocalContext.current as MainActivity).apply {
                FloatingActionButton(onClick = { onStart() }, containerColor = Color.White) {
                    Icon(Icons.Default.PlayArrow, null)
                }
            }
        }
    }
}
