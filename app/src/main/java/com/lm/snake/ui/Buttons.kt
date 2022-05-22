package com.lm.snake.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Buttons(side: String, onPress: (String) -> Unit) {
    Button(
        onClick = { if (side != "down") onPress("up") },
        modifier = Modifier.padding(top = 35.dp)
    ) {
        Icon(Icons.Default.KeyboardArrowUp, null)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 100.dp, end = 100.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { if (side != "right") onPress("left") }) {
            Icon(Icons.Default.KeyboardArrowLeft, null)
        }

        Button(onClick = { if (side != "left") onPress("right") }) {
            Icon(Icons.Default.KeyboardArrowRight, null)

        }
    }
    Button(onClick = { if (side != "up") onPress("down") }) {
        Icon(Icons.Default.KeyboardArrowDown, null)
    }
}