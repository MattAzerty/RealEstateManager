package fr.melanoxy.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateAgentDao {
    @Insert
    suspend fun insert(estateAgentEntity: EstateAgentEntity)

    @Query("SELECT * FROM estateAgent")
    fun getAllAgent(): Flow<List<EstateAgentEntity>>
}