package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv

import android.net.Uri


data class RealEstatePictureViewStateItem(
    val realEstateId: Long?,
    val pictureUri: Uri,
    val realEstatePictureName: String,
    val toDelete: Boolean,
    val isStored: Boolean,
    val isEdited: Boolean,
    val onRealEstatePictureLongPress: () -> Unit ={},
    val onRealEstatePictureDeleteClicked: () -> Unit={},
    val onRealEstatePictureDeleteLongPress: () -> Unit={},
    val onRealEstatePictureClicked: () -> Unit={},
    val onRealEstatePictureNameClicked: () -> Unit={},
)