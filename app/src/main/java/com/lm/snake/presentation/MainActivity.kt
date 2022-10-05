package com.lm.snake.presentation

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.lm.snake.ui.snake_game.Main
import com.lm.snake.ui.snake_game.close
import com.lm.snake.ui.snake_game.getToken
import com.lm.snake.ui.snake_game.saveOnline

class MainActivity : ComponentActivity() {

    private val manager by lazy { NotificationManagerCompat.from(applicationContext) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { Main() }
        getToken(sharedPreferences)
    }

    override fun onResume() {
        super.onResume()
         manager.cancelAll()
    }

    override fun onPause() {
        super.onPause()
         close()
        saveOnline("0")
    }

    private val sharedPreferences by lazy { getSharedPreferences("checkForFirst", MODE_PRIVATE) }
}

