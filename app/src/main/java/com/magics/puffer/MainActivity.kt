package com.magics.puffer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.magics.puffer.ui.navigation.PufferNavHost
import com.magics.puffer.ui.theme.PufferTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity — Compose handles all screens via Navigation Compose.
 * Annotated with @AndroidEntryPoint to enable Hilt injection in the activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PufferTheme {
                PufferNavHost()
            }
        }
    }
}