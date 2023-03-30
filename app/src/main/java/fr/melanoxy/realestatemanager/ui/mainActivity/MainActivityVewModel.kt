package fr.melanoxy.realestatemanager.ui.mainActivity

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    private var isTablet: Boolean = false


    fun onResume(isTablet: Boolean) {
        sharedRepository.isTablet(isTablet)
        this.isTablet = isTablet
    }

    fun onDateSelected(type: Int, formattedDate: String) {
        when(type){
            R.string.entryDate -> sharedRepository.setEntryDatePicked(formattedDate)
            R.string.saleDate -> sharedRepository.setSaleDatePicked(formattedDate)
        }
    }
}