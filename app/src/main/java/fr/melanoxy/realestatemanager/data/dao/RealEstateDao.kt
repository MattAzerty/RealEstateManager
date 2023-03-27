package fr.melanoxy.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstateEntity: RealEstateEntity): Long

}