package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarStateItem
import fr.melanoxy.realestatemanager.ui.utils.FILTERING_CRITERIA_LIST
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateDetailsViewModel @Inject constructor(
    //private val mailRepository: MailRepository
) : ViewModel() {

    val singleLiveRealEstateDetailsEvent = SingleLiveEvent<RealEstateDetailsEvent>()


    fun getTags(): List<RealEstateSearchBarStateItem> {
        return FILTERING_CRITERIA_LIST.mapIndexed { index, item ->
            RealEstateSearchBarStateItem(
                id= index.toLong(),
                criteriaType = item.split(":")[1],
                tag = "${item.split(":")[0]}: "
            )
        }
    }

    fun onAddChipCriteria(criteria: String) {
        if(criteria.isNullOrEmpty()){
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.DisplaySnackBarMessage(
                NativeText.Resource(
                R.string.error_search_empty))
        }else{
            singleLiveRealEstateDetailsEvent.value = RealEstateDetailsEvent.AddChip(criteria)
        }

    }

    fun onTagSelected(tagId: Long) {

    }

    fun onChipSelected(tagList: MutableList<String>) {

    }
}