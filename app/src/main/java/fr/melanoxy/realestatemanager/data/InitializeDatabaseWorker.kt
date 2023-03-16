package fr.melanoxy.realestatemanager.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import fr.melanoxy.realestatemanager.data.utils.fromJson
import fr.melanoxy.realestatemanager.domain.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity
import fr.melanoxy.realestatemanager.domain.estateAgent.InsertAgentUseCase
import fr.melanoxy.realestatemanager.ui.utils.KEY_INPUT_DATA_WORK_MANAGER
import kotlinx.coroutines.withContext

@HiltWorker
class InitializeDatabaseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val insertAgentUseCase: InsertAgentUseCase,
    private val gson: Gson,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result = withContext(coroutineDispatcherProvider.io) {
        val entitiesAsJson = inputData.getString(KEY_INPUT_DATA_WORK_MANAGER)

        if (entitiesAsJson != null) {
            val agentEntities = gson.fromJson<List<EstateAgentEntity>>(json = entitiesAsJson)

            if (agentEntities != null) {
                agentEntities.forEach { estateAgentEntity ->
                    insertAgentUseCase.invoke(estateAgentEntity)
                }
                Result.success()
            } else {
                Log.e(javaClass.simpleName, "Gson can't parse estateAgents : $entitiesAsJson")
                Result.failure()
            }
        } else {
            Log.e(javaClass.simpleName, "Failed to get data with key $KEY_INPUT_DATA_WORK_MANAGER from data: $inputData")
            Result.failure()
        }
    }
}