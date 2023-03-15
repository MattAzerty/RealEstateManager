package fr.melanoxy.realestatemanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*
Hilt's code generation, including a base class for your application
that serves as the application-level dependency container.
https://developer.android.com/training/dependency-injection/hilt-android?hl=fr
 */

@HiltAndroidApp
class MainApplication : Application()