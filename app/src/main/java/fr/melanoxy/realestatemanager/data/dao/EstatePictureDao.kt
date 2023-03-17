package fr.melanoxy.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstatePictureDao {

    @Insert
    suspend fun insert(estatePictureEntity: EstatePictureEntity)

    @Query("SELECT * FROM realEstate")
    @Transaction
    fun getPicturesFromRealEstate(): Flow<List<RealEstateWithPictureEntity>>
}