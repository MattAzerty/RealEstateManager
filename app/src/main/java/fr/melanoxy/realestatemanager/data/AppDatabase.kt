package fr.melanoxy.realestatemanager.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.viewbinding.BuildConfig
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import fr.melanoxy.realestatemanager.data.dao.EstateAgentDao
import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import fr.melanoxy.realestatemanager.data.dao.RealEstateDao
import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity
import fr.melanoxy.realestatemanager.ui.utils.DATABASE_NAME
import fr.melanoxy.realestatemanager.ui.utils.ESTATE_AGENTS
import fr.melanoxy.realestatemanager.ui.utils.KEY_INPUT_DATA_WORK_MANAGER

@Database(entities = [RealEstateEntity::class, EstateAgentEntity::class, EstatePictureEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getRealEstateDao(): RealEstateDao
    abstract fun getEstateAgentDao(): EstateAgentDao
    abstract fun getEstatePictureDao(): EstatePictureDao

    companion object {

        fun create(
            application: Application,
            workManager: WorkManager,
            gson: Gson,
        ): AppDatabase {
            val builder = Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                DATABASE_NAME
            )

            //https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1#4785
            // prepopulate the database after onCreate was called
            builder.addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {

                    val entitiesAsJson = gson.toJson(ESTATE_AGENTS)

                    workManager.enqueue(
                        OneTimeWorkRequestBuilder<InitializeDatabaseWorker>()
                            .setInputData(workDataOf(KEY_INPUT_DATA_WORK_MANAGER to entitiesAsJson))
                            .build()
                    )
                }
            })

            if (BuildConfig.DEBUG) {
                builder.fallbackToDestructiveMigration()
            }

            return builder.build()
        }
    }
}

