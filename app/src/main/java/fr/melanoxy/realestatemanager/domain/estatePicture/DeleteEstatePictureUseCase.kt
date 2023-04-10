package fr.melanoxy.realestatemanager.domain.estatePicture

import android.database.sqlite.SQLiteException
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteEstatePictureUseCase @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
) {
    suspend fun invoke(
        estatePictureList: List<EstatePictureEntity>
    ): Boolean? = try {
        realEstateRepository.deleteEstatePictureEntities(estatePictureList)
        true
    } catch (e: SQLiteException) {
        e.printStackTrace()
        false
    }
}