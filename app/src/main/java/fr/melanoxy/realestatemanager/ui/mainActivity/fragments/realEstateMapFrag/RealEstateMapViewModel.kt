package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.data.repositories.LocationRepository
import javax.inject.Inject

@HiltViewModel
class RealEstateMapViewModel @Inject constructor(
    locationRepository: LocationRepository,
) : ViewModel() {
    val userPositionLiveData: LiveData<Location?> = locationRepository.getUserPosition().asLiveData()
}