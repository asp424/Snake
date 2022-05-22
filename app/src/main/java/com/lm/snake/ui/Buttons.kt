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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Buttons(side: String, onPress: (String) -> Unit) {
    Button(
        onClick = { if (side != "down") onPress("up") }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue),
        modifier = Modifier.padding(top = 35.dp)
    ) {
        Icon(Icons.Default.KeyboardArrowUp, null, tint = Color.White)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 100.dp, end = 100.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { if (side != "right") onPress("left") }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue)) {
            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.White)
        }

        Button(onClick = { if (side != "left") onPress("right") }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue)) {
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White)
        }
    }

    Button(onClick = { if (side != "up") onPress("down") }, colors = ButtonDefaults.buttonColors(
        containerColor = Color.Blue
    )) {
        Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White)
    }
}