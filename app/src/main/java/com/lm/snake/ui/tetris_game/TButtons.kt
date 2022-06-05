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

@Composable
fun TButtons(
    offsetY: Int,
    onHold: (String, Boolean) -> Unit
    ) {
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
                {}, Modifier.onHoldFinger{ onHold("left", it) },
                colors = ButtonDefaults.buttonColors(Blue)
            ) { Icon(Icons.Default.KeyboardArrowLeft, null, tint = White) }

            Button(
                { onHold("rotation", false) }, colors = ButtonDefaults.buttonColors(Blue)
            ) { Icon(Icons.Default.Sync, null, tint = White) }

            Button(
                {}, Modifier.onHoldFinger{ onHold("right", it) },
                colors = ButtonDefaults.buttonColors(Blue)
            ) { Icon(Icons.Default.KeyboardArrowRight, null, tint = White) }
        }

        Button(
            onClick = {}, Modifier.onHoldFinger{ onHold("down", it) },
            colors = ButtonDefaults.buttonColors(Blue),
        ) {
            Icon(Icons.Default.KeyboardArrowDown, null, tint = White)
        }
    }
}





