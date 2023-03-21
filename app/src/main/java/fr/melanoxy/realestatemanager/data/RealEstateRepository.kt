package fr.melanoxy.realestatemanager.data

import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealEstateRepository @Inject constructor() {

    /*private val currentPictureIdMutableSharedFlow = MutableStateFlow<Long?>(null)
    val currentPictureIdChannel = Channel<Long>()
    val currentPictureIdFlow: StateFlow<Long?> = currentPictureIdMutableSharedFlow.asStateFlow()

    fun setCurrentPictureIdClicked(currentPictureId: Long) {
        currentPictureIdMutableSharedFlow.value =currentPictureId
        currentPictureIdChannel.trySend(currentPictureId)
    }*/

    val selectedRealEstateIdMutableSharedFlow = MutableStateFlow<Long?>(null)



}