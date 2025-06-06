package com.design_project.mais_paper.websocket

import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

object AppWebSocketManager : WebSocketManager by DefaultWebSocketManager("ws://192.168.100.92:8765")

class DefaultWebSocketManager(
    private val url: String
) : WebSocketManager {

    override val isConnected = MutableLiveData(false)
    override val incomingMessage = MutableLiveData("")

    private val client = OkHttpClient()
    private val request = Request.Builder().url(url).build()
    private var webSocket: WebSocket? = null

    private val listener =  WebSocketListener(
        onMessageReceived = { incomingMessage.postValue(it) },
        onDisconnected = { isConnected.postValue(false) },
        onConnected = { isConnected.postValue(true) }
    )

    override fun connect() {
        webSocket = client.newWebSocket(request, listener)
        isConnected.postValue(true)
    }

    override fun disconnect() {
        webSocket?.close(1000, "Manual disconnect")
        webSocket = null
        isConnected.postValue(false)
    }

    override fun send(message: String) {
        webSocket?.send(message)
    }
}
