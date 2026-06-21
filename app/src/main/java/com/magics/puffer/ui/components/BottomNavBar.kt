package com.magics.puffer.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.magics.puffer.ui.theme.PufferGray
import com.magics.puffer.ui.theme.PufferViolet

/** Navigation destinations available from the bottom bar */
sealed class PufferScreen(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard    : PufferScreen("dashboard",    "Today",     Icons.Default.Home)
    data object Plan         : PufferScreen("plan",         "Plan",      Icons.Default.CalendarMonth)
    data object Stats        : PufferScreen("stats",        "Stats",     Icons.Default.BarChart)
    data object Achievements : PufferScreen("achievements", "Badges",    Icons.Default.EmojiEvents)
    data object SeedMode     : PufferScreen("seeds",        "Seeds 🌻",  Icons.Default.Spa)
}

val bottomNavItems = listOf(
    PufferScreen.Dashboard,
    PufferScreen.Plan,
    PufferScreen.Stats,
    PufferScreen.Achievements,
    PufferScreen.SeedMode
)

/**
 * Bottom navigation bar for Puffer with animated selected indicator.
 *
 * @param currentRoute  Active route string
 * @param onNavigate    Called with the new route when a tab is tapped
 * @param seedUnlocked  Whether Seed Mode tab is accessible
 */
@Composable
fun PufferBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    seedUnlocked: Boolean = false
) {
    NavigationBar(
        containerColor = com.magics.puffer.ui.theme.PufferDarkCard,
        tonalElevation = 0.dp,
        modifier = Modifier.height(64.dp)
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            // Seed Mode tab: always visible but with a special look
            val isSeedTab = screen is PufferScreen.SeedMode

            // Subtle bounce animation when selected
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.1f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "nav_scale_${screen.route}"
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (isSeedTab && !seedUnlocked) return@NavigationBarItem
                    onNavigate(screen.route)
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        modifier = Modifier.scale(scale),
                        tint = when {
                            selected             -> PufferViolet
                            isSeedTab && !seedUnlocked -> PufferGray.copy(alpha = 0.4f)
                            else                 -> PufferGray
                        }
                    )
                },
                label = {
                    Text(
                        screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) PufferViolet else PufferGray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PufferViolet.copy(alpha = 0.15f)
                )
            )
        }
    }
}
