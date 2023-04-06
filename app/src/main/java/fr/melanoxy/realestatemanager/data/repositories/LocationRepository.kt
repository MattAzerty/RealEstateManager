package fr.melanoxy.realestatemanager.data.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import fr.melanoxy.realestatemanager.BuildConfig.MAPS_API_KEY
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.Address
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
//https://developer.android.com/training/location/retrieve-current
@Singleton
class LocationRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
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

    suspend fun getCoordinatesFromAddress(address: Address): LatLng? {
        val sAddress = "${address.city},${address.state},${address.street}"
        return withContext(coroutineDispatcherProvider.io) {
            val addresses = geocoder.getFromLocationName(sAddress, 1)//Blocking function
            if (addresses != null && addresses.isNotEmpty()) {
                val location = addresses[0]
                LatLng(location.latitude, location.longitude)
            }else null
        }
    }

    suspend fun getBitmapThumbnail(latLng: LatLng): Bitmap? {
        val zoom = 15
        val size = "400x400"
        val marker = "color:red|label:A|${latLng.latitude},${latLng.longitude}"
        val url = "https://maps.googleapis.com/maps/api/staticmap?center=${latLng.latitude},${latLng.longitude}&zoom=$zoom&size=$size&markers=$marker&key=$MAPS_API_KEY"

        return getBitmapFromUrl(url)
    }

    private suspend fun getBitmapFromUrl(url: String): Bitmap? = withContext(coroutineDispatcherProvider.io) {
        var bitmap: Bitmap? = null
        var inputStream: InputStream? = null
        var httpURLConnection: HttpURLConnection? = null
        try {
            val urlObject = URL(url)
            httpURLConnection = urlObject.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpURLConnection?.disconnect()
            inputStream?.close()
        }
        bitmap
    }

}