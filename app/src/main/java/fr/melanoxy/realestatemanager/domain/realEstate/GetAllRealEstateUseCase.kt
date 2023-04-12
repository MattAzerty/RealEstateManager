package fr.melanoxy.realestatemanager.domain.realEstate

import fr.melanoxy.realestatemanager.data.dao.RealEstateDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllRealEstateUseCase @Inject constructor(
    private val realEstateDao: RealEstateDao,
) {
    fun invoke(): Flow<List<RealEstateEntity>> = realEstateDao.getAll()
}