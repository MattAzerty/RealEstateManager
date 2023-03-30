package fr.melanoxy.realestatemanager.domain.searchBar

import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentFilterUseCase @Inject
constructor(
    private val sharedRepository: SharedRepository,
) {
    fun invoke(): Flow<List<String>> = sharedRepository.getCurrentFilterTagList()
}