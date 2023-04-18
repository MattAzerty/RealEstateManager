package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

sealed class RealEstateMapEvent {
    object CenterCameraOnUserPosition : RealEstateMapEvent()
    object CloseFragment : RealEstateMapEvent()
    object RemoveFragment : RealEstateMapEvent()
    object CloseSecondPaneFragment : RealEstateMapEvent()
    object OpenDetailsFragment : RealEstateMapEvent()
}