package fr.melanoxy.realestatemanager.data

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.melanoxy.realestatemanager.data.dao.EstateAgentDao
import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import fr.melanoxy.realestatemanager.data.dao.RealEstateDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(
            application
        )

    @Singleton
    @Provides
    fun provideWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideAppDatabase(
        application: Application,
        workManager: WorkManager,
        gson: Gson,
    ): AppDatabase = AppDatabase.create(application, workManager, gson)

    @Singleton
    @Provides
    fun provideRealEstateDao(appDatabase: AppDatabase): RealEstateDao = appDatabase.getRealEstateDao()

    @Singleton
    @Provides
    fun provideEstateAgentDao(appDatabase: AppDatabase): EstateAgentDao = appDatabase.getEstateAgentDao()

    @Singleton
    @Provides
    fun provideEstatePictureDao(appDatabase: AppDatabase): EstatePictureDao = appDatabase.getEstatePictureDao()
}