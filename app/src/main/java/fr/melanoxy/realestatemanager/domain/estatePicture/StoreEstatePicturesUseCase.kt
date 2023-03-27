package fr.melanoxy.realestatemanager.domain.estatePicture

import android.database.sqlite.SQLiteException
import fr.melanoxy.realestatemanager.data.RealEstateRepository
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv.RealEstatePictureViewStateItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreEstatePicturesUseCase @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
    ) {

    suspend fun invoke(estatePictureList: List<RealEstatePictureViewStateItem>): Boolean? = try {
        realEstateRepository.storeEstatePictureEntities(estatePictureList)
        true
    } catch (e: SQLiteException) {
        e.printStackTrace()
        false
    }
}