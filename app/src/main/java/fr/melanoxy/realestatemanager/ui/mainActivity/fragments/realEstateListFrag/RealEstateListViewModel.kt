package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesUseCase
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv.RealEstateViewStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateListViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
    private val sharedRepository: SharedRepository,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    getRealEstateWithPicturesUseCase: GetRealEstateWithPicturesUseCase,
) : ViewModel() {

    //TODO scope?
    /*val realEstatesLiveData: LiveData<List<RealEstateViewStateItem>> = liveData(coroutineDispatcherProvider.io){

       getRealEstateWithPicturesUseCase.invoke().collect { realEstateWithPicture ->
                emit(
                    realEstateWithPicture.map {
                        RealEstateViewStateItem(
                        realEstateId= it.realEstateEntity.id,
                        pictureUri= Uri.parse("file://${it.estatePictureEntities[0].path}"),
                        realEstateType=it.realEstateEntity.propertyType,
                        realEstateCity=it.realEstateEntity.address.city,
                        realEstatePrice="$${it.realEstateEntity.price}",
                        isSelected = false,
                        onRealEstateClicked={
                            realEstateRepository.selectedRealEstateIdMutableStateFlow.value=it.realEstateEntity.id
                        }
                        )
                    }
                )
            }
    }*/

    private val realEstateWithPicturesListLiveData = getRealEstateWithPicturesUseCase.invoke().asLiveData()
    private val selectedEstateIdLiveData = realEstateRepository.selectedRealEstateIdMutableStateFlow.asLiveData()

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
                    onRealEstateClicked={
                        realEstateRepository.selectedRealEstateIdMutableStateFlow.value=it.realEstateEntity.id
                    }
                )
            }
        }


        mediatorLiveData.value = listOfRealEstateViewStateItem
    }

    val realEstateListLiveData: LiveData<List<RealEstateViewStateItem>>
        get() = mediatorLiveData


    val isTabletLiveData = sharedRepository.isTabletStateFlow.asLiveData()
    val singleLiveRealEstateListEvent = SingleLiveEvent<RealEstateListEvent>()

    fun onFabButtonClicked(fabButtonId: Int) {
        singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(fabButtonId)
        }

    fun notifyFragmentNav() {
        sharedRepository.fragmentStateFlow.value = NavigationEvent.RealEstateListFragment
    }

}