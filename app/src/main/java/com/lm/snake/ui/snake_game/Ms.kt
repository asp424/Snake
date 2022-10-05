package com.lm.snake.ui.snake_game

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lm.snake.BuildConfig
import com.lm.snake.R
import com.lm.snake.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.*
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

const val NOTES = "notes"
const val TOKEN = "token"
const val WRITING1 = "writing1"
const val WRITING0 = "writing0"
const val ONLINE0 = "online0"
const val ONLINE1 = "online1"

fun notesList() = runListener(NOTES, ListenerMode.REALTIME)
fun token() = runListener(TOKEN, ListenerMode.SINGLE)
fun writing1() = runListener(WRITING1, ListenerMode.REALTIME)
fun writing0() = runListener(WRITING0, ListenerMode.REALTIME)
fun online0() = runListener(ONLINE0, ListenerMode.REALTIME)
fun online1() = runListener(ONLINE1, ListenerMode.REALTIME)

fun runListener(node: String, mode: ListenerMode) = callbackFlow {
    node.path.apply {
        when (mode) {
            ListenerMode.REALTIME -> eventListener(this@callbackFlow)
                .also { listener ->
                    addValueEventListener(listener)
                    awaitClose {
                        removeEventListener(listener)
                    }
                }
            ListenerMode.SINGLE -> eventListener(this@callbackFlow)
                .also { listener ->
                    addListenerForSingleValueEvent(listener)
                    awaitClose {
                        removeEventListener(listener)
                    }
                }
            ListenerMode.CHILD -> listener(this@callbackFlow)
                .also { listener ->
                    addChildEventListener(listener)
                    awaitClose { removeEventListener(listener) }
                }
        }
    }
}.flowOn(IO)

val String.path
    get() = fireBaseDatabase.child(this)

val fireBaseDatabase by lazy { FirebaseDatabase.getInstance().reference }

fun eventListener(scope: ProducerScope<RemoteLoadStates>) =
    object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            eventSnapshot(scope, snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            eventSnapshot(scope, error)
        }
    }

fun eventSnapshot(scope: ProducerScope<RemoteLoadStates>, snapshot: Any) = scope.trySendBlocking(
    when (snapshot) {
        is DataSnapshot -> RemoteLoadStates.Success(snapshot)
        is DatabaseError -> RemoteLoadStates.Failure(snapshot)
        else -> RemoteLoadStates.Loading
    }
)
fun listener(scope: ProducerScope<RemoteLoadStates>) =
    object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot(scope, snapshot,  0)
        }

        override fun onChildChanged(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {
            snapshot(scope, snapshot, 1)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {
            snapshot(scope, error, 0)
        }
    }

fun snapshot(
    scope: ProducerScope<RemoteLoadStates>,
    snapshot: Any, flag: Int
) = scope.trySendBlocking(
    when (snapshot) {
        is DataSnapshot -> if (flag == 0) RemoteLoadStates.Success(snapshot)
        else RemoteLoadStates.Update(snapshot)
        is DatabaseError -> RemoteLoadStates.Failure(snapshot)
        else -> RemoteLoadStates.Loading
    }
)

enum class ListenerMode {
    SINGLE, REALTIME, CHILD
}

sealed interface RemoteLoadStates {
    object Loading : RemoteLoadStates
    data class Success<T>(val data: T) : RemoteLoadStates
    data class Failure<T>(val data: T) : RemoteLoadStates
    data class Update<T>(val data: T) : RemoteLoadStates
    object Cancelled : RemoteLoadStates
    object EndLoading : RemoteLoadStates
    object Complete : RemoteLoadStates
}

fun saveNote(text: String) =
    runTask(
        NOTES.path.updateChildren(
            mapOf(randomId to cipherEncrypt("${if (side == "0") "Геннадич" else "Юрич"}: $text"))
        )
    )

fun saveWriting(isWriting: String) =
    runTask(
        (if (side == "0") WRITING0 else WRITING1).path.updateChildren(
            mapOf("0" to isWriting)
        )
    )

fun saveOnline(isOnline: String) =
    runTask(
        (if (side == "0") ONLINE0 else ONLINE1).path.updateChildren(
            mapOf("0" to isOnline)
        )
    )


fun <T> runTask(task: Task<T>) = callbackFlow {
    successFlow(task, this)
}.flowOn(IO)

suspend fun <T> successFlow(
    task: Task<T>, scope: ProducerScope<RemoteLoadStates>
) = with(scope) {
    task.apply {
        addOnSuccessListener(Executors.newSingleThreadExecutor())
        { trySendBlocking(RemoteLoadStates.Success(it)) }
        addOnCompleteListener(Executors.newSingleThreadExecutor())
        { trySendBlocking(RemoteLoadStates.Complete) }
        addOnCanceledListener { trySendBlocking(RemoteLoadStates.Cancelled) }
        addOnFailureListener { trySendBlocking(RemoteLoadStates.Failure(it)) }
        awaitClose { trySendBlocking(RemoteLoadStates.EndLoading) }
    }
}

val randomId get() = fireBaseDatabase.push().key.toString()

val <T> T.log get() = Log.d("My", toString())

val list: MutableState<RemoteLoadStates> = mutableStateOf(RemoteLoadStates.Loading)

var job: Job = Job()
var job1: Job = Job()
var job2: Job = Job()

val writing = mutableStateOf(false)

val isOnline = mutableStateOf(false)

fun deleteAll() = NOTES.path.removeValue()

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier =
    composed {
        clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
            onClick()
        }
    }

