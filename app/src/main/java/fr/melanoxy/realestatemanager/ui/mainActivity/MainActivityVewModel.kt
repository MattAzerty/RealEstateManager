package fr.melanoxy.realestatemanager.ui.mainActivity

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.repositories.LocationRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.PermissionChecker
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sharedRepository: SharedRepository,
    private val locationRepository: LocationRepository,
    private val permissionChecker: PermissionChecker,
) : ViewModel() {

    private var isTablet: Boolean = false


    @SuppressLint("MissingPermission")
    fun onResume(isTablet: Boolean) {
        //Check isTabletMode
        sharedRepository.isTablet(isTablet)
        this.isTablet = isTablet
        //StartLocation on permission granted
        if (permissionChecker.hasLocationPermission()) {
            locationRepository.startLocationRequest()
        } else {
            locationRepository.stopLocationRequest()
        }
    }

    fun onDateSelected(type: Int, formattedDate: String) {
        when(type){
            R.string.entryDate -> sharedRepository.setEntryDatePicked(formattedDate)
            R.string.saleDate -> sharedRepository.setSaleDatePicked(formattedDate)
        }
    }
}