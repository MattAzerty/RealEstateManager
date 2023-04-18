package fr.melanoxy.realestatemanager.data.repositories

import androidx.sqlite.db.SimpleSQLiteQuery
import fr.melanoxy.realestatemanager.data.dao.EstatePictureDao
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarStateItem
import fr.melanoxy.realestatemanager.ui.utils.ESTATE_AGENTS
import fr.melanoxy.realestatemanager.ui.utils.FILTERING_CRITERIA_LIST
import fr.melanoxy.realestatemanager.ui.utils.toDateFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedRepository @Inject constructor(
    private val estatePictureDao: EstatePictureDao,
) {
//FRAGMENT STATE
val fragmentStateFlow= MutableStateFlow<NavigationEvent>(NavigationEvent.RealEstateListFragment)

//ENTRY DATE PICKER

    val entryDatePickedChannelFromAddOrEdit = Channel<String>()
    val entryDatePickedChannelFromSearchBar = Channel<String>()
    val entryDatePickedChannelFromSearchBarTablet = Channel<String>()

    fun setEntryDatePicked(entryDatePicked: String) {
        when (fragmentStateFlow.value) {
        NavigationEvent.AddOrEditRealEstateFragment -> entryDatePickedChannelFromAddOrEdit.trySend(entryDatePicked)
        NavigationEvent.RealEstateListFragment ->
            if(isTabletStateFlow.value) entryDatePickedChannelFromSearchBarTablet.trySend(entryDatePicked)
            else entryDatePickedChannelFromSearchBar.trySend(entryDatePicked)
        else -> {}
        }
    }

    //SALE DATE PICKER
    val saleDatePickedChannelFromAddOrEdit = Channel<String>()
    val saleDatePickedChannelFromSearchBar = Channel<String>()
    val saleDatePickedChannelFromSearchBarTablet = Channel<String>()

    fun setSaleDatePicked(saleDatePicked: String) {
        when (fragmentStateFlow.value) {
            NavigationEvent.AddOrEditRealEstateFragment -> saleDatePickedChannelFromAddOrEdit.trySend(
                saleDatePicked
            )
            NavigationEvent.RealEstateListFragment ->
                if(isTabletStateFlow.value) saleDatePickedChannelFromSearchBarTablet.trySend(saleDatePicked)
                else saleDatePickedChannelFromSearchBar.trySend(saleDatePicked)
            else -> {}
        }
    }
//TABLET MODE DATA
    val isTabletStateFlow = MutableStateFlow(false)

    fun isTablet(isTablet: Boolean) {
        isTabletStateFlow.value = isTablet
    }

//SEARCH DATA
    private val tagFilterListStateFlow = MutableStateFlow(FILTERING_CRITERIA_LIST)
    val currentFilterTagListMutableStateFlow:MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    fun getCurrentFilterTagList(): Flow<List<RealEstateSearchBarStateItem>> {
        return tagFilterListStateFlow
            .combine(currentFilterTagListMutableStateFlow) { tagList, currentFilterTagList ->
                tagList.mapIndexed { index, item ->
                    RealEstateSearchBarStateItem(
                        id = index.toLong(),
                        criteriaType = item.split(":")[1],
                        tag = "${item.split(":")[0]}: "
                    )
                }.filter { list ->
                    currentFilterTagList.none { searchString ->
                        list.tag.contains("${searchString.split(":")[0]}:")
                    }
                }
            }
    }

    fun setCurrentFilterList(filterList: List<String>) {
        currentFilterTagListMutableStateFlow.value = filterList
    }


    @ExperimentalCoroutinesApi
    fun getFilterEstateList(): Flow<List<RealEstateWithPictureEntity>> {
        return simpleSQLiteQueryByUserFlow.flatMapLatest {
            estatePictureDao.getFilteredRealEstateWithPicture(it)
        }
    }

    private val simpleSQLiteQueryByUserFlow: Flow<SimpleSQLiteQuery> = currentFilterTagListMutableStateFlow.map { listOfTagQuery ->
        // Query string
        var queryString = String()
        // List of bind parameters
        val args: MutableList<Any> = ArrayList()
        var containsCondition = false
        // Beginning of query string
        queryString += "SELECT * FROM realEstate"

        listOfTagQuery.forEach { tag ->

            when(tag.split(":")[0])
            {
                "[A]" -> {//Case filter: AgentName
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " estateAgentId = ?"
                    args.add(getAgentId(tag.split(":")[1].trim()))
                    containsCondition = true
                }
                "[T]" -> {//Case filter: TypeOfProperty
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " propertyType LIKE ?"
                    args.add(tag.split(":")[1].trim())
                    containsCondition = true
                }
                "[C]" -> {//Case filter: City
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " city LIKE ?"
                    args.add(tag.split(":")[1].trim())
                    containsCondition = true
                }
                "[POI]" -> {//Case filter: PointOfInterest
                    tag.split(":")[1].trim().split("|").forEach {
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " pointsOfInterest LIKE ?"
                    args.add("%$it%")
                    containsCondition = true
                }
                }
                "[#P]" -> {//Case filter: minimum NumberOfPictures
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " numberOfPictures > ?"
                    args.add(tag.split(":")[1].trim().toInt())
                    containsCondition = true
                }
                "[SD>]" -> {//Case filter: After this SaleDate
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " saleDate > ?"
                    args.add(toDateFormat(tag.split(":")[1].trim()).time)
                    containsCondition = true
                }
                "[SD<]" -> {//Case filter: Before this SaleDate
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " saleDate < ?"
                    args.add(toDateFormat(tag.split(":")[1].trim()).time)
                    containsCondition = true
                }
                "[MED>]" -> {//Case filter: After this EntryDate
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " marketEntryDate > ?"
                    args.add(toDateFormat(tag.split(":")[1].trim()).time)
                    containsCondition = true
                }
                "[MED<]" -> {//Case filter: Before this EntryDate
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " marketEntryDate < ?"
                    args.add(toDateFormat(tag.split(":")[1].trim()).time)
                    containsCondition = true
                }
                "[$>]" -> {//Case filter: Price superior to
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " price > ?"
                    args.add(tag.split(":")[1].trim().toDouble())
                    containsCondition = true
                }
                "[$<]" -> {//Case filter: Price inferior to
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " price < ?"
                    args.add(tag.split(":")[1].trim().toDouble())
                    containsCondition = true
                }
                "[#R]" -> {//Case filter: numberOfRooms
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " numberOfRooms >= ?"
                    args.add(tag.split(":")[1].trim().toInt())
                    containsCondition = true
                }
                "[#B]" -> {//Case filter: numberOfBedRooms
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " numberOfBedrooms >= ?"
                    args.add(tag.split(":")[1].trim().toInt())
                    containsCondition = true
                }
                "[S>]" -> {//Case filter: Surface minimum.
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " surfaceArea > ?"
                    args.add(tag.split(":")[1].trim().toDouble())
                    containsCondition = true
                }
                "[S<]" -> {//Case filter: Surface maximum.
                    queryString += if(containsCondition) " AND" else " WHERE"
                    queryString += " surfaceArea < ?"
                    args.add(tag.split(":")[1].trim().toDouble())
                    containsCondition = true
                }
            }
        }
        queryString += ";"// End of query string
        SimpleSQLiteQuery(queryString,args.toTypedArray())
    }

    private fun getAgentId(agentName: String): Long {
       val indexAgent = ESTATE_AGENTS.indexOfFirst { it.firstName == agentName.split(" ")[0] ||  it.lastName== agentName.split(" ")[1] }
    return indexAgent.toLong() + 1
    }
}