package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import android.net.Uri
import android.text.InputType
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesFromIdUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.GetCurrentFilterListUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.SetCurrentFilterListUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarStateItem
import fr.melanoxy.realestatemanager.ui.utils.*
import javax.inject.Inject

@HiltViewModel
class RealEstateDetailsViewModel @Inject constructor(
    private val sharedRepository: SharedRepository,
    private val realEstateRepository: RealEstateRepository,
    getCurrentFilterListUseCase: GetCurrentFilterListUseCase,
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
                                id= selectedId,
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

    private fun buildPOIList(pointsOfInterest: ArrayList<String>?): String {
        return pointsOfInterest?.joinToString(separator = "|") ?: "none"
    }


    val isTabletLiveData = sharedRepository.isTabletStateFlow.asLiveData()
    val fragmentNavigationLiveData = sharedRepository.fragmentStateFlow.asLiveData()
    val singleLiveRealEstateDetailsEvent = SingleLiveEvent<RealEstateDetailsEvent>()

    val entryDatePickedLiveData: LiveData<Event<String>> =
        sharedRepository.entryDatePickedChannelFromSearchBarTablet.asLiveDataEvent(
            coroutineDispatcherProvider.io
        ) {
            emit(it)
        }

    val saleDatePickedLiveData: LiveData<Event<String>> =
        sharedRepository.saleDatePickedChannelFromSearchBarTablet.asLiveDataEvent(
            coroutineDispatcherProvider.io
        ) {
            emit(it)
        }

    val filterListLiveData: LiveData<List<RealEstateSearchBarStateItem>> =
        getCurrentFilterListUseCase.invoke().asLiveData()


    fun onAddChipCriteria(criteria: String) {
        var success = false

        if (criteria.isNotBlank()){
            success = when(criteria.split(":")[0]){
                "[A]" -> criteria.split(":")[1].trimStart().split(" ").size >1
                else -> criteria.split(":")[1].isNotEmpty() && criteria.split(":")[1].isNotBlank()
            }}

        if (success) {
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.AddChip(criteria)
        } else {
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.DisplaySnackBarMessage(
                NativeText.Resource(
                    R.string.error_search_empty
                )
            )
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
            "[#P]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER)
            "[$>]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            "[$<]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            "[#R]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER)
            "[#B]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER )
            "[S>]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            "[S<]:" -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            else -> singleLiveRealEstateDetailsEvent.value =
                RealEstateDetailsEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_TEXT)
        }
    }

    fun onChipGroupUpdate(tagList: MutableList<String>) {
        setCurrentFilterListUseCase.invoke(tagList)
    }

    fun onCloseFragmentClicked() {
        realEstateRepository.setSelectedRealEstateId(null)
        singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.PopToBackStack
    }

    fun onLocateRealEstateClicked() {
        singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.CloseFragment(R.layout.fragment_real_estate_map)
    }

    fun onLoanButtonClicked() {
        realEstateRepository.setSelectedRealEstateId(detailsOfRealEstateStateItemLiveData.value?.id)
        singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.CloseFragment(R.layout.fragment_real_estate_loan)
    }

    fun getCurrentChips(): List<String> {
        return sharedRepository.currentFilterTagListMutableStateFlow.value
    }

}