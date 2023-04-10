package fr.melanoxy.realestatemanager.domain.estatePicture

import android.database.sqlite.SQLiteException
import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateEstatePictureUseCase @Inject constructor(private val estatePictureDao: EstatePictureDao) {
    suspend fun invoke(estatePictureEntityList: List<EstatePictureEntity>): Boolean = try {
        estatePictureDao.update(estatePictureEntityList)
        true
    } catch (e: SQLiteException) {
        e.printStackTrace()
        false
    }
}