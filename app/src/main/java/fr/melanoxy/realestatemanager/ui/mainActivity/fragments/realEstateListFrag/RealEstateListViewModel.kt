package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateListViewModel @Inject constructor(
    //mailRepository: MailRepository,
) : ViewModel() {

    val singleLiveRealEstateListEvent = SingleLiveEvent<RealEstateListEvent>()

    fun onFabButtonClicked(fabButtonId: Int) {
        singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(fabButtonId)
        }

    }