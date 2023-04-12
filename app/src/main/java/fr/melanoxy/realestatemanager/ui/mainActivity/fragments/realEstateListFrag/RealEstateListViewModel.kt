package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesUseCase
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv.RealEstateViewStateItem
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateListViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
    private val sharedRepository: SharedRepository,
    private val permissionChecker: PermissionChecker,
    getRealEstateWithPicturesUseCase: GetRealEstateWithPicturesUseCase,
) : ViewModel() {

    private val realEstateWithPicturesListLiveData = getRealEstateWithPicturesUseCase.invoke().asLiveData()
    private val selectedEstateIdLiveData = realEstateRepository.selectedRealEstateIdStateFlow.asLiveData()

    private val mediatorLiveData = MediatorLiveData<List<RealEstateViewStateItem>>()
    init {
        mediatorLiveData.addSource(realEstateWithPicturesListLiveData) { realEstateWithPicturesList -> combine(realEstateWithPicturesList, selectedEstateIdLiveData.value)}
        mediatorLiveData.addSource(selectedEstateIdLiveData) { selectedEstateId -> combine(realEstateWithPicturesListLiveData.value, selectedEstateId)}
    }

    private fun combine(realEstateWithPicturesList: List<RealEstateWithPictureEntity>?, selectedEstateId: Long?) {

        var listOfRealEstateViewStateItem:List<RealEstateViewStateItem> = emptyList()

        if(!realEstateWithPicturesList.isNullOrEmpty()) {
            listOfRealEstateViewStateItem = realEstateWithPicturesList.map {
                RealEstateViewStateItem(
                    realEstateId= it.realEstateEntity.id,
                    pictureUri= Uri.parse("file://${it.estatePictureEntities[0].path}"),
                    realEstateType=it.realEstateEntity.propertyType,
                    realEstateCity=it.realEstateEntity.address.city,
                    realEstatePrice="$${it.realEstateEntity.price}",
                    isSelected = it.realEstateEntity.id == selectedEstateId,
                    onRealEstateClicked= {
                        realEstateRepository.setSelectedRealEstateId(it.realEstateEntity.id)
                        onItemClicked()
                    },
                    onRealEstateLongClick = {
                    realEstateRepository.setSelectedRealEstateId(it.realEstateEntity.id)
                    //same fragment and same behavior phone/tablet
                    singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(R.layout.fragment_real_estate_add)
                },
                )
            }
        }


        mediatorLiveData.value = listOfRealEstateViewStateItem
    }

    private fun onItemClicked() {
        if(!sharedRepository.isTabletStateFlow.value)
            singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(R.layout.fragment_real_estate_details)
    }

    val realEstateListLiveData: LiveData<List<RealEstateViewStateItem>>
        get() = mediatorLiveData


    val isTabletLiveData = sharedRepository.isTabletStateFlow.asLiveData()
    val singleLiveRealEstateListEvent = SingleLiveEvent<RealEstateListEvent>()

    fun onFabButtonClicked(layoutId: Int) {
        when(layoutId) {
            R.layout.fragment_real_estate_add -> singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(layoutId)
            R.layout.fragment_real_estate_map -> {
                if(permissionChecker.hasLocationPermission()){ showMapFragment(layoutId)
                }else singleLiveRealEstateListEvent.value = RealEstateListEvent.RequestLocationPermission
            }
        }
    }

    private fun showMapFragment(layoutId: Int) {
        if(sharedRepository.isTabletStateFlow.value) singleLiveRealEstateListEvent.value= RealEstateListEvent.ReplaceSecondPaneFragment(layoutId)
        else singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(layoutId)
    }

    fun notifyFragmentNav() {
        sharedRepository.fragmentStateFlow.value = NavigationEvent.RealEstateListFragment
    }

    fun onLocationPermissionResult(permission: Boolean?) {
        if(permission == true) showMapFragment(R.layout.fragment_real_estate_map)
        else singleLiveRealEstateListEvent.value = RealEstateListEvent.DisplaySnackBarMessage(
            NativeText.Resource(
            R.string.error_location_permission))
    }

}