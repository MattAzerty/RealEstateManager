package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.repositories.LocationRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListEvent
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateMapViewModel @Inject constructor(
    locationRepository: LocationRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    val singleLiveRealEstateMapEvent = SingleLiveEvent<RealEstateMapEvent>()

    fun onFabButtonClicked(fabId: Int) {
        when(fabId) {
            R.id.fragment_real_estate_map_fab_position -> singleLiveRealEstateMapEvent.value = RealEstateMapEvent.CenterCameraOnUserPosition
            R.id.fragment_real_estate_map_fab_close -> {
                if(sharedRepository.isTabletStateFlow.value){ singleLiveRealEstateMapEvent.value = RealEstateMapEvent.CloseSecondPaneFragment
                }else singleLiveRealEstateMapEvent.value = RealEstateMapEvent.CloseFragment
            }
        }
    }

    fun notifyFragmentNav() {
        sharedRepository.fragmentStateFlow.value = NavigationEvent.RealEstateMapFragment
    }

    val userPositionLiveData: LiveData<Location?> = locationRepository.getUserPosition().asLiveData()
}