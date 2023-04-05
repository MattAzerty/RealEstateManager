package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateListEvent {
    data class ReplaceCurrentFragment(val fragmentId: Int) : RealEstateListEvent()
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateListEvent()
    object ReplaceSecondPaneFragment : RealEstateListEvent()
    object RequestLocationPermission : RealEstateListEvent()
}