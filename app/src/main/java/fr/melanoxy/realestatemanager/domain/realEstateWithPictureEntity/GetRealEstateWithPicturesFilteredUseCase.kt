package fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity

import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealEstateWithPicturesFilteredUseCase @Inject constructor(
    private val sharedRepository: SharedRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun invoke(): Flow<List<RealEstateWithPictureEntity>> = sharedRepository.getFilterEstateList()
}