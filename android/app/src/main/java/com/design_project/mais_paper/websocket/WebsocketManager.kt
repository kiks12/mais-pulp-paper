package com.design_project.mais_paper.websocket

import androidx.lifecycle.MutableLiveData

interface WebSocketManager {
    val isConnected: MutableLiveData<Boolean>
    val incomingMessage: MutableLiveData<String>

    fun connect()
    fun disconnect()
    fun send(message: String)
}
