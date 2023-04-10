package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.realEstate.GetRealEstateFromIdUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewPagerInfosViewModel @Inject constructor(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val realEstateRepository: RealEstateRepository,
    private val getRealEstateFromIdUseCase: GetRealEstateFromIdUseCase,
) : ViewModel() {


    val realEstateViewPagerInfosStateItemLiveData = MutableLiveData(RealEstateViewPagerInfosStateItem())
    init {
        val id = realEstateRepository.selectedRealEstateIdStateFlow.value
        if(id!=null){
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getRealEstateFromIdUseCase.invoke(id).collect { entity ->
                withContext(coroutineDispatcherProvider.main) {
                realEstateViewPagerInfosStateItemLiveData.value =
                    RealEstateViewPagerInfosStateItem(
                street = entity.address.street,
                city = entity.address.city,
                state = entity.address.state,
                zipcode = entity.address.zipCode,
                price = entity.price,
                numberOfRooms = entity.numberOfRooms,
                numberOfBedrooms = entity.numberOfBedrooms,
                surfaceArea = entity.surfaceArea
                    )}
            }
        }

        }
    }

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