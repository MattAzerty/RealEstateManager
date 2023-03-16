package fr.melanoxy.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import fr.melanoxy.realestatemanager.domain.agentWithRealEstateEntity.AgentWithRealEstateEntity
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstateEntity: RealEstateEntity)

    @Query("SELECT * FROM estateAgent")//TODO explain why not realestate (foreign key)
    @Transaction
    fun getAllRealEstateProperties(): Flow<List<AgentWithRealEstateEntity>>


}