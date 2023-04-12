package fr.melanoxy.realestatemanager.ui.mainActivity

import android.view.View

interface MainEventListener {

    fun displaySnackBarMessage(message: CharSequence)
    fun hideKeyboard(view: View)
    fun showKeyboard(view: View)
    fun showDatePicker(type: Int)
    fun switchMainPane(id: Int)
    fun switchSecondPane(id: Int)
}