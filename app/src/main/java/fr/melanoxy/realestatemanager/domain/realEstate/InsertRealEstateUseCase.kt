package fr.melanoxy.realestatemanager.domain.realEstate

import android.database.sqlite.SQLiteException
import fr.melanoxy.realestatemanager.data.dao.RealEstateDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertRealEstateUseCase @Inject constructor(private val realEstateDao: RealEstateDao) {

    suspend fun invoke(realEstateEntity: RealEstateEntity): Long? = try {
        realEstateDao.insert(realEstateEntity)
    } catch (e: SQLiteException) {
        e.printStackTrace()
        null
    }
}