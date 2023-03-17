package fr.melanoxy.realestatemanager.data.utils

import fr.melanoxy.realestatemanager.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildConfigResolver @Inject constructor() {
    val isDebug: Boolean
        get() = BuildConfig.DEBUG
}