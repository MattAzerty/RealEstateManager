package fr.melanoxy.realestatemanager.data.repositories

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
//ENTRY DATE PICKER
private val entryDatePickedMutableStateFlow = MutableStateFlow<String?>(null)
    val entryDatePickedChannel = Channel<String>()

    fun setEntryDatePicked(entryDatePicked: String) {
        entryDatePickedMutableStateFlow.value = entryDatePicked
        entryDatePickedChannel.trySend(entryDatePicked)
    }
//SALE DATE PICKER
    private val saleDatePickedMutableStateFlow = MutableStateFlow<String?>(null)
    val saleDatePickedChannel = Channel<String>()

    fun setSaleDatePicked(saleDatePicked: String) {
        saleDatePickedMutableStateFlow.value = saleDatePicked
        saleDatePickedChannel.trySend(saleDatePicked)
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