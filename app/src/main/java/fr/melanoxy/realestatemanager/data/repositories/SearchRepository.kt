package fr.melanoxy.realestatemanager.data.repositories

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    //private val context: Context
) {
    val isTabletStateFlow = MutableStateFlow<Boolean>(false)

    fun isTablet(isTablet: Boolean) {
        isTabletStateFlow.value = isTablet
    }
}