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

    @Query("SELECT * FROM estateAgent")//https://www.reddit.com/r/androiddev/comments/7g9246/dont_forget_to_inject_android_room_sql_language/?rdt=39767
    fun getAllAgent(): Flow<List<EstateAgentEntity>>
}