package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv

import android.net.Uri


data class RealEstatePictureViewStateItem(
    val realEstateId: Long?,
    val pictureUri: Uri,
    val realEstatePictureName: String,
    val isStored: Boolean,
    val onRealEstatePictureClicked: () -> Unit,
)