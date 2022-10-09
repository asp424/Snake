package com.lm.snake.ui.snake_game

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebasechat.FirebaseChat
import com.lm.firebasechat.MeDigit
import com.lm.snake.BuildConfig
import com.lm.snake.R
import com.lm.snake.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val list: MutableState<UIStates> = mutableStateOf(UIStates.Loading)

val writing = mutableStateOf(false)

val isOnline = mutableStateOf(false)

var counter = mutableStateOf(0)

var visibility = mutableStateOf(false)

var notify = mutableStateOf(false)

val <T> T.log get() = Log.d("My", toString())

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier =
    composed {
        clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }) { onClick() }
    }

fun open(firebaseChat: FirebaseChat, coroutine: CoroutineScope) {
    if (counter.value == 10) {
        counter.value = 0
        visibility.value = true
        list.value = UIStates.Loading
        firebaseChat.setOnline()
        firebaseChat.startListener(
            onMessage = { list.value = UIStates.Success(it) },
            onOnline = { isOnline.value = it != "0" },
            onWriting = { writing.value = it != "0" },
            onNotify = {
                if (it == "ring") coroutine.launch {
                    notify.value = true
                    delay(3000)
                    notify.value = false
                    firebaseChat.clearNotify()
                }
            }
        )
    }
}

fun close(chat: FirebaseChat) {
    visibility.value = false; counter.value = 0; notify.value = false
    with(chat) { setOffline(); setNoWriting(); stopListener(); clearNotifyMe() }
}

val firebaseChat by lazy {
    FirebaseChat.Builder()
        .setName("Геннадич")
        .setMainNode("leha")
        .setCryptoKey(BuildConfig.C_KEY)
        .setMeDigit(MeDigit.ZERO)
        .build()
}

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService : FirebaseMessagingService() {

    private val notificationManager
            by lazy { NotificationManagerCompat.from(applicationContext) }

    private val activityManager
            by lazy { getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(applicationContext, resources.getString(R.string.id))
    }

    private val pendingIntent by lazy {
        PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }, PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (!isRun()) {
            showNotification(notificationManager, notificationBuilder, pendingIntent)
            firebaseChat.onGetNotify()
        }
    }

    private fun isRun(): Boolean {
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        for (i in runningProcesses) {
            if (i.importance == IMPORTANCE_FOREGROUND && i.processName == packageName) {
                return true
            }
        }
        return false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun showNotification(
    notificationManager: NotificationManagerCompat,
    notificationBuilder: NotificationCompat.Builder,
    pendingIntent: PendingIntent
) {
    notificationManager.createNotificationChannel(
        NotificationChannel("1", "ass", NotificationManager.IMPORTANCE_DEFAULT)
    )

    notificationManager.notify(
        1, notificationBuilder
            .setContentTitle("Snake")
            .setContentText("Let's play!")
            .setSmallIcon(R.drawable.logo_snake1)
            .setPriority(PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .build()
    )
}
sealed class UIStates {
    object Loading : UIStates()
    class Success(val list: List<String>) : UIStates()
}











