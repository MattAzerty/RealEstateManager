package fr.melanoxy.realestatemanager.data

import android.content.Context
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos.RealEstateViewPagerInfosStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv.RealEstatePictureViewStateItem
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealEstateRepository @Inject constructor(
    private val context: Context
) {

    /*private val currentPictureIdMutableSharedFlow = MutableStateFlow<Long?>(null)
   val currentPictureIdChannel = Channel<Long>()
   val currentPictureIdFlow: StateFlow<Long?> = currentPictureIdMutableSharedFlow.asStateFlow()

   fun setCurrentPictureIdClicked(currentPictureId: Long) {
       currentPictureIdMutableSharedFlow.value =currentPictureId
       currentPictureIdChannel.trySend(currentPictureId)
   }*/

    val selectedRealEstateIdMutableSharedFlow = MutableStateFlow<Long?>(null)
    val estatePicturesPathListMutableStateFlow = MutableStateFlow<List<String>?>(null)

    val realEstateViewPagerInfosStateItem = RealEstateViewPagerInfosStateItem()

    fun onStreetFieldChanged(street: String) {
        realEstateViewPagerInfosStateItem.street = street
    }

    fun onCityFieldChanged(city: String) {
        realEstateViewPagerInfosStateItem.city = city
    }

    fun onStateChanged(state: String) {
        realEstateViewPagerInfosStateItem.state = state
    }

    fun onZipcodeChanged(zipcode: String) {
        realEstateViewPagerInfosStateItem.zipcode = zipcode
    }

    fun onPriceFieldChanged(price: String) {
        realEstateViewPagerInfosStateItem.price = price.toDouble()
    }

    fun onNumberOfRoomChanged(numberOfRoom: String) {
        realEstateViewPagerInfosStateItem.numberOfRooms = numberOfRoom.toInt()
    }

    fun onNumberOfBedroomChanged(numberOfBedroom: String) {
        realEstateViewPagerInfosStateItem.numberOfBedrooms = numberOfBedroom.toInt()
    }

    fun onSurfaceFieldChanged(surface: String) {
        realEstateViewPagerInfosStateItem.surfaceArea = surface.toDouble()
    }

    suspend fun storeEstatePictureEntities(estatePictureList: List<RealEstatePictureViewStateItem>) {

        val pathList:MutableList<String> = ArrayList()

        estatePictureList.forEach {
            val inputStream = context.contentResolver.openInputStream(it.pictureUri)
            val file = File(context.filesDir, "${it.realEstatePictureName}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            pathList.add(file.absolutePath)
        }

        estatePicturesPathListMutableStateFlow.emit(pathList)

    }


}