package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag.RealEstateDetailsEvent
import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateListEvent {
    data class AddChip(val tag: String) : RealEstateListEvent()
    data class ReplaceCurrentFragment(val layoutId: Int) : RealEstateListEvent()
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateListEvent()
    data class ReplaceSecondPaneFragment(val layoutId: Int) : RealEstateListEvent()
    object RequestLocationPermission : RealEstateListEvent()
    object ShowSaleDatePicker : RealEstateListEvent()
    object ShowMarketEntryDatePicker : RealEstateListEvent()
    object ShowSearchBarKeyboard : RealEstateListEvent()
    object ShowPOISelector : RealEstateListEvent()
}