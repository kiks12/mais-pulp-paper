package com.design_project.mais_paper

import java.util.Timer
import java.util.TimerTask

class StepTimerManager(
    private val steps: List<ProcessStep>,
    private val onStepUpdate: (stepName: String, elapsed: Long, total: Long) -> Unit,
    private val onStepComplete: (stepName: String) -> Unit,
    private val onWaitForUser: (stepName: String) -> Unit
) {
    private var currentStepIndex = 0
    private var currentElapsed = 0L
    private var timer: Timer? = null

    fun start() {
        if (steps.isNotEmpty()) runStep(currentStepIndex)
    }

    private fun startTimer(step: ProcessStep) {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                currentElapsed += 1000
                onStepUpdate(step.name, currentElapsed, step.durationMillis)

                if (currentElapsed >= step.durationMillis) {
                    timer?.cancel()
                    step.onNotify?.invoke()
                    onStepComplete(step.name)
                    if (currentStepIndex + 1 < steps.size) {
                        runStep(currentStepIndex + 1)
                    }
                }
            }
        }, 0, 1000)
    }

    private fun runStep(index: Int) {
        currentStepIndex = index
        val step = steps[index]
        currentElapsed = 0L

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                currentElapsed += 1000
                onStepUpdate(step.name, currentElapsed, step.durationMillis)

                if (currentElapsed >= step.durationMillis) {
                    timer?.cancel()
                    step.onNotify?.invoke()
                    onStepComplete(step.name)

                    // Wait for user if needed before proceeding
                    if (step.waitForUser) {
                        onWaitForUser(step.name) // Show message to tap "Continue"
                    } else {
                        if (index + 1 < steps.size) {
                            runStep(index + 1)
                        }
                    }
                }
            }
        }, 0, 1000)
    }


    fun continueAfterUserAction() {
        if (currentStepIndex + 1 < steps.size) {
            runStep(currentStepIndex + 1)
        }
    }

    fun stop() {
        timer?.cancel()
    }
}
