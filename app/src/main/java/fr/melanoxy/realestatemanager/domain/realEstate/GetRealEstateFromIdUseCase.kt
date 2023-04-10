package fr.melanoxy.realestatemanager.domain.realEstate

import fr.melanoxy.realestatemanager.data.dao.RealEstateDao
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealEstateFromIdUseCase  @Inject constructor(
    private val realEstateDao: RealEstateDao,
) {

    fun invoke(selectedId: Long): Flow<RealEstateEntity> = realEstateDao.getRealEstateFromId(selectedId)
}