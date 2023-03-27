package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import java.util.*
import kotlin.collections.ArrayList


data class RealEstateAddOrEditViewState(
    var estateAgentId: Long?=null,
    var propertyType: String?=null,
    var pointsOfInterest: ArrayList<Int>?=null,
    var description: String?=null,
    var photosListPath: ArrayList<String>?=null,
    var marketEntryDate: Date?=null,
    var saleDate: Date?=null
)