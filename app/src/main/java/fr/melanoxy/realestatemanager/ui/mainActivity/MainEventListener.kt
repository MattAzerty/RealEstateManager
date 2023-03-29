package fr.melanoxy.realestatemanager.ui.mainActivity

import android.view.View

interface MainEventListener {

    fun displaySnackBarMessage(message: CharSequence)
    fun hideKeyboard(view: View)
    fun showkeyboard(view: View)

}