package com.lm.snake.ui.tetris_game

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TButtons(
    offsetY: Int,
    shiftX: (String) -> Unit,
    shiftY: (Boolean) -> Unit,
    rotation: () -> Unit
) {
    var shiftJob: Job by remember { mutableStateOf(Job()) }
    buttonEvents.apply {
        Column(
            Modifier
                .padding(start = 40.dp, end = 40.dp, top = 10.dp)
                .fillMaxSize()
                .offset(0.dp, offsetY.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    {}, Modifier.motionEventSpy {
                        if (it.action == 0) shiftX(LEFT)
                        else shiftJob.cancel()
                    },
                    colors = ButtonDefaults.buttonColors(Blue)
                ) { Icon(Icons.Default.KeyboardArrowLeft, null, tint = White) }

                Button(
                    { rotation() }, colors = ButtonDefaults.buttonColors(Blue)
                ) { Icon(Icons.Default.Sync, null, tint = White) }

                Button(
                    {}, Modifier.motionEventSpy {
                        if (it.action == 0) shiftX(RIGHT)
                        else shiftJob.cancel()
                    },
                    colors = ButtonDefaults.buttonColors(Blue)
                ) { Icon(Icons.Default.KeyboardArrowRight, null, tint = White) }
            }

            Button(
                onClick = {}, Modifier.motionEventSpy { shiftY(it.action == 0) },
                colors = ButtonDefaults.buttonColors(Blue),
            ) {
                Icon(Icons.Default.KeyboardArrowDown, null, tint = White)
            }
        }
    }
}




