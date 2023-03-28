package fr.melanoxy.realestatemanager.data.dao

import androidx.room.*
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstatePictureDao {

    @Insert
    suspend fun insert(estatePictureEntity: EstatePictureEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pictureEntities: List<EstatePictureEntity>)

    @Query("SELECT * FROM realEstate")
    @Transaction
    fun getAllRealEstateWithPicture(): Flow<List<RealEstateWithPictureEntity>>

    @Query("SELECT * FROM estatePicture WHERE realEstateId=:selectedId")
    @Transaction
    fun getPictureOfRealEstate(selectedId: Long): Flow<List<EstatePictureEntity>>
}