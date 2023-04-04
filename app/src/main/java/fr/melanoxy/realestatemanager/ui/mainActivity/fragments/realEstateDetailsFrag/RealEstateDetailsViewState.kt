package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem

data class RealEstateDetailsViewState (
    val pictureList: List<RealEstatePictureViewStateItem>,
    val description: String,
)