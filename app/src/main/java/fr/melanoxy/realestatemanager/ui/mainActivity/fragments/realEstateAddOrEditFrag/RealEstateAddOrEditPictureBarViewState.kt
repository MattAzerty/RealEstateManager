package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import android.graphics.Bitmap
import fr.melanoxy.realestatemanager.ui.utils.NativeText

data class RealEstateAddOrEditPictureBarViewState (
    val noPictureTextViewVisibility: Int,
    val barText: NativeText,
    val barIconTip: Bitmap?,
)