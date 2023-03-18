package fr.melanoxy.realestatemanager.data

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharingRepository @Inject constructor() {

    val errorMessageSateFlow = MutableStateFlow<String?>(null)

}//END of uiWidgetRepository