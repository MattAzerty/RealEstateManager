package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import com.google.android.gms.maps.model.LatLng

data class RealEstateMarkerStateItem (
    val id:Long,
    val realEstateName:String,
    val realEstatePrice:String,
    val coordinates:LatLng,
    val isSold:Boolean
)