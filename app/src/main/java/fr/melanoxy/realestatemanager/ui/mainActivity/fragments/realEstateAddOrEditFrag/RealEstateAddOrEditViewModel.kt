package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.Address
import fr.melanoxy.realestatemanager.domain.estateAgent.GetEstateAgentUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.estatePicture.GetPictureOfRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.InsertEstatePictureUserCase
import fr.melanoxy.realestatemanager.domain.estatePicture.StoreEstatePicturesUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.GetCoordinateRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.GetThumbnailRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.InsertRealEstateUseCase
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.realEstateSpinners.AddAgentViewStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem
import fr.melanoxy.realestatemanager.ui.utils.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class RealEstateAddOrEditViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val realEstateRepository: RealEstateRepository,
    private val sharedRepository: SharedRepository,
    private val getPictureOfRealEstateUseCase: GetPictureOfRealEstateUseCase,
    private val getEstateAgentUseCase: GetEstateAgentUseCase,
    private val getCoordinateRealEstateUseCase: GetCoordinateRealEstateUseCase,
    private val getThumbnailRealEstateUseCase: GetThumbnailRealEstateUseCase,
    private val insertRealEstateUseCase: InsertRealEstateUseCase,
    private val storeEstatePicturesUseCase: StoreEstatePicturesUseCase,
    private val insertEstatePictureUserCase: InsertEstatePictureUserCase,
) : ViewModel() {

    private val realEstateAddOrEditViewState = RealEstateAddOrEditViewState()
    private var itemIndex = 1
    private var deletedItemIndices: MutableList<Int> = mutableListOf()
    private var selectedPicture: Uri?=null

    private val selectedRealEstateId =realEstateRepository.selectedRealEstateIdStateFlow.value
    private val tempPictureListItemLiveData = MutableLiveData<List<RealEstatePictureViewStateItem>>()

    val entryDatePickedLiveData: LiveData<Event<String>> = sharedRepository.entryDatePickedChannelFromAddOrEdit.asLiveDataEvent(
        coroutineDispatcherProvider.io) {
        emit(it)
    }

    val saleDatePickedLiveData: LiveData<Event<String>> = sharedRepository.saleDatePickedChannelFromAddOrEdit.asLiveDataEvent(
        coroutineDispatcherProvider.io) {
        emit(it)
    }

    val agentViewStateLiveData: LiveData<List<AddAgentViewStateItem>> = liveData(coroutineDispatcherProvider.io){
        if(selectedRealEstateId==null){
            getEstateAgentUseCase.invoke().collect { agents ->
                emit(
                    agents.map {
                        AddAgentViewStateItem(
                            agentId = it.id,
                            agentName = "${it.firstName} ${it.lastName}",
                            agentPfpUrl = it.picUrl
                        )
                    }
                )
            }
        }
        }

    private val pictureEntityListLiveData: LiveData<List<EstatePictureEntity>> = liveData(coroutineDispatcherProvider.io){
        if(selectedRealEstateId!=null){
            getPictureOfRealEstateUseCase.invoke(selectedRealEstateId).collect {
                emit(it)
            }
        }
    }

    private val mediatorLiveData = MediatorLiveData<List<RealEstatePictureViewStateItem>>()
    init {
        mediatorLiveData.addSource(pictureEntityListLiveData) { pictureListFromRealEstateId -> combine(pictureListFromRealEstateId, tempPictureListItemLiveData.value)}
        mediatorLiveData.addSource(tempPictureListItemLiveData) { tempListOfPicture -> combine(pictureEntityListLiveData.value, tempListOfPicture)}
    }

    private fun combine(pictureListFromRealEstate: List<EstatePictureEntity>?, tempListOfPicture: List<RealEstatePictureViewStateItem>?) {

        val listOfLoadedPicture = tempListOfPicture ?: emptyList()
        var listFromRealEstate:List<RealEstatePictureViewStateItem> = emptyList()

        if(pictureListFromRealEstate!=null && pictureListFromRealEstate.isNotEmpty()){

            listFromRealEstate = pictureListFromRealEstate.map {

            RealEstatePictureViewStateItem(
                realEstateId = selectedRealEstateId!!,
                pictureUri = Uri.parse(it.path),
                realEstatePictureName = it.name,
                isStored = true,
                isEdited = false,
                toDelete = false,
                onRealEstatePictureClicked = {
                    Log.e("MyViewModel", "Error occurred while doing something")
                                             },
                onRealEstatePictureLongPress = {
                    selectedPicture = Uri.parse(it.path)
                    notifyPictureToDelete()
                },
                onRealEstatePictureDeleteClicked = {
                    deletePictureSelected()
                },
                onRealEstatePictureDeleteLongPress = {
                    selectedPicture = Uri.parse(it.path)
                    notifyPictureToDelete()
                },
                onRealEstatePictureNameClicked = {
                    selectedPicture = Uri.parse(it.path)
                    notifyPictureIsEdited()
                }
            )

            }
        }

//Update behavior of AddPictureBar
        if(listOfLoadedPicture.isEmpty()) {realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.UpdateBarMessage(
                RealEstateAddOrEditPictureBarViewState(
                    noPictureTextViewVisibility = View.VISIBLE,
                    barText = NativeText.Resource(R.string.select_methode),
                    barIconTip = null
                )
            )
        }else{realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.UpdateBarMessage(
                RealEstateAddOrEditPictureBarViewState(
                    noPictureTextViewVisibility = View.GONE,
                    barText = NativeText.Resource(R.string.x_picture_selected),
                    barIconTip = intToBitmap(listOfLoadedPicture.size)
                )
            )
            }

        mediatorLiveData.value = (listOfLoadedPicture + listFromRealEstate).sortedBy{it.pictureUri}.reversed()

    }

    private fun deletePictureSelected() {

        val picList = tempPictureListItemLiveData.value?.toMutableList() ?: mutableListOf()

        repeat(picList.count { it.toDelete })
        {
            val index = picList.indexOfFirst { it.toDelete }
            if (index != -1) {
                val picture = picList[index]
                picList.removeAt(index)
                tempPictureListItemLiveData.value = picList

            }
        }

        selectedPicture=null
    }

    private fun notifyPictureIsEdited() {

        val picList = tempPictureListItemLiveData.value?.toMutableList() ?: mutableListOf()

        val index = picList.indexOfFirst { it.pictureUri == selectedPicture }
        if (index != -1) {
           val picture = picList[index]
           picList.removeAt(index)
            picList.add(
                RealEstatePictureViewStateItem(
                    realEstateId = null,
                    pictureUri = picture.pictureUri,
                    realEstatePictureName = picture.realEstatePictureName,
                    isStored = false,
                    isEdited = !picture.isEdited,
                    toDelete =false,
                    onRealEstatePictureClicked = {
                        Log.e("MyViewModel", "Error occurred while doing something")
                    },
                    onRealEstatePictureLongPress = {
                        selectedPicture = picture.pictureUri
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureDeleteClicked = {
                        deletePictureSelected()
                    },
                    onRealEstatePictureDeleteLongPress = {
                        selectedPicture = picture.pictureUri
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureNameClicked = {
                        selectedPicture = picture.pictureUri
                        notifyPictureIsEdited()
                    }
                ) )

            tempPictureListItemLiveData.value = picList

            if(!picList.any { it.isEdited }) {
                realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.CloseEditTextToChangePictureName
            }else{realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.ShowEditTextToChangePictureName}
        }
    }

    val realEstatePicturesLiveData: LiveData<List<RealEstatePictureViewStateItem>>
           get() = mediatorLiveData

    fun onCameraPermissionResult(hasCameraPermission: Boolean?) {
        if(hasCameraPermission == true) realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.LaunchActivityPhotoCapture
        else realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(
            R.string.error_camera_permission))
    }

    fun onCloseFragmentClicked() {
        realEstateAddFragSingleLiveEvent.value= RealEstateAddOrEditEvent.CloseFragment
    }

    fun onAddPictureFromCameraSelected() {
        if(permissionChecker.hasCameraPermission()) realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.LaunchActivityPhotoCapture
        else realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.RequestCameraPermission
    }

    fun onPickPicturesFromGallerySelected() {
        realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.LaunchActivityPickVisualMedia
    }

    fun onLaunchCameraInterfaceResult(result: ActivityResult, imageUri: Uri?) {
        when (result.resultCode){
            Activity.RESULT_OK -> addPicturesToRv(listOf(imageUri!!))
            Activity.RESULT_CANCELED -> realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(
                R.string.no_picture_selected))
        }
    }

    fun onPickVisualMediaResult(uris: List<Uri>?) {
        if (uris!=null && uris.isNotEmpty()) {addPicturesToRv(uris)}
        else {realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(
            R.string.no_picture_selected))}
    }

    private fun addPicturesToRv(listOfImageUri: List<Uri>) {
        val previousPic = tempPictureListItemLiveData.value?.toMutableList() ?: mutableListOf()
        listOfImageUri.forEach {
            previousPic.add(
                RealEstatePictureViewStateItem(
                    realEstateId = null,
                    pictureUri = it,
                    realEstatePictureName = getNextItemName(),
                    isStored = false,
                    isEdited = false,
                    toDelete =false,
                    onRealEstatePictureClicked = {
                        Log.e("MyViewModel", "Error occurred while doing something")
                                                 },
                    onRealEstatePictureLongPress = {
                        selectedPicture = it
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureDeleteClicked = {
                        deletePictureSelected()
                    },
                    onRealEstatePictureDeleteLongPress = {
                        selectedPicture = it
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureNameClicked = {
                        selectedPicture = it
                        notifyPictureIsEdited()
                    }
                ) )
        }


        tempPictureListItemLiveData.value = previousPic
    }

    private fun getNextItemName(): String {
        val pictureName:String
        if(deletedItemIndices.isEmpty()) {
            pictureName = "PIC$itemIndex"
            itemIndex++
        }else{
            pictureName ="PIC${deletedItemIndices[0]}"
        }

        return pictureName

    }

    fun onCloseAddPictureClicked() {
        itemIndex = 1
        deletedItemIndices.clear()
        tempPictureListItemLiveData.value = emptyList()
    }

    fun onNewNameForPicProvided(nameForPic: String) {

    if(nameForPic.length>1){

        val picList = tempPictureListItemLiveData.value?.toMutableList() ?: mutableListOf()

        repeat(picList.count { it.isEdited })
        {
        val index = picList.indexOfFirst { it.isEdited }
        if (index != -1) {
            val picture = picList[index]
            picList.removeAt(index)
            picList.add(
                RealEstatePictureViewStateItem(
                    realEstateId = null,
                    pictureUri = picture.pictureUri,
                    realEstatePictureName = nameForPic,
                    isStored = false,
                    isEdited = false,
                    toDelete =false,
                    onRealEstatePictureClicked = {
                        Log.e("MyViewModel", "Error occurred while doing something")
                    },
                    onRealEstatePictureLongPress = {
                        selectedPicture = picture.pictureUri
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureDeleteClicked = {
                        deletePictureSelected()
                    },
                    onRealEstatePictureDeleteLongPress = {
                        selectedPicture = picture.pictureUri
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureNameClicked = {
                        selectedPicture = picture.pictureUri
                        notifyPictureIsEdited()
                    }
                ) )
            tempPictureListItemLiveData.value = picList
            selectedPicture=null
        }
        }

    }else {
        notifyPictureIsEdited()
        realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(
        R.string.name_too_short))

    }
    }

    private fun notifyPictureToDelete() {

        val picList = tempPictureListItemLiveData.value?.toMutableList() ?: mutableListOf()

        val index = picList.indexOfFirst { it.pictureUri == selectedPicture }
        if (index != -1) {
            val picture = picList[index]
            picList.removeAt(index)
            picList.add(
                RealEstatePictureViewStateItem(
                    realEstateId = null,
                    pictureUri = picture.pictureUri,
                    realEstatePictureName = picture.realEstatePictureName,
                    isStored = false,
                    isEdited = false,
                    toDelete =!picture.toDelete,
                    onRealEstatePictureClicked = {
                        Log.e("MyViewModel", "Error occurred while doing something")
                    },
                    onRealEstatePictureLongPress = {
                        selectedPicture = picture.pictureUri
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureDeleteClicked = {
                        deletePictureSelected()
                    },
                    onRealEstatePictureDeleteLongPress = {
                        selectedPicture = picture.pictureUri
                        notifyPictureToDelete()
                    },
                    onRealEstatePictureNameClicked = {
                        selectedPicture = picture.pictureUri
                        notifyPictureIsEdited()
                    }
                ) )

            tempPictureListItemLiveData.value = picList
        }
    }

    fun onAgentSelected(agentId: Long) {
        realEstateAddOrEditViewState.estateAgentId = agentId
    }

    fun onSaveRealEstateClicked() {

        val viewPagerInfos = realEstateRepository.realEstateViewPagerInfosStateItem
        val picList = tempPictureListItemLiveData.value
        var realEstateIdCreated:Long? = null

        if(allFieldsNonNull(viewPagerInfos) && realEstateAddOrEditViewState.estateAgentId!=null) {
        viewModelScope.launch(coroutineDispatcherProvider.io) {

            val realEstateAddress = Address(
                street = viewPagerInfos.street!!,
                city = viewPagerInfos.city!!,
                state = viewPagerInfos.state!!,
                zipCode = viewPagerInfos.zipcode!!
            )

            val realEstateCoordinate = getCoordinateRealEstateUseCase.invoke(realEstateAddress)

            if(realEstateCoordinate!=null){

            val thumbnail = getThumbnailRealEstateUseCase.invoke(realEstateCoordinate)

            if(thumbnail!=null){
            realEstateIdCreated = insertRealEstateUseCase.invoke(
                RealEstateEntity(
                    estateAgentId = realEstateAddOrEditViewState.estateAgentId!!,
                    propertyType = realEstateAddOrEditViewState.propertyType ?: "Unknown",
                    price = viewPagerInfos.price ?: 0.0,
                    surfaceArea = viewPagerInfos.surfaceArea ?: 0.0,
                    numberOfRooms = viewPagerInfos.numberOfRooms ?: 0,
                    numberOfBedrooms = viewPagerInfos.numberOfBedrooms ?: 0,
                    description = realEstateAddOrEditViewState.description ?: "No Description.",
                    thumbnail = thumbnail,
                    address = realEstateAddress,
                    coordinates= "${realEstateCoordinate.latitude},${realEstateCoordinate.longitude}",
                    pointsOfInterest = realEstateAddOrEditViewState.pointsOfInterest ?: ArrayList(),
                    marketEntryDate = realEstateAddOrEditViewState.marketEntryDate ?: Date(),
                    saleDate = realEstateAddOrEditViewState.saleDate ?: Date()
                )
            )}}

            //PictureEntities to Room

            if (picList!=null && realEstateIdCreated!=null && picList.isNotEmpty()){
                storeEstatePicturesUseCase.invoke(picList, realEstateIdCreated)
            }

            val success = insertEstatePictureUserCase.invoke(
               realEstateRepository.estatePicturesListEntityMutableStateFlow.value!!
            )

            withContext(coroutineDispatcherProvider.main) {
                realEstateAddFragSingleLiveEvent.value = if (success) {
                    RealEstateAddOrEditEvent.CloseFragment
                } else {
                    RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(R.string.cant_insert_real_estate))
                }
            }

        }

    }else{
        realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(
        R.string.please_provide_all_infos))}
    }

    fun onTypeOfPropertySelected(typeOfProperty: String) {
        realEstateAddOrEditViewState.propertyType = typeOfProperty
    }

    fun onChipSelected(listOfTypeSelected: List<String>) {
    realEstateAddOrEditViewState.pointsOfInterest = ArrayList(listOfTypeSelected)
    }

    fun onDescriptionChanged(description: String) {
        realEstateAddOrEditViewState.description = description
    }

    fun onEntryDateSelected(entryDate: String) {
        realEstateAddOrEditViewState.marketEntryDate = toDateFormat(entryDate)
    }

    fun onSaleDateSelected(saleDate: String) {
        realEstateAddOrEditViewState.saleDate = toDateFormat(saleDate)
    }

    fun notifyFragmentNav() {
    sharedRepository.fragmentStateFlow.value = NavigationEvent.AddOrEditRealEstateFragment
    }

    val realEstateAddFragSingleLiveEvent = SingleLiveEvent<RealEstateAddOrEditEvent>()

}