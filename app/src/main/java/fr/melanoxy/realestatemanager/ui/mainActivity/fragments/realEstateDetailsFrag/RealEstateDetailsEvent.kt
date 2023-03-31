package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateDetailsEvent {
    data class AddChip(val tag: String) : RealEstateDetailsEvent()
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateDetailsEvent()
    object ShowSaleDatePicker : RealEstateDetailsEvent()
    object ShowMarketEntryDatePicker : RealEstateDetailsEvent()
    object ShowSearchBarKeyboard : RealEstateDetailsEvent()
    object ShowPOISelector : RealEstateDetailsEvent()
}