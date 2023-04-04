package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv

import android.net.Uri

data class RealEstateViewStateItem (
    val realEstateId: Long,
    val pictureUri: Uri,
    val realEstateType: String,
    val realEstateCity: String,
    val realEstatePrice: String,
    val isSelected: Boolean,
    val onRealEstateClicked: () -> Unit,
    val onRealEstateLongClick: () -> Unit,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RealEstateViewStateItem

        if (realEstateId != other.realEstateId) return false
        if (pictureUri != other.pictureUri) return false
        if (realEstateType != other.realEstateType) return false
        if (realEstateCity != other.realEstateCity) return false
        if (realEstatePrice != other.realEstatePrice) return false
        if (isSelected != other.isSelected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = realEstateId.hashCode()
        result = 31 * result + pictureUri.hashCode()
        result = 31 * result + realEstateType.hashCode()
        result = 31 * result + realEstateCity.hashCode()
        result = 31 * result + realEstatePrice.hashCode()
        result = 31 * result + isSelected.hashCode()
        return result
    }
}