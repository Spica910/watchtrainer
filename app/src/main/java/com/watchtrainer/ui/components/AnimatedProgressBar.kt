package com.watchtrainer.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme

/**
 * 애니메이션이 적용된 프로그레스 바 컴포넌트
 * 운동 목표 달성도를 시각적으로 표시합니다.
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    var animatedProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(progress) {
        animatedProgress = 0f
        kotlinx.coroutines.delay(animationDelay.toLong())
        animate(
            initialValue = 0f,
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            animatedProgress = value
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
    ) {
        val strokeWidth = size.height
        val halfStroke = strokeWidth / 2
        
        // Background
        drawLine(
            color = backgroundColor,
            start = Offset(halfStroke, center.y),
            end = Offset(size.width - halfStroke, center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        
        // Progress
        if (animatedProgress > 0f) {
            drawLine(
                color = color,
                start = Offset(halfStroke, center.y),
                end = Offset(
                    halfStroke + (size.width - strokeWidth) * animatedProgress,
                    center.y
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * 원형 프로그레스 인디케이터
 * 원형으로 진행도를 표시합니다.
 */
@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
    strokeWidth: Float = 4.dp.value,
    animationDuration: Int = 1000
) {
    var animatedProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(progress) {
        animate(
            initialValue = animatedProgress,
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            animatedProgress = value
        }
    }
    
    Canvas(modifier = modifier) {
        val sweep = animatedProgress * 360f
        val startAngle = -90f
        
        // Background circle
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Progress arc
        if (animatedProgress > 0f) {
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}