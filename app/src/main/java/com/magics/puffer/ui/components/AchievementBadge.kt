package com.magics.puffer.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magics.puffer.domain.model.Achievement
import com.magics.puffer.ui.theme.*

/**
 * Badge card for a single achievement.
 * Locked achievements appear dimmed with a lock icon.
 * Unlocked achievements show with a glowing gradient border and emoji.
 */
@Composable
fun AchievementBadge(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val borderBrush = if (achievement.isUnlocked) {
        Brush.linearGradient(listOf(PufferViolet, PufferTeal))
    } else {
        Brush.linearGradient(listOf(PufferBorderGray, PufferBorderGray))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PufferDarkCard)
            .border(
                width = if (achievement.isUnlocked) 1.5.dp else 1.dp,
                brush  = borderBrush,
                shape  = RoundedCornerShape(16.dp)
            )
            .alpha(if (achievement.isUnlocked) 1f else 0.45f)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Emoji in a circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked)
                            Brush.radialGradient(listOf(PufferVioletGlow, PufferDeepNavy))
                        else
                            Brush.radialGradient(listOf(PufferBorderGray, PufferDeepNavy))
                    )
            ) {
                Text(
                    text = if (achievement.isUnlocked) achievement.emoji else "🔒",
                    fontSize = 24.sp
                )
            }

            // Title + description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = achievement.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (achievement.isUnlocked) PufferWhite else PufferGray
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = achievement.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color  = PufferGray,
                        fontSize = 11.sp
                    ),
                    maxLines = 2
                )
                if (achievement.isUnlocked && achievement.requiredDays > 0) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "${achievement.requiredDays} days smoke-free ✓",
                        style = MaterialTheme.typography.labelSmall.copy(color = PufferTeal),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
