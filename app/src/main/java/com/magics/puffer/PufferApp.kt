package com.magics.puffer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp to enable Hilt dependency injection
 * throughout the entire app.
 */
@HiltAndroidApp
class PufferApp : Application()
