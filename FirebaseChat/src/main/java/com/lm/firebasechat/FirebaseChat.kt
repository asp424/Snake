package com.lm.firebasechat

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FirebaseChat private constructor(
    private val mainPath: String,
    private val meDigit: MeDigit,
    private val activity: ComponentActivity?,
    private val name: String,
    private val cryptoKey: String
) : DefaultLifecycleObserver {

    class Builder {
        private var mainNode: String = ""

        private var cryptoKey: String = "1111111111111111"

        private var name: String = ""

        private var meDigit: MeDigit = MeDigit.ZERO

        private var activity: ComponentActivity? = null

        fun setMainNode(mainPath: String) = apply { this.mainNode = mainPath }

        fun setMeDigit(meDigit: MeDigit) = apply { this.meDigit = meDigit }

        fun setCryptoKey(cryptoKey: String) = apply { this.cryptoKey = cryptoKey }

        fun setName(name: String) = apply { this.name = name }

        fun setActivity(activity: ComponentActivity) = apply { this.activity = activity }

        fun build() = FirebaseChat(mainNode, meDigit, activity, name, cryptoKey).apply {
            activity?.lifecycle?.addObserver(this)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        setOffline()
        setNoWriting()
        stopListener()
        activity?.lifecycle?.removeObserver(this)
    }

    fun setOnline() = save("1", Nodes.ONLINE.node())

    fun setOffline() = save("0", Nodes.ONLINE.node())

    fun setNoWriting() = save("0", Nodes.WRITING.node())

    fun setWriting() = save("1", Nodes.WRITING.node())

    fun saveMessage(text: String) {
        Nodes.MESSAGES.node().child.apply {
            firebaseHandler.runTask(
                updateChildren(
                    mapOf(randomId to crypto.cipherEncrypt("$getName: $text", cryptoKey))
                )
            )
        }
    }

    fun deleteAllMessages() = Nodes.MESSAGES.node().child.removeValue()

    fun getAndSaveToken(onGet: (String) -> Unit) {
        FirebaseMessaging.getInstance()
            .token.addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.result.toString()
                    saveToken(token)
                    onGet(token)
                }
            }
    }

    fun startListener(
        onMessage: (List<String>) -> Unit,
        onOnline: (String) -> Unit,
        onWriting: (String) -> Unit
    ) {
        stopListener()
        messagesJob = CoroutineScope(IO).launch {
            runListener(Nodes.MESSAGES.node(), ListenerMode.REALTIME).collect {
                if (it is RemoteLoadStates.Success<*>){
                   onMessage((it.data as DataSnapshot).children.toList().map { v ->
                       crypto.cipherDecrypt(v.value.toString(), cryptoKey)
                    })
                } else {
                    listOf(((it as RemoteLoadStates.Failure<*>).data as DatabaseError).message)
                }
            }
        }

        writingJob = CoroutineScope(IO).launch {
            runListener(Nodes.WRITING.node(), ListenerMode.REALTIME).collect {
                it.checkValue { v -> onWriting(v) }
            }
        }

        onlineJob = CoroutineScope(IO).launch {
            runListener(Nodes.ONLINE.node(), ListenerMode.REALTIME).collect {
                it.checkValue { v -> onOnline(v) }
            }
        }
    }

    fun stopListener(){
        messagesJob.cancel()
        writingJob.cancel()
        onlineJob.cancel()
    }

    fun sendNotification(apiKey: String) { readToken { fcmProvider.sendRemoteMessage(it, apiKey) } }

    private fun save(value: String, node: String) {
        node.getNode.child.apply {
            firebaseHandler.runTask(updateChildren(mapOf("0" to value)))
        }
    }

    private fun ProducerScope<RemoteLoadStates>.eventListener() =
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySendBlocking(RemoteLoadStates.Success(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(RemoteLoadStates.Failure(error))
            }
        }

    private fun saveToken(token: String) = firebaseHandler.runTask(
        Nodes.TOKEN.node().child.updateChildren(mapOf(getDigit to token))
    )

    private fun readToken(onRead: (String) -> Unit) {
        CoroutineScope(IO).launch {
            runListener(Nodes.TOKEN.node(), ListenerMode.SINGLE).collect {
                if (it is RemoteLoadStates.Success<*>) {
                    (it.data as DataSnapshot).children.map { what ->
                        if (what.key.toString() == getReverseDigit) {
                            onRead(what.value.toString())
                        }
                    }
                }
            }
        }
    }

    private fun runListener(node: String, mode: ListenerMode) = callbackFlow {
        node.getReverseNode.child.apply {
            when (mode) {
                ListenerMode.REALTIME -> eventListener()
                    .also { listener ->
                        addValueEventListener(listener)
                        awaitClose { removeEventListener(listener) }
                    }
                ListenerMode.SINGLE -> eventListener()
                    .also { listener ->
                        addListenerForSingleValueEvent(listener)
                        awaitClose { removeEventListener(listener) }
                    }
            }
        }
    }.flowOn(IO)

    private fun RemoteLoadStates.checkValue(onCheck: (String) -> Unit) {
        if (this is RemoteLoadStates.Success<*>) {
            (data as DataSnapshot).children.map { ds ->
                if (ds.key == "0") { onCheck(ds.value.toString()) }
            }
        }
    }

    private val getName get() = name.ifEmpty { if (meDigit == MeDigit.ZERO) "zero" else "one" }

    private val getPath
        get() = mainPath.ifEmpty { if (meDigit == MeDigit.ZERO) "default0" else "default1" }

    private val String.getNode get() = if (this == Nodes.MESSAGES.node()) this else "$this$getDigit"

    private val String.getReverseNode get() = when (this) {
            Nodes.MESSAGES.node() -> this
            Nodes.TOKEN.node() -> this
            else -> "$this$getReverseDigit"
        }

    private val getDigit get() = if (meDigit == MeDigit.ZERO) "0" else "1"

    private val getReverseDigit get() = if (meDigit == MeDigit.ZERO) "1" else "0"

    private val String.child get() = databaseReference.child(getPath).child(this)

    private val firebaseHandler by lazy { FirebaseHandler.Base() }

    private val fcmProvider by lazy { FCMProvider() }

    private val crypto by lazy { Crypto() }

    private val randomId get() = databaseReference.push().key.toString()

    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private var messagesJob: Job = Job()

    private var writingJob: Job = Job()

    private var onlineJob: Job = Job()
}
