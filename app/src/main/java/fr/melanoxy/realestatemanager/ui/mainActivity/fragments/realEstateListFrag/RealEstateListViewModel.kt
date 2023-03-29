package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SearchRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv.RealEstateViewStateItem
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateListViewModel @Inject constructor(
    realEstateRepository: RealEstateRepository,
    searchRepository: SearchRepository,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    getRealEstateWithPicturesUseCase: GetRealEstateWithPicturesUseCase,
) : ViewModel() {

    val realEstatesLiveData: LiveData<List<RealEstateViewStateItem>> = liveData(coroutineDispatcherProvider.io){

       getRealEstateWithPicturesUseCase.invoke().collect { realEstateWithPicture ->
                emit(
                    realEstateWithPicture.map {
                        RealEstateViewStateItem(
                        realEstateId= it.realEstateEntity.id,
                        pictureUri= Uri.parse("file://${it.estatePictureEntities[0].path}"),
                        realEstateType=it.realEstateEntity.propertyType,
                        realEstateCity=it.realEstateEntity.address.city,
                        realEstatePrice="$${it.realEstateEntity.price}",
                        onRealEstateClicked={realEstateRepository.selectedRealEstateIdMutableStateFlow.value=it.realEstateEntity.id}
                        )
                    }
                )
            }
    }

    val isTabletLiveData = searchRepository.isTabletStateFlow.asLiveData()
    val singleLiveRealEstateListEvent = SingleLiveEvent<RealEstateListEvent>()

    fun onFabButtonClicked(fabButtonId: Int) {
        singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(fabButtonId)
        }

    }