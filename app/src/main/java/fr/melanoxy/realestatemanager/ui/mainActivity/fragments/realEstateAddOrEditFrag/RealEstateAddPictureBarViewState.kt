package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import android.graphics.Bitmap
import fr.melanoxy.realestatemanager.ui.utils.NativeText

data class RealEstateAddPictureBarViewState (
    val noPictureTextViewVisibility: Int,
    val barText: NativeText,
    val barIconTip: Bitmap?,
)