package fr.melanoxy.realestatemanager.ui.mainActivity

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SearchRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private var isTablet: Boolean = false


    fun onResume(isTablet: Boolean) {
        searchRepository.isTablet(isTablet)
        this.isTablet = isTablet
    }
}