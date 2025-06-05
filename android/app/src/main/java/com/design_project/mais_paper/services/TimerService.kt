package com.design_project.mais_paper.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.design_project.mais_paper.ProcessStep
import com.design_project.mais_paper.ProcessUpdate
import com.design_project.mais_paper.StepTimerManager

class TimerService : Service() {

    lateinit var stepManager : StepTimerManager
    private val steps = listOf(
        ProcessStep("Grinding", 5_000), // 15_000
        ProcessStep("Boiling", 5_000, onNotify = { // 15 * 60_000
            sendNotification("Boiling Done", "Please open boiling lid")
            Log.w(TAG, "Open boiling lid")
        }, waitForUser = true),
        ProcessStep("Auger Feeder", 5_000), // 20_000
        ProcessStep("Pulping", 5_000, onNotify = { // 60_000
            sendNotification("Pulping Done", "Please open pulping lid")
            Log.w(TAG, "Open pulping lid")
        }, waitForUser = true),
        ProcessStep("Conveyor", 5_000), // 30_000
        ProcessStep("Drying", 5_000, onNotify = {
            ProcessUpdate.doneFlag.postValue(true)
        }) // 15 * 60_000
    )


    companion object {
        const val TAG = "TimerService"
    }

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    private fun startTimer() {
        stepManager = StepTimerManager(
            steps,
            onStepUpdate = { name, elapsed, total ->
                val text = "$name: ${elapsed/1000}s / ${total/1000}s"
                ProcessUpdate.text.postValue(text)
                Log.d(TAG, text)

                when (name) {
                    "Grinding" -> {
                        ProcessUpdate.grindingCurrentValue.postValue(elapsed/1000)
                        ProcessUpdate.grindingMaxValue.postValue(total/1000)
                    }
                    "Boiling" -> {
                        ProcessUpdate.boilingCurrentValue.postValue(elapsed/1000)
                        ProcessUpdate.boilingMaxValue.postValue(total/1000)
                    }
                    "Auger Feeder" -> {
                        ProcessUpdate.augerCurrentValue.postValue(elapsed/1000)
                        ProcessUpdate.augerMaxValue.postValue(total/1000)
                    }
                    "Pulping" -> {
                        ProcessUpdate.pulpingCurrentValue.postValue(elapsed/1000)
                        ProcessUpdate.pulpingMaxValue.postValue(total/1000)
                    }
                    "Conveyor" -> {
                        ProcessUpdate.conveyorCurrentValue.postValue(elapsed/1000)
                        ProcessUpdate.conveyorMaxValue.postValue(total/1000)
                    }
                    "Drying" -> {
                        ProcessUpdate.dryingCurrentValue.postValue(elapsed/1000)
                        ProcessUpdate.dryingMaxValue.postValue(total/1000)
                    }
                    else -> {
                        ProcessUpdate.current.postValue(elapsed/1000)
                        ProcessUpdate.maxValue.postValue(total/1000)
                    }
                }
            },
            onStepComplete = { name ->
                val text = "$name complete"
                ProcessUpdate.text.postValue(text)
                Log.d(TAG, "$name complete")
            },
            onWaitForUser = { name ->
                val text = "$name: Please perform action and tap 'Continue'"
                ProcessUpdate.text.postValue(text)
                Log.w(TAG, "$name: Please perform action and tap 'Continue'")
            }
        )

        stepManager.start()
    }


    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    private fun sendNotification(title: String, message: String, id: Int = 2) {
        val notification = NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle("Timer Running")
            .setContentText("Your timer is active.")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()

        startForeground(1, notification)
        startTimer()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_channel",
                "Timer Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}