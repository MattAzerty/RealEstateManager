package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos

data class RealEstateViewPagerInfosStateItem (
    var street: String?=null,
    var city: String?=null,
    var state: String?=null,
    var zipcode: String?=null,
    var price: Double?=null,
    var numberOfRooms: Int?=null,
    var numberOfBedrooms: Int?=null,
    var surfaceArea: Double?=null,
)