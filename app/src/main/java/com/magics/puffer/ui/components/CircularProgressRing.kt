package com.magics.puffer.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magics.puffer.ui.theme.*

/**
 * Animated circular progress ring for the dashboard.
 * Shows today's cigarette count vs. the daily target.
 *
 * @param smoked    Cigarettes already smoked today
 * @param target    Daily target (allowed)
 * @param size      Diameter of the ring
 */
@Composable
fun CircularProgressRing(
    smoked: Int,
    target: Int,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 16.dp
) {
    // Animate progress from 0 to current value on first composition
    val progress = if (target > 0) (smoked.toFloat() / target).coerceIn(0f, 1.5f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue  = progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label        = "ring_progress"
    )

    // Color changes based on how close to target: violet → orange → red
    val ringColor = when {
        smoked > target                    -> PufferRed
        smoked >= (target * 0.8f).toInt() -> PufferOrange
        else                               -> PufferViolet
    }
    val animatedColor by animateColorAsState(
        targetValue  = ringColor,
        animationSpec = tween(500),
        label        = "ring_color"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val diameter = minOf(this.size.width, this.size.height) - strokePx
            val topLeft  = Offset(strokePx / 2, strokePx / 2)
            val arcSize  = Size(diameter, diameter)

            // Background track
            drawArc(
                color      = PufferBorderGray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress arc — solid color (animateColorAsState gives us a single Color)
            drawArc(
                color      = animatedColor,
                startAngle = -90f,
                sweepAngle = (360f * animatedProgress.coerceAtMost(1f)),
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        // Center label
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = "$smoked",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = PufferWhite
                )
            )
            Text(
                text     = "of $target today",
                style    = MaterialTheme.typography.bodyMedium.copy(color = PufferGray),
                fontSize = 13.sp
            )
        }
    }
}
