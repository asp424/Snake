package com.lm.snake.ui.tetris_game

data class ButtonEvents(
    val DOWN: String = "down",
    val RIGHT: String = "right",
    val LEFT: String = "left",
    val ROTATION: String = "rotation"
)

val buttonEvents = ButtonEvents()
