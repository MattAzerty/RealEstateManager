package fr.melanoxy.realestatemanager.domain.realEstate

import android.database.sqlite.SQLiteException
import fr.melanoxy.realestatemanager.data.dao.RealEstateDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateRealEstateUseCase @Inject constructor(private val realEstateDao: RealEstateDao) {
    suspend fun invoke(realEstateEntity: RealEstateEntity): Boolean = try {
        realEstateDao.update(realEstateEntity)
        true
    } catch (e: SQLiteException) {
        e.printStackTrace()
        false
    }
}