package com.lm.snake.ui.snake_game

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext


val Float.generateFrog
    get() = Offset((2..28).random() * this, (2..40).random() * this)
val SnapshotStateList<Offset>.lastX get() = get(lastIndex).x
val SnapshotStateList<Offset>.lastY get() = get(lastIndex).y
val SnapshotStateList<Offset>.zeroX get() = get(0).x
val SnapshotStateList<Offset>.zeroY get() = get(0).y
val scope get() = CoroutineScope(Dispatchers.IO)
fun SnapshotStateList<Offset>.resetSnake(snakeCount: Int, size: Float) = apply {
    clear(); (0..snakeCount).onEach { add(Offset(it * size - snakeCount * size, 0f)) }
}

suspend fun SnapshotStateList<Offset>.onAutoTouch(level: Boolean, onCompare: () -> Unit) {
    withContext(Dispatchers.IO) {
        if (level)
            onEachIndexed { i, it ->
                if (lastX == it.x && lastY == it.y && i != lastIndex) {
                    onCompare()
                    cancel()
                }
            }
        else Unit
    }
}

fun SnapshotStateList<Offset>.onFrogTouch(
    width: Float,
    height: Float,
    frog: Offset,
    side: String,
    onCompare: () -> Unit
) {
    if (lastX + width == frog.x && lastY + height == frog.y) {
        when (side) {
            "right" -> add(0, Offset(zeroX + size, zeroY))
            "left" -> add(0, Offset(zeroX - size, zeroY))
            "up" -> add(0, Offset(zeroX, zeroY - size))
            "down" -> add(0, Offset(zeroX, zeroY + size))
        }
        onCompare()
    }
}

fun SnapshotStateList<Offset>.addCell(side: String, size: Float) {
    removeAt(0)
    when (side) {
        "right" -> add(Offset(lastX + size, lastY))
        "left" -> add(Offset(lastX - size, lastY))
        "up" -> add(Offset(lastX, lastY - size))
        "down" -> add(Offset(lastX, lastY + size))
    }
}

fun SnapshotStateList<Offset>.levelWork(
    level: Boolean,
    width: Float,
    height: Float,
    size: Float,
    onCompare: () -> Unit
) {
    when (level) {
        true -> {
            if (lastX !in -width + size * 2..width - size * 2 ||
                lastY !in -height + size * 2..height - size * 2
            ) onCompare()
        }
        false -> {
            when {
                lastX > width - size -> {
                    this[lastIndex] = Offset(lastX - (width * 2), lastY)
                }
                lastX < -width + size -> {
                    this[lastIndex] = Offset(lastX + (width * 2), lastY)
                }
                lastY > height - size -> {
                    this[lastIndex] = Offset(lastX, lastY - (height * 2))
                }
                lastY < -height + size -> {
                    this[lastIndex] = Offset(lastX, lastY + (height * 2))
                }
            }
        }
    }
}

