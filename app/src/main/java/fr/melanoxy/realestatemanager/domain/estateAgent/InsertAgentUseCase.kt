package fr.melanoxy.realestatemanager.domain.estateAgent

import fr.melanoxy.realestatemanager.data.dao.EstateAgentDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertAgentUseCase @Inject constructor(
    private val estateAgentDao: EstateAgentDao,
    ) {
        suspend fun invoke(estateAgentEntity: EstateAgentEntity) {
            estateAgentDao.insert(estateAgentEntity)
        }
}