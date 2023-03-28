package fr.melanoxy.realestatemanager.ui.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.data.RealEstateRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
) : ViewModel() {

    private var isTablet: Boolean = false


    fun onResume(isTablet: Boolean) {
        realEstateRepository.isTablet(isTablet)
        this.isTablet = isTablet
    }
}