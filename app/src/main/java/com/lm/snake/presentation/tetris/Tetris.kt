package com.lm.snake.presentation.tetris

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tetris() {
    LocalDensity.current.apply {
        val rotation = remember { mutableStateOf(0f) }
        val offset = remember { mutableStateOf(Offset.Zero) }
        var actualOffset by remember { mutableStateOf(Offset.Zero) }
        val screenWidth = LocalConfiguration.current.screenWidthDp.absoluteValue
        val screenHeight = LocalConfiguration.current.screenHeightDp.absoluteValue
        val rectSize = (screenWidth / 10).absoluteValue
        val rectSizeValue = (screenWidth / 10).absoluteValue.dp.value
        val rectSizePx = (screenWidth / 10).absoluteValue * (density.absoluteValue)
        val cardWidth = rectSize * 10
        val cardHeight = rectSize * 10

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Yellow),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Top
        ) {
            Card(
                Modifier
                    .height(cardHeight.absoluteValue.dp)
                    .width(cardWidth.absoluteValue.dp),
                border = BorderStroke(0.1.dp, Color.Black),
                shape = RoundedCornerShape(0.dp)
            ) {
                G(cardWidth,
                    rectSizePx,
                    offset.value,
                    rotation.value
                ) { actualOffset = Offset(it.x, it.y) }
            }

            TButtons() { press ->
                if (press == "rotation") rotation.rotate {
                    when (actualOffset.x) {
                        4 * rectSizePx ->
                            when (rotation.value) {
                                270f -> offset.value = Offset(
                                    offset.value.x - rectSizeValue,
                                    offset.value.y
                                )
                            }

                        -4 * rectSizePx -> when (rotation.value) {
                            90f ->
                                offset.value = Offset(
                                    offset.value.x + rectSizeValue,
                                    offset.value.y
                                )
                        }
                    }
                    if (screenHeight == 0)
                        when (rotation.value) {
                            0f -> offset.value = Offset(
                                offset.value.x,
                                offset.value.y - rectSizeValue
                            )
                        }
                }
                else {
                    when (press) {
                        "right" ->
                            if (rotation.value == 270f) {
                                if (actualOffset.x < 3 * rectSizePx) {
                                    offset.onPress(offset.value, rectSizeValue, press)
                                }
                            } else if (actualOffset.x < 4 * rectSizePx) {
                                offset.onPress(offset.value, rectSizeValue, press)
                            }
                        "left" ->
                            if (rotation.value == 90f) {
                                if (actualOffset.x > -3 * rectSizePx) {
                                    offset.onPress(offset.value, rectSizeValue, press)
                                }
                            } else if (actualOffset.x > - 4 * rectSizePx) {
                                offset.onPress(offset.value, rectSizeValue, press)
                            }
                        "down" ->
                            if (rotation.value == 0f) {
                                if (screenHeight > rectSizePx) {
                                    offset.onPress(offset.value, rectSizeValue, press)
                                }
                            } else if (screenHeight > 0) {
                                offset.onPress(offset.value, rectSizeValue, press)
                            }
                    }
                }
            }
        }
        Column(Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Center) {

            Text(
                text = "cardWidth ${screenWidth}",
                modifier = Modifier.offset(0.dp, (-100).dp)
            )
            Text(
                text = "actX ${actualOffset.x}",
                modifier = Modifier.offset(0.dp, (-100).dp)
            )
            Text(
                text = "rs ${rectSizePx}",
                modifier = Modifier.offset(0.dp, (-100).dp)
            )

        }
    }
}


@Composable
fun G(
    cardWidth: Int,
    rectSize: Float,
    offset: Offset,
    rotation: Float,
    border: Float = 5f,
    actualOffset: (Offset) -> Unit
) {
    LocalDensity.current.apply {
        Canvas(
            Modifier
                .offset(offset.x.dp + cardWidth.dp / 2, offset.y.dp)
                .rotate(rotation)
                .onPlaced { actualOffset(it.localToWindow(Offset.Zero)) }
        ) {
            listOf(
                Offset(-rectSize, -rectSize), Offset(0f, -rectSize),
                Offset(-rectSize, 0f), Offset(-rectSize, rectSize)
            ).forEach {
                drawRect(
                    White,
                    Offset(it.x, it.y),
                    Size(rectSize, rectSize)
                )
                drawRect(
                    Blue,
                    Offset(it.x + border / 2, it.y + border / 2),
                    Size(rectSize - border, rectSize - border)
                )
            }
        }
    }
}


fun MutableState<Offset>.onPress(offset: Offset, step: Float, press: String) {
    value = when (press) {
        "left" -> Offset(offset.x - step, offset.y)
        "right" -> Offset(offset.x + step, offset.y)
        "down" -> Offset(offset.x, offset.y + step)
        else -> Offset.Zero
    }
}

fun MutableState<Float>.rotate(onRotate: () -> Unit) {
    value = when (value) {
        0f -> 90f
        90f -> 180f
        180f -> 270f
        270f -> 0f
        else -> 0f
    }
    onRotate()
}


