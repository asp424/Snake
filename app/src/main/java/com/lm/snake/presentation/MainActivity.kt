package com.lm.snake.presentation

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.lm.firebasechat.FirebaseChat
import com.lm.firebasechat.MeDigit
import com.lm.snake.BuildConfig.C_KEY
import com.lm.snake.ui.snake_game.*

class MainActivity : ComponentActivity() {

    private val manager by lazy { NotificationManagerCompat.from(applicationContext) }

    private val firebaseChat by lazy {
        FirebaseChat.Builder()
            .setName("Геннадич")
            .setMainNode("leha")
            .setCryptoKey(C_KEY)
            .setActivity(this)
            .setMeDigit(MeDigit.ZERO)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Main(firebaseChat) }
        if (read(sharedPreferences).isEmpty()) firebaseChat.getAndSaveToken {
            save(sharedPreferences)
        }
    }

    override fun onResume() {
        super.onResume()
        manager.cancelAll()
    }

    override fun onPause() {
        super.onPause()
        close()
    }

    private val sharedPreferences by lazy { getSharedPreferences("checkForFirst", MODE_PRIVATE) }

    private fun save(sharedPreferences: SharedPreferences) =
        sharedPreferences.edit().putString("id", "start").apply()

    private fun read(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString("id", "").toString()
}

