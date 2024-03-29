package com.lm.snake.ui.snake_game

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lm.firebasechat.FirebaseChat
import com.lm.snake.BuildConfig
import com.lm.snake.ui.theme.DarkGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Notes(visibility: Boolean, firebaseChat: FirebaseChat) {
    val getCoroutine = rememberCoroutineScope()
    Visibility(visible = visibility) {
        with(firebaseChat) {
            if (list.value is UIStates.Success) {
                val listMessages = (list.value as UIStates.Success).list
                Scaffold(content = {
                    val state = rememberLazyListState()

                    LaunchedEffect(true) {
                        delay(300)
                        state.animateScrollToItem(listMessages.size)
                    }

                    LazyColumn(
                        content = {
                            items(listMessages) {
                                Text(
                                    text = it.parseMessage(),
                                    modifier = Modifier
                                        .padding(bottom = 5.dp)
                                )
                            }
                        }, modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            bottom = 100.dp, start = 10.dp, end = 10.dp, top = 60.dp
                        ), state = state
                    )

                    var text by remember { mutableStateOf("") }
                    val width = LocalConfiguration.current.screenWidthDp.dp
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(start = 20.dp, bottom = 2.dp)
                                .offset(
                                    animateDpAsState(
                                        if (writing.value) 0.dp else (-100).dp
                                    ).value
                                )
                        ) {
                            Text(
                                text = "writing...",
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(value = text, onValueChange = {
                                text = it
                                if (it.isNotEmpty()) firebaseChat.setWriting()
                                else firebaseChat.setNoWriting()
                            }, modifier = Modifier
                                .width(width - 100.dp)
                                .onFocusEvent { focusState ->
                                    if (focusState.isFocused) {
                                        getCoroutine.launch {
                                            delay(300)
                                            state.animateScrollToItem(listMessages.size)
                                        }
                                    }
                                })
                            FloatingActionButton(onClick = {
                                if (text.isNotEmpty()) {
                                    saveMessage(text); text = ""
                                    firebaseChat.sendNotification(BuildConfig.FCM_SERVER_KEY)
                                }
                                firebaseChat.setNoWriting()
                            }) {
                                Icon(Icons.Default.Send, null)
                            }
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(end = 20.dp, bottom = 60.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Icon(
                            Icons.Rounded.Notifications, null, tint = DarkGreen,
                            modifier = Modifier
                                .size(40.dp)
                                .scale(
                                    animateFloatAsState(
                                        if (notify.value) 1f else 0f
                                    ).value
                                )
                        )
                    }
                    LocalDensity.current.apply {
                        FloatingActionButton(
                            onClick = { deleteAllMessages() },
                            shape = CircleShape, containerColor = Color.Red,
                            modifier = Modifier
                                .size(40.dp)
                                .offset(width - 50.dp, 10.dp)
                        ) {
                            Icon(Icons.Rounded.Delete, null, tint = Color.White)
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 35.dp, top = 35.dp)
                    ) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier, colors = CardDefaults.cardColors(
                                containerColor = if (isOnline.value) Color.Green else Color.Red
                            ), border = BorderStroke(1.dp, Color.White)
                        ) {
                            Text(
                                text = if (isOnline.value) "online" else "offline",
                                modifier = Modifier.padding(5.dp),
                                color = Color.White
                            )
                        }
                    }
                })
            } else {
                Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
