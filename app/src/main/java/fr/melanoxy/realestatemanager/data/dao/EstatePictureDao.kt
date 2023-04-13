package fr.melanoxy.realestatemanager.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface EstatePictureDao {

    @Insert
    suspend fun insert(estatePictureEntity: EstatePictureEntity)

    @Update
    suspend fun update(estatePictureEntity: List<EstatePictureEntity>)

    @Query("DELETE FROM estatePicture WHERE id=:pictureId")
    suspend fun delete(pictureId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pictureEntities: List<EstatePictureEntity>)

    @Query("SELECT * FROM realEstate")
    @Transaction
    fun getAllRealEstateWithPicture(): Flow<List<RealEstateWithPictureEntity>>

    @RawQuery
    fun getFilteredRealEstateWithPicture(query: SupportSQLiteQuery): Flow<List<RealEstateWithPictureEntity>>

    @Query("SELECT * FROM realEstate WHERE id= :selectedId")
    @Transaction
    fun getRealEstateWithPictureFromId(selectedId: Long): Flow<RealEstateWithPictureEntity>

    @Query("SELECT * FROM estatePicture WHERE realEstateId= :selectedId")
    @Transaction
    fun getPictureOfRealEstate(selectedId: Long): Flow<List<EstatePictureEntity>>
}