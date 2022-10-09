package com.lm.snake.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.lm.snake.ui.snake_game.Main
import com.lm.snake.ui.snake_game.close
import com.lm.snake.ui.snake_game.firebaseChat

class MainActivity : ComponentActivity() {

    private val manager by lazy { NotificationManagerCompat.from(applicationContext) }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Main(firebaseChat) }
        packageManager.getPackageInfo(packageName, 0).longVersionCode.also { version ->
            readVersion().also { savedVersion ->
                if (savedVersion == 0L || version != savedVersion) {
                    firebaseChat.getAndSaveToken { saveVersion(version) }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        manager.cancelAll()
    }

    override fun onPause() {
        super.onPause()
        close(firebaseChat)
    }

    private val sharedPreferences by lazy { getSharedPreferences("checkForFirst", MODE_PRIVATE) }

    private fun saveVersion(value: Long) = sharedPreferences.edit().putLong("version", value).apply()

    private fun readVersion() = sharedPreferences.getLong("version", 0L)
}

