package fr.melanoxy.realestatemanager.data

import android.content.Context
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
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
    val isTabletStateFlow = MutableStateFlow<Boolean>(false)
    val selectedRealEstateIdMutableStateFlow = MutableStateFlow<Long?>(null)
    val estatePicturesListEntityMutableStateFlow = MutableStateFlow<List<EstatePictureEntity>?>(null)

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

    suspend fun storeEstatePictureEntities(
        estatePictureList: List<RealEstatePictureViewStateItem>,
        realEstateIdCreated: Long?
    ) {

        val pictureEntity:MutableList<EstatePictureEntity> = ArrayList()
        estatePicturesListEntityMutableStateFlow.value = null

        estatePictureList.forEach {
            val inputStream = context.contentResolver.openInputStream(it.pictureUri)
            val file = File(context.filesDir, "${it.realEstatePictureName}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            pictureEntity.add(
                EstatePictureEntity(
                    realEstateId = realEstateIdCreated!!,
                    name = it.realEstatePictureName,
                    path = file.absolutePath
                )
            )
        }

        estatePicturesListEntityMutableStateFlow.emit(pictureEntity)

    }

    fun isTablet(isTablet: Boolean) {
        isTabletStateFlow.value = isTablet
    }


}