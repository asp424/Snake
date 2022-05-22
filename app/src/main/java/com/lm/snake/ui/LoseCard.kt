package com.lm.snake.ui

import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.snake.presentation.MainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoseCard(lose: Boolean, countFrogs: Int) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .width(
                    animateDpAsState(
                        if (lose) 100.dp else 0.dp
                    ).value
                )
                .height(
                    animateDpAsState(
                        if (lose) 110.dp else 0.dp
                    ).value
                ), border = BorderStroke(2.dp, Color.Red), shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "You lose",
                modifier = Modifier.padding(top = 20.dp, start = 16.dp),
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp
            )

            Text(
                text = "Score: $countFrogs",
                modifier = Modifier.padding(top = 3.dp, start = 20.dp),
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp
            )
            Box(modifier = Modifier.padding(top = 6.dp, start = 40.dp)) {
                (LocalContext.current as MainActivity).apply {
                    Icon(Icons.Default.Sync, null, modifier = Modifier.clickable {
                        setContent { Main() }
                    })
                }
            }
        }
    }
}