package fr.melanoxy.realestatemanager.domain.realEstate

import android.database.sqlite.SQLiteException
import com.google.android.gms.maps.model.LatLng
import fr.melanoxy.realestatemanager.data.repositories.LocationRepository
import fr.melanoxy.realestatemanager.domain.Address
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCoordinateRealEstateUseCase @Inject constructor(private val locationRepository: LocationRepository) {

    suspend fun invoke(address: Address): LatLng? = try {
        locationRepository.getCoordinatesFromAddress(address)
    } catch (e: SQLiteException) {
        e.printStackTrace()
        null
    }
}