var counter = mutableStateOf(0)

var visibility = mutableStateOf(false)

val listNotesText
    get() = ((list.value as RemoteLoadStates.Success<*>).data as DataSnapshot).children.toList()
        .takeLast(50)

fun load() {
    if (counter.value == 10) {
        saveOnline("1")
        counter.value = 0
        list.value = RemoteLoadStates.Loading
        job.cancel()
        job = CoroutineScope(IO).launch {
            notesList().collect { list.value = it }
        }
        job1.cancel()
        job1 = CoroutineScope(IO).launch {
            (if (side == "0") writing1() else writing0()).collect{
                if (it is RemoteLoadStates.Success<*>) {
                    (it.data as DataSnapshot).children.map { ds ->
                        if (ds.key == "0"){
                            writing.value = ds.value != "0"
                        }
                    }
                }
            }
        }
        job2.cancel()
        job2 = CoroutineScope(IO).launch {
            (if (side == "0") online1() else online0()).collect{
                if (it is RemoteLoadStates.Success<*>) {
                    (it.data as DataSnapshot).children.map { ds ->
                        if (ds.key == "0"){
                            isOnline.value = ds.value != "0"
                        }
                    }
                }
            }
        }
        visibility.value = true
    }
}

fun close() {
    job.cancel()
    job1.cancel()
    job2.cancel()
    visibility.value = false; counter.value = 0
    saveWriting("0")
    saveOnline("0")
}

fun cipherEncrypt(text: String): String {
    instance().apply {
        return try {
            init(Cipher.ENCRYPT_MODE, BuildConfig.C_KEY.toByteArray())
            val encryptedValue = doFinal(text.toByteArray())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getEncoder().encodeToString(encryptedValue)
            } else {
                android.util.Base64.encodeToString(encryptedValue, android.util.Base64.DEFAULT)
            }
        } catch (e: Exception) {
            "error"
        }
    }
}

fun cipherDecrypt(text: String): String {
    instance().apply {
        return try {
            init(Cipher.DECRYPT_MODE, BuildConfig.C_KEY.toByteArray())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(doFinal(Base64.getMimeDecoder().decode(text)))
            } else {
                String(
                    doFinal(android.util.Base64.decode(text, android.util.Base64.DEFAULT))
                )
            }
        } catch (e: Exception) {
            "error"
        }
    }
}

fun Cipher.init(mode: Int, key: ByteArray) =
    init(mode, SecretKeySpec(key, "AES"), IvParameterSpec(key))

fun instance(): Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

class FirebaseService : FirebaseMessagingService() {

    private val manager by lazy { NotificationManagerCompat.from(applicationContext) }

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(applicationContext, resources.getString(R.string.id))
    }

    private val intent by lazy {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (!isRun()) showForegroundNotification()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showForegroundNotification() {
        manager.createNotificationChannel(
            NotificationChannel("1", "ass", NotificationManager.IMPORTANCE_DEFAULT)
        )
        manager.notify(
            1, notificationBuilder
                .setContentTitle("Snake")
                .setContentText("Let's play!")
                .setSmallIcon(R.drawable.logo_snake1)
                .setPriority(PRIORITY_MAX)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this, 0, intent, PendingIntent.FLAG_IMMUTABLE
                    )
                )
                .build()
        )
    }

    private fun isRun(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName
        for (i in runningProcesses) {
            if (i.importance == IMPORTANCE_FOREGROUND && i.processName == packageName) {
                return true
            }
        }
        return false
    }
}

fun sendRemoteMessage(token: String) {
    FCMApi.sendRemoteMessage(
        JSONObject()
            .put(
                "data", JSONObject().put("textMessage", "hello")
            )
            .put("registration_ids", JSONArray().put(token)).toString()
    )?.enqueue(object : Callback<String?> {
        override fun onResponse(call: Call<String?>, response: Response<String?>) {
            response.code().log
            response.errorBody()?.string().log
        }

        override fun onFailure(call: Call<String?>, t: Throwable) {}
    })
}

val FCMApi by lazy {
    Retrofit.Builder()
        .baseUrl(BuildConfig.FCM_BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build().create(ApiInterface::class.java)
}

interface ApiInterface {
    @Headers(
        "Authorization: key=${BuildConfig.FCM_SERVER_KEY}",
        "Content-Type:application/json"
    )
    @POST("send")
    fun sendRemoteMessage(@Body remoteBody: String?): Call<String?>?
}

fun getToken(sharedPreferences: SharedPreferences) {
    if (read(sharedPreferences).isEmpty()) FirebaseMessaging.getInstance()
        .token.addOnCompleteListener {
            if (it.isSuccessful) {
                runTask(TOKEN.path.updateChildren(mapOf(side to it.result.toString())))
                save(sharedPreferences)
            }
        }
}

private fun save(sharedPreferences: SharedPreferences) =
    sharedPreferences.edit().putString("id", "start").apply()

private fun read(sharedPreferences: SharedPreferences) =
    sharedPreferences.getString("id", "").toString()

const val side = "1"











