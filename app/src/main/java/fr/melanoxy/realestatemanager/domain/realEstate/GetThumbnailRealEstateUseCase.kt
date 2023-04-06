package fr.melanoxy.realestatemanager.domain.realEstate

import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import fr.melanoxy.realestatemanager.data.repositories.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetThumbnailRealEstateUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    suspend fun invoke(latLng:LatLng): Bitmap? = try {
        locationRepository.getBitmapThumbnail(latLng)
    } catch (e: SQLiteException) {
        e.printStackTrace()
        null
    }
}