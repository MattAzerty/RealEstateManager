package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateAddOrEditEvent {
    object CloseFragment : RealEstateAddOrEditEvent()
    object RequestCameraPermission : RealEstateAddOrEditEvent()
    object LaunchActivityPhotoCapture : RealEstateAddOrEditEvent()
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateAddOrEditEvent()

}