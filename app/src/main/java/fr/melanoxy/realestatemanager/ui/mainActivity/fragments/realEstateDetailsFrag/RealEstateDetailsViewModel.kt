package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.searchBar.GetCurrentFilterUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.GetFilterListTagUseCase
import fr.melanoxy.realestatemanager.domain.searchBar.SetCurrentFilterListUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarStateItem
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RealEstateDetailsViewModel @Inject constructor(
    getFilterListTagUseCase: GetFilterListTagUseCase,
    getCurrentFilterUseCase: GetCurrentFilterUseCase,
    private val setCurrentFilterListUseCase: SetCurrentFilterListUseCase,
) : ViewModel() {

    val singleLiveRealEstateDetailsEvent = SingleLiveEvent<RealEstateDetailsEvent>()
    val filterListLiveData: LiveData<List<RealEstateSearchBarStateItem>> =
        getFilterListTagUseCase.invoke().combine(getCurrentFilterUseCase.invoke()) { tagList, currentFilterTagList ->
            tagList.mapIndexed { index, item ->
                RealEstateSearchBarStateItem(
                    id= index.toLong(),
                    criteriaType = item.split(":")[1],
                    tag = "${item.split(":")[0]}: "
                )
            }.filter { list ->
                currentFilterTagList.none { searchString ->
                    list.tag.contains("${searchString.split(":")[0]}:")
                }}
    }.asLiveData()


    fun onAddChipCriteria(criteria: String) {
        if(criteria.isEmpty()){
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.DisplaySnackBarMessage(
                NativeText.Resource(
                R.string.error_search_empty))
        }else{
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.AddChip(criteria)
        }

    }

    fun onTagSelected(tag: String) {
        when (tag.trim()){
            "[SD>]:" -> singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.ShowSaleDatePicker
            "[SD<]:" -> singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.ShowSaleDatePicker
            "[MED>]:" -> singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.ShowMarketEntryDatePicker
            "[MED<]:" -> singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.ShowMarketEntryDatePicker
        }
    }

    fun onChipGroupUpdate(tagList: MutableList<String>) {
        setCurrentFilterListUseCase.invoke(tagList)
    }
}