package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateLoanFrag

import fr.melanoxy.realestatemanager.ui.utils.NativeText

sealed class RealEstateLoanEvent {
    data class DisplaySnackBarMessage(val message: NativeText) : RealEstateLoanEvent()
}