package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateAddOrEditEvent {
    object CloseFragment : RealEstateAddOrEditEvent()
    object RequestCameraPermission : RealEstateAddOrEditEvent()
    object LaunchActivityPhotoCapture : RealEstateAddOrEditEvent()
    object LaunchActivityPickVisualMedia : RealEstateAddOrEditEvent()
    object ShowEditTextToChangePictureName : RealEstateAddOrEditEvent()
    object CloseEditTextToChangePictureName : RealEstateAddOrEditEvent()
    data class UpdateBarMessage(val barState: RealEstateAddOrEditPictureBarViewState) : RealEstateAddOrEditEvent()
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateAddOrEditEvent()

}