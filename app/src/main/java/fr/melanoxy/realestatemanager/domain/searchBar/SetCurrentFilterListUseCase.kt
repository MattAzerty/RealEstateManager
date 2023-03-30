package fr.melanoxy.realestatemanager.domain.searchBar

import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetCurrentFilterListUseCase @Inject constructor(
    private val sharedRepository: SharedRepository,
) {
    fun invoke(filterList:List<String>) = sharedRepository.setCurrentFilterList(filterList)
}