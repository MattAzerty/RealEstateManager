package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import javax.inject.Inject

@HiltViewModel
class ViewPagerInfosViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
) : ViewModel() {

//ADDRESS FIELDS FROM FragmentAddressFields
    fun onStreetFieldChanged(street: String) {
        realEstateRepository.onStreetFieldChanged(street)
    }

    fun onCityFieldChanged(city: String) {
        realEstateRepository.onCityFieldChanged(city)
    }

    fun onStateChanged(state: String) {
        realEstateRepository.onStateChanged(state)
    }

    fun onZipcode(zipcode: String) {
        realEstateRepository.onZipcodeChanged(zipcode)
    }

    //SPECIF FIELDS FROM FragmentInfosFields
    fun onPriceFieldChanged(price: String) {
        realEstateRepository.onPriceFieldChanged(price)
    }

    fun onNumberOfRoomChanged(numberOfRoom: String) {
        realEstateRepository.onNumberOfRoomChanged(numberOfRoom)
    }

    fun onNumberOfBedroomChanged(numberOfBedroom: String) {
        realEstateRepository.onNumberOfBedroomChanged(numberOfBedroom)
    }

    fun onSurfaceFieldChanged(surface: String) {
        realEstateRepository.onSurfaceFieldChanged(surface)
    }

}