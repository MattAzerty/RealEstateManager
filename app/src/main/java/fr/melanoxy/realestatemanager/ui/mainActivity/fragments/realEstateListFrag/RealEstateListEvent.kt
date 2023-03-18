package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

sealed class RealEstateListEvent {
    data class ReplaceCurrentFragment(val fragmentId: Int) : RealEstateListEvent()
}