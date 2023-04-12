package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListEvent

sealed class RealEstateMapEvent {
    object CenterCameraOnUserPosition : RealEstateMapEvent()
    object CloseFragment : RealEstateMapEvent()
    object CloseSecondPaneFragment : RealEstateMapEvent()
    object OpenDetailsFragment : RealEstateMapEvent()
}