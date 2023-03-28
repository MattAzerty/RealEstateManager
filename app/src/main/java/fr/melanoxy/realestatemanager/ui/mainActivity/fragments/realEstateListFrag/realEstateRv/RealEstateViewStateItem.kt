package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv

import android.net.Uri

data class RealEstateViewStateItem (
    val realEstateId: Long,
    val pictureUri: Uri,
    val realEstateType: String,
    val realEstateCity: String,
    val realEstatePrice: String,
    val onRealEstateClicked: () -> Unit,
)