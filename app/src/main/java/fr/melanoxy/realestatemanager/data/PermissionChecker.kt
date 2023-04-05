package fr.melanoxy.realestatemanager.data

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionChecker @Inject constructor(
    private val context: Context
) {
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}