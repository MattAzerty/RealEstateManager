package fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity

import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealEstateWithPicturesFromIdUseCase @Inject constructor(
    private val estatePictureDao: EstatePictureDao,
) {
    fun invoke(selectedId: Long): Flow<RealEstateWithPictureEntity> = estatePictureDao.getRealEstateWithPictureFromId(selectedId)
}