package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import android.graphics.Bitmap
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem

data class RealEstateDetailsViewState (
    val type:String,
    val city:String,
    val price:String,
    val thumbnail:Bitmap,
    val pictureList: List<RealEstatePictureViewStateItem>,
    val description: String,
    val agentName: String,
    val surface: String,
    val room: String,
    val bedroom:String,
    val nearPOI:String,
    val locationCoordinate:String,
)