package fr.melanoxy.realestatemanager.data.repositories

import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.utils.FILTERING_CRITERIA_LIST
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedRepository @Inject constructor(
) {
//FRAGMENT STATE
val fragmentStateFlow= MutableStateFlow<NavigationEvent>(NavigationEvent.RealEstateListFragment)

//ENTRY DATE PICKER

    val entryDatePickedChannelFromAddOrEdit = Channel<String>()
    val entryDatePickedChannelFromSearchBar = Channel<String>()

    fun setEntryDatePicked(entryDatePicked: String) {
        when (fragmentStateFlow.value) {
        NavigationEvent.AddOrEditRealEstateFragment -> entryDatePickedChannelFromAddOrEdit.trySend(entryDatePicked)
        NavigationEvent.RealEstateListFragment -> entryDatePickedChannelFromSearchBar.trySend(entryDatePicked)
        else -> {}
        }
    }

    //SALE DATE PICKER
    val saleDatePickedChannelFromAddOrEdit = Channel<String>()
    val saleDatePickedChannelFromSearchBar = Channel<String>()

    fun setSaleDatePicked(saleDatePicked: String) {
        when (fragmentStateFlow.value) {
            NavigationEvent.AddOrEditRealEstateFragment -> saleDatePickedChannelFromAddOrEdit.trySend(
                saleDatePicked
            )
            NavigationEvent.RealEstateListFragment -> saleDatePickedChannelFromSearchBar.trySend(
                saleDatePicked
            )
            else -> {}
        }
    }
//TABLET MODE DATA
    val isTabletStateFlow = MutableStateFlow(false)

    fun isTablet(isTablet: Boolean) {
        isTabletStateFlow.value = isTablet
    }

//SEARCH DATA
    private val tagFilterListStateFlow = MutableStateFlow(FILTERING_CRITERIA_LIST)
    private val currentFilterTagListStateFlow:MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    fun getFilterListTag(): Flow<List<String>> {
        return  tagFilterListStateFlow.asStateFlow()
    }
    fun getCurrentFilterTagList(): Flow<List<String>> {
        return  currentFilterTagListStateFlow.asStateFlow()
    }

    fun setCurrentFilterList(filterList: List<String>) {
        currentFilterTagListStateFlow.value = filterList
    }


}