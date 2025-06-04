package com.design_project.mais_paper

data class ProcessStep(
    val name: String,
    val durationMillis: Long,
    val onNotify: (() -> Unit)? = null,
    val waitForUser: Boolean = false
)
