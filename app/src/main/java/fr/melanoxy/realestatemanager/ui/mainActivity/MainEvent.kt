package fr.melanoxy.realestatemanager.ui.mainActivity

sealed class MainEvent {
    data class ShowSnackBarMessage(val message: String) : MainEvent()
}