package fr.melanoxy.realestatemanager.domain.estateAgent

import fr.melanoxy.realestatemanager.data.dao.EstateAgentDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetEstateAgentUseCase @Inject constructor(
    private val estateAgentDao: EstateAgentDao,
) {
    fun invoke(): Flow<List<EstateAgentEntity>> = estateAgentDao.getAllAgent()
}