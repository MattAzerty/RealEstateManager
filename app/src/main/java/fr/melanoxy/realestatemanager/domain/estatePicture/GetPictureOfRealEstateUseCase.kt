package fr.melanoxy.realestatemanager.domain.estatePicture

import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPictureOfRealEstateUseCase @Inject constructor(
    private val estatePictureDao: EstatePictureDao,
) {
    fun invoke(selectedId: Long): Flow<List<EstatePictureEntity>> = estatePictureDao.getPictureOfRealEstate(selectedId)
}