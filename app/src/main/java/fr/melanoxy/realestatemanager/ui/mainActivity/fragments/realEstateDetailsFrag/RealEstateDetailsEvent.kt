package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateDetailsEvent {
    data class AddChip(val tag: String) : RealEstateDetailsEvent()
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateDetailsEvent()
    data class CloseFragment(val layoutId: Int) : RealEstateDetailsEvent()
    object ShowSaleDatePicker : RealEstateDetailsEvent()
    object ShowMarketEntryDatePicker : RealEstateDetailsEvent()
    data class ShowSearchBarKeyboard(val inputType:Int) : RealEstateDetailsEvent()
    object ShowPOISelector : RealEstateDetailsEvent()
    object PopToBackStack : RealEstateDetailsEvent()
}