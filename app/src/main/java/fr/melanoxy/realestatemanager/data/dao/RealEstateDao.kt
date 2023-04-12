package fr.melanoxy.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstateEntity: RealEstateEntity): Long

    @Update
    suspend fun update(realEstateEntity: RealEstateEntity)

    @Query("SELECT * FROM realEstate")
    fun getAll(): Flow<List<RealEstateEntity>>

    @Query("SELECT * FROM realEstate WHERE id= :estateId")
    fun getRealEstateFromId(estateId:Long): Flow<RealEstateEntity>

}