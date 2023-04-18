package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

sealed class RealEstateMapEvent {
    object CenterCameraOnUserPosition : RealEstateMapEvent()
    object CloseFragment : RealEstateMapEvent()
    object CloseSecondPaneFragment : RealEstateMapEvent()
    object OpenDetailsFragment : RealEstateMapEvent()
}