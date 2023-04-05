package fr.melanoxy.realestatemanager.data.repositories

import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
//https://developer.android.com/training/location/retrieve-current
@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {

    companion object {
        const val SMALLEST_DISPLACEMENT_THRESHOLD_METER: Float= 100F
        const val LOCATION_REQUEST_INTERVAL_MS = 10000
    }

    private var callback: LocationCallback? = null
    private val locationMutableStateFlow = MutableStateFlow<Location?>(null)


    fun getUserPosition(): StateFlow<Location?> {
        return locationMutableStateFlow.asStateFlow()
    }


    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    fun startLocationRequest() {
        if (callback == null) {
            callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    locationMutableStateFlow.value=location
                }
            }
        }
        fusedLocationProviderClient.removeLocationUpdates(callback!!)//TODO: https://developer.android.com/training/location/request-updates
        fusedLocationProviderClient.requestLocationUpdates( //when position change updates will be send
            LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
                .setInterval(LOCATION_REQUEST_INTERVAL_MS.toLong()),
            callback!!,
            Looper.getMainLooper()
        )
    }

    fun stopLocationRequest() {
        if (callback != null) {
            fusedLocationProviderClient.removeLocationUpdates(callback!!)
        }
    }




}