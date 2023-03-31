package fr.melanoxy.realestatemanager.ui.mainActivity

sealed class NavigationEvent {
    object RealEstateListFragment : NavigationEvent()
    object AddOrEditRealEstateFragment : NavigationEvent()
}