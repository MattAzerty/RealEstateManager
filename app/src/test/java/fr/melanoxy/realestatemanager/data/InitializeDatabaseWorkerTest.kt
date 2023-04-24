package fr.melanoxy.realestatemanager.data

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.gson.Gson
import fr.melanoxy.realestatemanager.TestCoroutineRule
import fr.melanoxy.realestatemanager.domain.estateAgent.InsertAgentUseCase
import fr.melanoxy.realestatemanager.getDefaultAgentEntitiesAsJson
import fr.melanoxy.realestatemanager.getDefaultAgentEntity
import fr.melanoxy.realestatemanager.ui.utils.KEY_INPUT_DATA_WORK_MANAGER
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InitializeDatabaseWorkerTest {


    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val context: Context = mockk()
    private val workerParams: WorkerParameters = mockk(relaxed = true)
    private val insertAgentUseCase: InsertAgentUseCase = mockk()
    private val gson: Gson = AppModule.provideGson()

    private val initializeDatabaseWorker = spyk(
        InitializeDatabaseWorker(
            context = context,
            workerParams = workerParams,
            insertAgentUseCase = insertAgentUseCase,
            gson = gson,
            coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider()
        )
    )

    @Before
    fun setUp() {
        every { initializeDatabaseWorker.inputData.getString(KEY_INPUT_DATA_WORK_MANAGER) } returns getDefaultAgentEntitiesAsJson()
        coJustRun { insertAgentUseCase.invoke(any()) }
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        val result = initializeDatabaseWorker.doWork()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        coVerify(exactly = 1) {
            initializeDatabaseWorker.inputData.getString(KEY_INPUT_DATA_WORK_MANAGER)
            insertAgentUseCase.invoke(getDefaultAgentEntity(0))
            insertAgentUseCase.invoke(getDefaultAgentEntity(1))
            insertAgentUseCase.invoke(getDefaultAgentEntity(2))
        }
        confirmVerified(insertAgentUseCase)
    }

    @Test
    fun `error case - no input data`() = testCoroutineRule.runTest {
        // Given
        every { initializeDatabaseWorker.inputData.getString(KEY_INPUT_DATA_WORK_MANAGER) } returns null

        // When
        val result = initializeDatabaseWorker.doWork()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
        confirmVerified(insertAgentUseCase)
    }

    @Test
    fun `error case - can't parse empty input data`() = testCoroutineRule.runTest {
        // Given
        every { initializeDatabaseWorker.inputData.getString(KEY_INPUT_DATA_WORK_MANAGER) } returns ""

        // When
        val result = initializeDatabaseWorker.doWork()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
        confirmVerified(insertAgentUseCase)
    }

    @Test
    fun `error case - can't parse input data`() = testCoroutineRule.runTest {
        // Given
        every { initializeDatabaseWorker.inputData.getString(KEY_INPUT_DATA_WORK_MANAGER) } returns "some unparsable gibberish"

        // When
        val result = initializeDatabaseWorker.doWork()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
        confirmVerified(insertAgentUseCase)
    }
}