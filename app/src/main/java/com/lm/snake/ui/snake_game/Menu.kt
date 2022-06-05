package com.lm.snake.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.snake.ui.snake_game.Visibility

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Menu(
    menu: Boolean,
    replay: Boolean,
    lose: Boolean,
    onReplay: (Boolean) -> Unit,
    onStart: (Boolean) -> Unit
) {
    var selected by remember { mutableStateOf(true) }
    var state by remember { mutableStateOf(true) }
    Visibility(visible = menu) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            ElevatedCard() {
                Row(
                    modifier = Modifier.width(
                        animateDpAsState(
                            if (menu) 90.dp else 0.dp, tween(500)
                        ).value
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected, onClick = { selected = true })
                    Text(text = "Hard")
                }

                Row(
                    modifier = Modifier.width(
                        animateDpAsState(
                            if (menu) 90.dp else 0.dp, tween(500)
                        ).value
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = !selected, onClick = { selected = false })
                    Text(text = "Easy")
                }
            }
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = if (menu && replay && !lose) 210.dp else 160.dp)
            .offset(
                animateDpAsState(if (menu) 0.dp else 120.dp, tween(500)).value,
                animateDpAsState(if (menu) 0.dp else 100.dp, tween(500),
                    finishedListener = { state = !state }
                ).value
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FloatingActionButton(
            onClick = { onStart(selected) },
            containerColor = Color.White
        ) {
            Icon(if (state) Icons.Default.PlayArrow else Icons.Default.Pause, null)
        }
        Box(
            Modifier
                .size(animateDpAsState(if (menu && replay && !lose) 50.dp else 0.dp).value)
                .padding(top = 3.dp)
        ) {
            FloatingActionButton(
                onClick = { onReplay(selected) },
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Sync, null)
            }
        }
    }
}