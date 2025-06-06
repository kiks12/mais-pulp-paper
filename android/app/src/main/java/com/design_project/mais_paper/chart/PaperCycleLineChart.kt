package com.design_project.mais_paper.chart

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.design_project.mais_paper.room.PaperCycle
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun PaperCycleLineChart(paperCycles: List<PaperCycle>) {
    val labels = paperCycles.map { it.date }
    val data = paperCycles.map { it.count.toDouble() }

    if (paperCycles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No Data to show")
        }
    }

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(horizontal = 22.dp),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.labelSmall,
            padding = 16.dp,
            labels = labels,
            builder = { modifier,label,shouldRotate,index ->
                Text(modifier=modifier,text=label)
            }
        ),
        data = listOf(
            Line(
                label = "Paper",
                values = data,
                color = SolidColor(Color(0xFF23af92)),
                firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(width = 2.dp),
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color.White),
                    strokeWidth = 4.dp,
                    radius = 7.dp,
                    strokeColor = SolidColor(Color(0xFF23af92)),
                )
            )
        ),
        curvedEdges = false,
        animationMode = AnimationMode.Together(delayBuilder = {
            it * 500L
        }),
    )
}
