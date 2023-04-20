package fr.melanoxy.realestatemanager

import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesUseCase
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import io.mockk.every
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import io.mockk.mockk
import io.mockk.verify
import io.mockk.confirmVerified
import assertk.assertThat
import assertk.assertions.isEqualTo

class GetRealEstateWithPictureUseCaseTest {

    companion object {
        private val GET_REAL_ESTATES_WITH_PICTURES_FLOW: Flow<List<RealEstateWithPictureEntity>> = flowOf()
    }

    private val estatePictureDao: EstatePictureDao = mockk()

    private val getRealEstateWithPicturesUseCase = GetRealEstateWithPicturesUseCase(estatePictureDao)

    @Before
    fun setUp() {
        every { estatePictureDao.getAllRealEstateWithPicture() } returns GET_REAL_ESTATES_WITH_PICTURES_FLOW
    }

    @Test
    fun verify() {
        // When
        val result = getRealEstateWithPicturesUseCase.invoke()

        // Then
        assertThat(result).isEqualTo(GET_REAL_ESTATES_WITH_PICTURES_FLOW)
        verify(exactly = 1) { estatePictureDao.getAllRealEstateWithPicture() }
        confirmVerified(estatePictureDao)
    }

}