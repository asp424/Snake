package com.lm.snake.ui.tetris_game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.round

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

val Int.screenSize get() = (round(toDouble()).toInt() / 10) * 10

@Composable
fun CoroutineScope.DropWork(onShift: () -> Unit) =
    LaunchedEffect(true) { this@DropWork.launch {
        while (true) { delay(20); onShift() } }
    }

fun timer(delay: Long, onTick: () -> Unit) =
    CoroutineScope(Dispatchers.IO).launch {
        (0..1000).asFlow().onEach { if (it != 0) delay(delay) }.collect {
            onTick()
        }
    }

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onHoldFinger(isHold: (Boolean) -> Unit) =
    motionEventSpy { isHold(it.action == 0) }

