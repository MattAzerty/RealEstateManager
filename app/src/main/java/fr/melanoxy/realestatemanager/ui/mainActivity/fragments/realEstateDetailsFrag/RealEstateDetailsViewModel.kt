package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesFromIdUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.GetCurrentFilterUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.GetFilterListTagUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.SetCurrentFilterListUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarStateItem
import fr.melanoxy.realestatemanager.ui.utils.*
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RealEstateDetailsViewModel @Inject constructor(
    private val sharedRepository: SharedRepository,
    private val realEstateRepository: RealEstateRepository,
    getFilterListTagUseCase: GetFilterListTagUseCase,
    getCurrentFilterUseCase: GetCurrentFilterUseCase,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val setCurrentFilterListUseCase: SetCurrentFilterListUseCase,
    private val getRealEstateWithPicturesFromIdUseCase: GetRealEstateWithPicturesFromIdUseCase,
) : ViewModel() {

    private val selectedEstateIdLiveData =
        realEstateRepository.selectedRealEstateIdStateFlow.asLiveData()

    val detailsOfRealEstateStateItemLiveData: LiveData<RealEstateDetailsViewState> =
        selectedEstateIdLiveData.switchMap { selectedId ->

            liveData {
                if (selectedId != null) {
                    getRealEstateWithPicturesFromIdUseCase.invoke(selectedId).collect { entity ->
                        emit(
                            RealEstateDetailsViewState(
                                type = entity.realEstateEntity.propertyType,
                                city = entity.realEstateEntity.address.city,
                                price = "$${entity.realEstateEntity.price}",
                                thumbnail = entity.realEstateEntity.thumbnail,
                                pictureList = entity.estatePictureEntities.map {
                                    RealEstatePictureViewStateItem(
                                        realEstateId = selectedId,
                                        pictureUri = Uri.parse("file://${it.path}"),
                                        realEstatePictureName = it.name,
                                        isStored = false,
                                        isEdited = false,
                                        toDelete = false,
                                    )
                                },
                                description = entity.realEstateEntity.description,
                                agentName = "${ESTATE_AGENTS[entity.realEstateEntity.estateAgentId.toInt()-1].firstName} ${ESTATE_AGENTS[entity.realEstateEntity.estateAgentId.toInt()-1].lastName}",
                                surface = "${entity.realEstateEntity.surfaceArea}m²",
                                room = entity.realEstateEntity.numberOfRooms.toString(),
                                bedroom = entity.realEstateEntity.numberOfBedrooms.toString(),
                                nearPOI = buildPOIList(entity.realEstateEntity.pointsOfInterest),
                                locationCoordinate = adaptCoordinates(entity.realEstateEntity.coordinates)
                            )
                        )
                    }
                }
            }
        }

    private fun adaptCoordinates(coordinates: String): String {
        val lat = String.format("%.5f", coordinates.split(",")[0].toDouble())
        val long = String.format("%.5f", coordinates.split(",")[1].toDouble())
        return "$lat $long"
    }

    private fun buildPOIList(pointsOfInterest: ArrayList<String>): String {
        return pointsOfInterest.joinToString(separator = "|")
    }


    val isTabletLiveData = sharedRepository.isTabletStateFlow.asLiveData()
    val fragmentNavigationLiveData = sharedRepository.fragmentStateFlow.asLiveData()
    val singleLiveRealEstateDetailsEvent = SingleLiveEvent<RealEstateDetailsEvent>()

    val entryDatePickedLiveData: LiveData<Event<String>> =
        sharedRepository.entryDatePickedChannelFromSearchBar.asLiveDataEvent(
            coroutineDispatcherProvider.io
        ) {
            emit(it)
        }

    val saleDatePickedLiveData: LiveData<Event<String>> =
        sharedRepository.saleDatePickedChannelFromSearchBar.asLiveDataEvent(
            coroutineDispatcherProvider.io
        ) {
            emit(it)
        }

    val filterListLiveData: LiveData<List<RealEstateSearchBarStateItem>> =
        getFilterListTagUseCase.invoke()
            .combine(getCurrentFilterUseCase.invoke()) { tagList, currentFilterTagList ->
                tagList.mapIndexed { index, item ->
                    RealEstateSearchBarStateItem(
                        id = index.toLong(),
                        criteriaType = item.split(":")[1],
                        tag = "${item.split(":")[0]}: "
                    )
                }.filter { list ->
                    currentFilterTagList.none { searchString ->
                        list.tag.contains("${searchString.split(":")[0]}:")
                    }
                }
            }.asLiveData()


    fun onAddChipCriteria(criteria: String) {
        if (criteria.isEmpty()) {
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.DisplaySnackBarMessage(
                NativeText.Resource(
                    R.string.error_search_empty
                )
            )
        } else {
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.AddChip(criteria)
        }
    }

    fun onTagSelected(tag: String) {
        when (tag.trim()) {
            "[SD>]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSaleDatePicker
            "[SD<]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSaleDatePicker
            "[MED>]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowMarketEntryDatePicker
            "[MED<]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowMarketEntryDatePicker
            "[POI]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowPOISelector
            else -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard
        }
    }

    fun onChipGroupUpdate(tagList: MutableList<String>) {
        setCurrentFilterListUseCase.invoke(tagList)
    }

    fun onCloseFragmentClicked() {
        realEstateRepository.setSelectedRealEstateId(null)
        if (!sharedRepository.isTabletStateFlow.value && sharedRepository.fragmentStateFlow.value==NavigationEvent.RealEstateDetailsFragment){
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.PopToBackStack
        }else{singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.CloseFragment(R.layout.fragment_real_estate_list)}
    }

}