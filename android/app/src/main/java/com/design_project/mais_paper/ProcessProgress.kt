package com.design_project.mais_paper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProcessProgress(name: String, progress: Float) {
    Box(
        modifier = Modifier.height(215.dp).padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = 100F,
            color = Color.LightGray
        )
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = if(progress.isNaN()) 0F else progress
        )
        Text(text = "$name")
    }
}