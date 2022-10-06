package com.lm.firebasechat

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.Executors.newSingleThreadExecutor

interface FirebaseHandler {

    fun <T> runTask(task: Task<T>): Flow<RemoteLoadStates>

    class Base : FirebaseHandler {

        override fun <T> runTask(task: Task<T>) = channelFlow {
            task.apply {
                addOnSuccessListener(newSingleThreadExecutor())
                { trySendBlocking(RemoteLoadStates.Success(it)); close() }
                addOnFailureListener { trySendBlocking(RemoteLoadStates.Failure(it)); close() }
                awaitClose()
            }
        }.flowOn(IO)
    }
}