package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import android.net.Uri
import android.text.InputType
import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesFilteredUseCase
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesUseCase
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import fr.melanoxy.realestatemanager.domain.searchBar.GetCurrentFilterListUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.SetCurrentFilterListUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag.RealEstateDetailsEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv.RealEstateViewStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarStateItem
import fr.melanoxy.realestatemanager.ui.utils.Event
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import fr.melanoxy.realestatemanager.ui.utils.asLiveDataEvent
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RealEstateListViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
    private val sharedRepository: SharedRepository,
    private val permissionChecker: PermissionChecker,
    private val setCurrentFilterListUseCase: SetCurrentFilterListUseCase,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    getCurrentFilterListUseCase: GetCurrentFilterListUseCase,
    getRealEstateWithPicturesFilteredUseCase:GetRealEstateWithPicturesFilteredUseCase,
) : ViewModel() {

    val filterListLiveData: LiveData<List<RealEstateSearchBarStateItem>> =
        getCurrentFilterListUseCase.invoke().asLiveData()

    private val selectedEstateIdLiveData = realEstateRepository.selectedRealEstateIdStateFlow.asLiveData()
    private val filteredRealEstateListLiveData = getRealEstateWithPicturesFilteredUseCase.invoke().asLiveData()


    private val mediatorLiveData = MediatorLiveData<List<RealEstateViewStateItem>>()
    init {
        //mediatorLiveData.addSource(realEstateWithPicturesListLiveData) { realEstateWithPicturesList -> combine(realEstateWithPicturesList, selectedEstateIdLiveData.value, filteredRealEstateListLiveData.value)}
        mediatorLiveData.addSource(selectedEstateIdLiveData) { selectedEstateId -> combine(selectedEstateId, filteredRealEstateListLiveData.value)}
        mediatorLiveData.addSource(filteredRealEstateListLiveData) { filteredRealEstateList -> combine(selectedEstateIdLiveData.value, filteredRealEstateList)}
    }

    private fun combine(selectedEstateId: Long?, filteredRealEstateList: List<RealEstateWithPictureEntity>?) {

        var listOfRealEstateViewStateItem:List<RealEstateViewStateItem> = emptyList()

        if(!filteredRealEstateList.isNullOrEmpty()) {
            listOfRealEstateViewStateItem = filteredRealEstateList.map {
                RealEstateViewStateItem(
                    realEstateId= it.realEstateEntity.id,
                    pictureUri= Uri.parse("file://${it.estatePictureEntities[0].path}"),
                    realEstateType="- ${it.realEstateEntity.propertyType} -",
                    realEstateCity=it.realEstateEntity.address.city,
                    realEstatePrice= NumberFormat.getCurrencyInstance(Locale.US).format(it.realEstateEntity.price),
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
            R.layout.fragment_real_estate_add -> {
                realEstateRepository.setSelectedRealEstateId(null)
                singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(layoutId)
            }
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

    fun onTagSelected(tag: String) {
        when (tag.trim()) {
            "[SD>]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSaleDatePicker
            "[SD<]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSaleDatePicker
            "[MED>]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowMarketEntryDatePicker
            "[MED<]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowMarketEntryDatePicker
            "[POI]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowPOISelector
            "[#P]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER)
            "[$>]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            "[$<]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            "[#R]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER)
            "[#B]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER )
            "[S>]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            "[S<]:" -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            else -> singleLiveRealEstateListEvent.value =
                RealEstateListEvent.ShowSearchBarKeyboard(InputType.TYPE_CLASS_TEXT)
        }
    }

    fun onAddChipCriteria(criteria: String) {
        var success = false

        if (criteria.isNotBlank()){
        success = when(criteria.split(":")[0]){
            "[A]" -> criteria.split(":")[1].trimStart().split(" ").size >1
            else -> criteria.split(":")[1].isNotEmpty() && criteria.split(":")[1].isNotBlank()
        }}

        if (success) {
            singleLiveRealEstateListEvent.value = RealEstateListEvent.AddChip(criteria)
        } else {
            singleLiveRealEstateListEvent.value = RealEstateListEvent.DisplaySnackBarMessage(
                NativeText.Resource(
                    R.string.error_search_empty
                )
            )
        }

    }

    fun onChipGroupUpdate(tagList: MutableList<String>) {
        setCurrentFilterListUseCase.invoke(tagList)
    }

    fun getCurrentChips(): List<String> {
        return sharedRepository.currentFilterTagListMutableStateFlow.value
    }

    fun onCollapseClicked() {
        sharedRepository.setCurrentFilterList(emptyList())
    }

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

}