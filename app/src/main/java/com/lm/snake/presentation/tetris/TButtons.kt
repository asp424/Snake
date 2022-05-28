package com.lm.snake.presentation.tetris

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TButtons(onClick: (String) -> Unit) {
    Column( Modifier
        .height(120.dp).padding(start = 40.dp, end = 40.dp, top = 10.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth().padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onClick("left") }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                )
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.White)
            }

            Button(
                onClick = { onClick("rotation") }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                )
            ) {
                Icon(Icons.Default.Sync, null, tint = Color.White)
            }

            Button(
                onClick = { onClick("right") }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                )
            ) {
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White)
            }
        }

        Button(
            onClick = { onClick("down") }, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue
            )
        ) {
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White)
        }
    }
}