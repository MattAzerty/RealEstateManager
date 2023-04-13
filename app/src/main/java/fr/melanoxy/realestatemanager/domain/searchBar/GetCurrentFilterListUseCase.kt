package fr.melanoxy.realestatemanager.domain.searchBar

import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarStateItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentFilterListUseCase @Inject
constructor(
    private val sharedRepository: SharedRepository,
) {
    fun invoke(): Flow<List<RealEstateSearchBarStateItem>> = sharedRepository.getCurrentFilterTagList()
}