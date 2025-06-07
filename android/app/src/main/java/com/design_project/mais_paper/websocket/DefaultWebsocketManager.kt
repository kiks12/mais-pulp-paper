package com.design_project.mais_paper.websocket

import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

object AppWebSocketManager : WebSocketManager {

    private val _incomingMessage = MutableLiveData<String>()
    override val incomingMessage: MutableLiveData<String> get() = _incomingMessage

    private val _isConnected = MutableLiveData(false)
    override val isConnected: MutableLiveData<Boolean> get() = _isConnected

    private var instance: WebSocketManager? = null

    fun init(url: String) {
        if (instance == null) {
            val manager = DefaultWebSocketManager(url)
            instance = manager

            // Forward events
            manager.incomingMessage.observeForever { _incomingMessage.postValue(it) }
            manager.isConnected.observeForever { _isConnected.postValue(it) }

            manager.connect()
        }
    }

    fun reset(url: String) {
        instance?.disconnect()
        instance = null
        init(url)
    }

    override fun connect() = instance?.connect() ?: Unit
    override fun disconnect() = instance?.disconnect() ?: Unit
    override fun send(message: String) = instance?.send(message) ?: Unit
}

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
