package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag

import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateAddEvent {
    object CloseFragment : RealEstateAddEvent()
    object RequestCameraPermission : RealEstateAddEvent()
    object LaunchActivityPhotoCapture : RealEstateAddEvent()
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateAddEvent()

}