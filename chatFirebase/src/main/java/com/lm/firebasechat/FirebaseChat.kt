package com.lm.firebasechat

import android.annotation.SuppressLint
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.random.Random

class FirebaseChat constructor(
    private val mainPath: String,
    private val meDigit: MeDigit,
    private val name: String,
    private val cryptoKey: String
) {

    class Builder {
        private var mainNode: String = ""

        private var cryptoKey: String = "1111111111111111"

        private var name: String = ""

        private var meDigit: MeDigit = MeDigit.ZERO

        fun setMainNode(mainPath: String) = apply { this.mainNode = mainPath }

        fun setMeDigit(meDigit: MeDigit) = apply { this.meDigit = meDigit }

        fun setCryptoKey(cryptoKey: String) = apply { this.cryptoKey = cryptoKey }

        fun setName(name: String) = apply { this.name = name }

        fun build() = FirebaseChat(mainNode, meDigit, name, cryptoKey)
    }

    fun setOnline() = save(ONE, Nodes.ONLINE.node())

    fun setOffline() = save(ZERO, Nodes.ONLINE.node())

    fun setNoWriting() = save(ZERO, Nodes.WRITING.node())

    fun setWriting() = save(ONE, Nodes.WRITING.node())

    fun clearNotify() = runTask(Nodes.NOTIFY.node()
        .getReverseNode.child.updateChildren(mapOf(ZERO to CLEAR_NOTIFY)))

    fun clearNotifyMe() = save(CLEAR_NOTIFY, Nodes.NOTIFY.node())

    fun onGetNotify() = save(SET_NOTIFY, Nodes.NOTIFY.node())

    fun saveMessage(text: String) {
        runTask(
            Nodes.MESSAGES.node().child.updateChildren(
                mapOf(
                    randomId to crypto.cipherEncrypt(
                        "$getNameLit($currentTime): $text", cryptoKey
                    )
                )
            )
        )
    }

    fun deleteAllMessages() = Nodes.MESSAGES.node().child.removeValue()

    fun getAndSaveToken(onGet: (String) -> Unit) =
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toString().also { token -> saveToken(token); onGet(token) }
            }
        }

    fun startListener(
        onMessage: (List<String>) -> Unit,
        onOnline: (String) -> Unit,
        onWriting: (String) -> Unit,
        onNotify: (String) -> Unit
    ) {
        stopListener()
        messagesJob = CoroutineScope(IO).launch {
            runListener(Nodes.MESSAGES.node(), ListenerMode.REALTIME).collect {
                if (it is RemoteLoadStates.Success<*>) {
                    onMessage((it.data as DataSnapshot).children.toList().map { v ->
                        crypto.cipherDecrypt(v.value.toString(), cryptoKey)
                    })
                } else {
                    listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
                }
            }
        }

        writingJob = listener(Nodes.WRITING.node()) { onWriting(it) }

        onlineJob = listener(Nodes.ONLINE.node()) { onOnline(it) }

        notifyJob = listener(Nodes.NOTIFY.node()) { onNotify(it) }
    }

    fun stopListener() {
        messagesJob.cancel(); writingJob.cancel(); onlineJob.cancel(); notifyJob.cancel()
    }

    fun sendNotification(apiKey: String) = readToken { fcmProvider.sendRemoteMessage(it, apiKey) }

    fun String.parseMessage() = "${substringBefore(TIME_TAG_START)}${
        getTime(substringAfter(TIME_TAG_START).substringBefore(TIME_TAG_END).toLong())
    }${substringAfter(TIME_TAG_END)}"

    fun String.isread() =
        substringAfter(WAS_READ_TAG_START).substringBefore(WAS_READ_TAG_END).toBoolean()

    fun String.setWasRead() = replace(WAS_NO_READ_TAG, WAS_READ_TAG)

    fun String.isMe(): Boolean {
        if (startsWith(ME_TAG_ONE)) {
            if (meDigit == MeDigit.ONE) return true
        } else {
            if (meDigit == MeDigit.ZERO) return true
        }
        return false
    }

    private fun listener(node: String, onGet: (String) -> Unit) =
        CoroutineScope(IO).launch {
            runListener(node, ListenerMode.REALTIME).collect { it.checkValue { v -> onGet(v) } }
        }

    private fun save(value: String, node: String) =
        runTask(node.getNode.child.updateChildren(mapOf(ZERO to value)))

    private fun ProducerScope<RemoteLoadStates>.eventListener() =
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySendBlocking(RemoteLoadStates.Success(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(RemoteLoadStates.Failure(error))
            }
        }

    private fun saveToken(token: String) = runTask(
        Nodes.TOKEN.node().child.updateChildren(mapOf(getDigit to token))
    )

    private fun readToken(onRead: (String) -> Unit) =
        CoroutineScope(IO).launch {
            runListener(Nodes.TOKEN.node(), ListenerMode.SINGLE).collect {
                it.snapshot { w ->
                    if (w.key.toString() == getReverseDigit) onRead(w.value.toString())
                }
            }
        }

    private fun RemoteLoadStates.checkValue(onCheck: (String) -> Unit) =
        snapshot { ds -> if (ds.key == ZERO) onCheck(ds.value.toString()) }

    private inline fun RemoteLoadStates.snapshot(crossinline onGet: (DataSnapshot) -> Unit) {
        if (this is RemoteLoadStates.Success<*>) (data as DataSnapshot).children.map { onGet(it) }
    }

    private fun runListener(node: String, mode: ListenerMode) = callbackFlow {
        node.getReverseNode.child.apply {
            eventListener().also { listener ->
                when (mode) {
                    ListenerMode.REALTIME -> addValueEventListener(listener)
                    ListenerMode.SINGLE -> addListenerForSingleValueEvent(listener)
                }
                awaitClose { removeEventListener(listener) }
            }
        }
    }

    private fun getTime(wasDate: Long) = with(formatDate("H:mm", wasDate)) {
        when (calendar.get(Calendar.DAY_OF_YEAR) - formatDate("D", wasDate).toInt()) {
            1 -> "yesterday at $this"
            0 -> this
            else -> formatDate("d:MM:yy ", wasDate)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(value: String, date: Long): String {
        return SimpleDateFormat(value).apply { timeZone = TimeZone.getDefault() }.format(date)
    }

    private fun <T> runTask(task: Task<T>) = channelFlow {
        task.apply {
            addOnSuccessListener(Executors.newSingleThreadExecutor())
            { trySendBlocking(RemoteLoadStates.Success(it)); close() }
            addOnFailureListener { trySendBlocking(RemoteLoadStates.Failure(it)); close() }
            awaitClose()
        }
    }.flowOn(IO)

    private val getName get() = name.ifEmpty { getMeTag }

    private val getPath
        get() = mainPath.ifEmpty {
            if (meDigit == MeDigit.ZERO) DEFAULT_PATH_ZERO else DEFAULT_PATH_ONE
        }

    private val String.getNode get() = if (this == Nodes.MESSAGES.node()) this else "$this$getDigit"

    private val String.getReverseNode
        get() = when (this) {
            Nodes.MESSAGES.node() -> this
            Nodes.TOKEN.node() -> this
            else -> "$this$getReverseDigit"
        }

    private val getMeTag get() = if (meDigit == MeDigit.ZERO) ME_TAG_ZERO else ME_TAG_ONE

    private val getNameLit get() = name.ifEmpty { if (meDigit == MeDigit.ZERO) ZERO else ONE }

    private val getDigit get() = if (meDigit == MeDigit.ZERO) ZERO else ONE

    private val getReverseDigit get() = if (meDigit == MeDigit.ZERO) ONE else ZERO

    private val String.child get() = databaseReference.child(getPath).child(this)

    private val randomId get() = databaseReference.push().key.toString()

    private val calendar get() = Calendar.getInstance()

    private val currentTime get() = "$TIME_TAG_START${calendar.time.time}$TIME_TAG_END"

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private val fcmProvider by lazy { FCMProvider() }

    private val crypto by lazy { Crypto() }

    private var messagesJob: Job = Job()

    private var writingJob: Job = Job()

    private var onlineJob: Job = Job()

    private var notifyJob: Job = Job()

    companion object {
        private const val TIME_TAG_START = "<T>"
        private const val TIME_TAG_END = "</T>"
        private const val WAS_READ_TAG_START = "<r>"
        private const val ME_TAG_START = "<n>"
        private const val ME_TAG_END = "</n>"
        private const val WAS_READ_TAG_END = "</r>"
        private const val WAS_READ_TAG = "${WAS_READ_TAG_START}true$WAS_READ_TAG_END"
        private const val ME_TAG_ONE = "<one>"
        private const val ME_TAG_ZERO = "<zero>"
        private const val WAS_NO_READ_TAG = "${WAS_READ_TAG_START}false$WAS_READ_TAG_END"
        private const val ZERO = "0"
        private const val ONE = "1"
        private const val CLEAR_NOTIFY = "none"
        private const val SET_NOTIFY = "ring"
        private const val DEFAULT_PATH_ZERO = "default0"
        private const val DEFAULT_PATH_ONE = "default1"
    }
}

