package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.repositories.LocationRepository
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.domain.realEstate.GetAllRealEstateUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateMapViewModel @Inject constructor(
    locationRepository: LocationRepository,
    private val sharedRepository: SharedRepository,
    private val realEstateRepository: RealEstateRepository,
    getAllRealEstateUseCase: GetAllRealEstateUseCase
) : ViewModel() {

    val singleLiveRealEstateMapEvent = SingleLiveEvent<RealEstateMapEvent>()

    fun onFabButtonClicked(fabId: Int) {
        when(fabId) {
            R.id.fragment_real_estate_map_fab_position -> singleLiveRealEstateMapEvent.value = RealEstateMapEvent.CenterCameraOnUserPosition
            R.id.fragment_real_estate_map_fab_close -> {
                if(sharedRepository.isTabletStateFlow.value){
                    sharedRepository.fragmentStateFlow.value = NavigationEvent.RealEstateListFragment
                    singleLiveRealEstateMapEvent.value = RealEstateMapEvent.CloseSecondPaneFragment
                }else singleLiveRealEstateMapEvent.value = RealEstateMapEvent.CloseFragment
            }
        }
    }

    fun notifyFragmentNav() {
        sharedRepository.fragmentStateFlow.value = NavigationEvent.RealEstateMapFragment
    }

    fun onInfoWindowClicked(realEstateId: Long) {
        realEstateRepository.setSelectedRealEstateId(realEstateId)
        if(sharedRepository.isTabletStateFlow.value && sharedRepository.fragmentStateFlow.value == NavigationEvent.RealEstateMapFragment){
        sharedRepository.fragmentStateFlow.value = NavigationEvent.RealEstateDetailsFragment
        singleLiveRealEstateMapEvent.value = RealEstateMapEvent.OpenDetailsFragment
        }else{
            sharedRepository.fragmentStateFlow.value = NavigationEvent.RealEstateDetailsFragment
            singleLiveRealEstateMapEvent.value = RealEstateMapEvent.OpenDetailsFragment
        }
    }

    val userPositionLiveData: LiveData<Location?> = locationRepository.getUserPosition().asLiveData()
    val realEstatesPositionsLiveData = getAllRealEstateUseCase.invoke().asLiveData().map { list ->
        list.map {
            RealEstateMarkerStateItem(
                id= it.id,
                realEstateName=it.propertyType,
                realEstatePrice="${it.price}$",
                coordinates=  LatLng(it.coordinates.split(",")[0].toDouble(), it.coordinates.split(",")[1].toDouble()),
                isSold= it.saleDate!=null,
            )
        }
    }

}
