package fr.melanoxy.realestatemanager.domain.agentWithRealEstateEntity

import androidx.room.Embedded
import androidx.room.Relation
import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity

data class AgentWithRealEstateEntity(
    @Embedded
    val estateAgentEntity: EstateAgentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "estateAgentId"
    )
    val realEstateProperties: List<RealEstateEntity>,
)