package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem
import java.util.*
import kotlin.collections.ArrayList


data class RealEstateAddOrEditViewState(
    val estateAgentId: Long?=null,
    val propertyType: String?=null,
    val pointsOfInterest: ArrayList<String>?=null,
    var description: String?=null,
    val pictureItemList: List<RealEstatePictureViewStateItem>? =null,
    val marketEntryDate: String?=null,
    val saleDate: String?=null
)