package com.design_project.mais_paper.websocket

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import android.util.Log

class WebSocketListener(
    private val onMessageReceived: (String) -> Unit,
    private val onConnected: () -> Unit,
    private val onDisconnected: () -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Connected")
        onConnected()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Received: $text")
        onMessageReceived(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("WebSocket", "Received bytes: $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.d("WebSocket", "Closing: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Error: ${t.message}", t)
        onDisconnected()
    }
}
