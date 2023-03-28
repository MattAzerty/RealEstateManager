package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.RealEstateRepository
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateListViewModel @Inject constructor(
    realEstateRepository: RealEstateRepository,
) : ViewModel() {

    val isTabletLiveData = realEstateRepository.isTabletStateFlow.asLiveData()
    val singleLiveRealEstateListEvent = SingleLiveEvent<RealEstateListEvent>()

    fun onFabButtonClicked(fabButtonId: Int) {
        singleLiveRealEstateListEvent.value = RealEstateListEvent.ReplaceCurrentFragment(fabButtonId)
        }

    }