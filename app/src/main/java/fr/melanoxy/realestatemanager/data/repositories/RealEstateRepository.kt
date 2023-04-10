package fr.melanoxy.realestatemanager.data.repositories

import android.content.Context
import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import fr.melanoxy.realestatemanager.domain.estatePicture.DeleteEstatePictureUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos.RealEstateViewPagerInfosStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealEstateRepository @Inject constructor(
    private val context: Context,
    private val estatePictureDao: EstatePictureDao
) {

    private val selectedRealEstateIdMutableStateFlow = MutableStateFlow<Long?>(null)
    val selectedRealEstateIdStateFlow = selectedRealEstateIdMutableStateFlow.asStateFlow()
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

        val folderName = "$realEstateIdCreated"
        val folder = File(context.filesDir, folderName)

        if (!folder.exists()) {
            folder.mkdir()
        }

        estatePictureList.forEach {
            val inputStream = context.contentResolver.openInputStream(it.pictureUri)
            val file = File(folder.absolutePath, "${it.realEstatePictureName}.jpg")
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

    suspend fun deleteEstatePictureEntities(
        estatePictureList: List<EstatePictureEntity>
    ) {
        estatePictureList.forEach {
            if(File(it.path).delete()) estatePictureDao.delete(it.id)
        }
    }

    fun setSelectedRealEstateId(id: Long?) {
        if(selectedRealEstateIdMutableStateFlow.value!= id) selectedRealEstateIdMutableStateFlow.value = id
        else selectedRealEstateIdMutableStateFlow.value = null
    }

}