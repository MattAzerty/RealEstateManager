package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag

import android.net.Uri


data class RealEstateViewStateItem (
    val realEstateId: Long,
    val pictureUri: Uri,
    val realEstateName: String,
    val isStored: Boolean,
)