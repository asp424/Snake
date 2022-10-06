package com.lm.firebasechat

sealed interface Nodes{
    object MESSAGES: Nodes
    object ONLINE: Nodes
    object WRITING: Nodes
    object TOKEN: Nodes

    fun node() = when(this){
        MESSAGES -> "messages"
        ONLINE -> "online"
        WRITING -> "writing"
        TOKEN -> "token"
    }
}