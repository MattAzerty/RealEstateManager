package fr.melanoxy.realestatemanager.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.android.gms.maps.model.LatLng
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.TestCoroutineRule
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.domain.estateAgent.GetEstateAgentUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.DeleteEstatePictureUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.InsertEstatePictureUserCase
import fr.melanoxy.realestatemanager.domain.estatePicture.StoreEstatePicturesUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.UpdateEstatePictureUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.GetCoordinateRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.GetThumbnailRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.InsertRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.UpdateRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesFromIdUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.RealEstateAddOrEditEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.RealEstateAddOrEditViewModel
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos.RealEstateViewPagerInfosStateItem
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RealEstateAddOrEditViewModelTest {

    companion object {
        private const val INSERT_RE_DELAY = 50L
        private const val DEFAULT_DESCRIPTION = "DEFAULT_DESCRIPTION"
    }

    /*
    Allows to test code that uses LiveData, ViewModel,
     and other Android Architecture Components that depend on the Android main thread.
     Allow to run the code on a separate thread or on the JUnit test thread to avoid race conditions
     or other issues
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /*
    This rule is used for testing asynchronous code that uses Kotlin coroutines in a JUnit test environment.
     */
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val permissionChecker: PermissionChecker = mockk()
    private lateinit var realEstateRepository: RealEstateRepository
    private lateinit var sharedRepository: SharedRepository
    private val getRealEstateWithPicturesFromIdUseCase: GetRealEstateWithPicturesFromIdUseCase = mockk()
    private val getEstateAgentUseCase: GetEstateAgentUseCase = mockk()
    private val getCoordinateRealEstateUseCase: GetCoordinateRealEstateUseCase = mockk()
    private val getThumbnailRealEstateUseCase: GetThumbnailRealEstateUseCase = mockk()
    private val insertRealEstateUseCase: InsertRealEstateUseCase = mockk()
    private val updateRealEstateUseCase: UpdateRealEstateUseCase = mockk()
    private val updateEstatePictureUseCase: UpdateEstatePictureUseCase = mockk()
    private val storeEstatePicturesUseCase: StoreEstatePicturesUseCase = mockk()
    private val deleteEstatePictureUseCase: DeleteEstatePictureUseCase = mockk()
    private val insertEstatePictureUserCase: InsertEstatePictureUserCase = mockk()
    private lateinit var realEstateAddOrEditViewModel:RealEstateAddOrEditViewModel
    private lateinit var uri: Uri
    private lateinit var bitmap: Bitmap

    @Before
    fun setUp() {

        realEstateRepository = mockk()
        every { realEstateRepository.selectedRealEstateIdStateFlow} returns MutableStateFlow(null)
        every { realEstateRepository.estatePicturesListEntityMutableStateFlow} returns MutableStateFlow(null)
        justRun { realEstateRepository.setSelectedRealEstateId(null) }

        sharedRepository = mockk()
        every { sharedRepository.entryDatePickedChannelFromAddOrEdit} returns Channel<String>().apply {
            trySend(
                DEFAULT_DESCRIPTION
            ).isSuccess
        }
        every { sharedRepository.saleDatePickedChannelFromAddOrEdit} returns Channel<String>().apply {
            trySend(
                DEFAULT_DESCRIPTION
            ).isSuccess
        }

        uri = mockk()
        every { uri.scheme } returns "content"
        every { uri.authority } returns "com.example.myProvider"
        every { uri.path } returns "/myPath/myPage"

        val nemo = LatLng(-48.8767, -123.3933)
        coEvery { getCoordinateRealEstateUseCase.invoke(any()) } returns nemo//https://goo.gl/maps/a3gGXw9acvQEbfTW6
        bitmap = mockk()
        coEvery { getThumbnailRealEstateUseCase.invoke(nemo) } returns bitmap
        coEvery { insertRealEstateUseCase.invoke(any()) } coAnswers {
            delay(INSERT_RE_DELAY)
            1L
        }
        coEvery { storeEstatePicturesUseCase.invoke(any(),any()) } coAnswers {
            delay(INSERT_RE_DELAY)
            true
        }

        realEstateAddOrEditViewModel = RealEstateAddOrEditViewModel(
            permissionChecker = permissionChecker,
            coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
            realEstateRepository = realEstateRepository,
            sharedRepository = sharedRepository,
            getRealEstateWithPicturesFromIdUseCase = getRealEstateWithPicturesFromIdUseCase,
            getEstateAgentUseCase = getEstateAgentUseCase,
            getCoordinateRealEstateUseCase = getCoordinateRealEstateUseCase,
            getThumbnailRealEstateUseCase = getThumbnailRealEstateUseCase,
            insertRealEstateUseCase = insertRealEstateUseCase,
            updateRealEstateUseCase = updateRealEstateUseCase,
            updateEstatePictureUseCase = updateEstatePictureUseCase,
            storeEstatePicturesUseCase = storeEstatePicturesUseCase,
            deleteEstatePictureUseCase = deleteEstatePictureUseCase,
            insertEstatePictureUserCase = insertEstatePictureUserCase
        )
    }


    @Test
    fun `notification on RealEstate added success case`() = testCoroutineRule.runTest {

    // Given
        //add Infos and address
        every {realEstateRepository.realEstateViewPagerInfosStateItem} returns RealEstateViewPagerInfosStateItem(
        street = "myStreet",
        city = "myCity",
        state = "myState",
        zipcode = "myZipcode",
        price = 1.1,
        numberOfRooms = 2,
        numberOfBedrooms = 3,
        surfaceArea = 4.4
        )
        //add one pic
        val uriList = listOf(uri)
        realEstateAddOrEditViewModel.onPickVisualMediaResult(uriList)
        //select Agent
        realEstateAddOrEditViewModel.onAgentSelected(1)
            // When
        realEstateAddOrEditViewModel.onSaveRealEstateClicked()
            // Then
        advanceUntilIdle()
        assertThat(realEstateAddOrEditViewModel.realEstateAddFragSingleLiveEvent.value).isEqualTo(
            RealEstateAddOrEditEvent.CloseFragmentWithMessage(NativeText.Resource(R.string.add_success))
        )
    }
}
