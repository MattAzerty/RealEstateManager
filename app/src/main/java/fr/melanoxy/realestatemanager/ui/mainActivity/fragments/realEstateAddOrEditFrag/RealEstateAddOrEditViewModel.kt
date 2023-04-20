package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.Address
import fr.melanoxy.realestatemanager.domain.estateAgent.GetEstateAgentUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.*
import fr.melanoxy.realestatemanager.domain.realEstate.*
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.GetRealEstateWithPicturesFromIdUseCase
import fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity.RealEstateWithPictureEntity
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.realEstateSpinners.AddAgentViewStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureViewStateItem
import fr.melanoxy.realestatemanager.ui.utils.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class RealEstateAddOrEditViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val realEstateRepository: RealEstateRepository,
    private val sharedRepository: SharedRepository,
    private val getRealEstateWithPicturesFromIdUseCase: GetRealEstateWithPicturesFromIdUseCase,
    private val getEstateAgentUseCase: GetEstateAgentUseCase,
    private val getCoordinateRealEstateUseCase: GetCoordinateRealEstateUseCase,
    private val getThumbnailRealEstateUseCase: GetThumbnailRealEstateUseCase,
    private val insertRealEstateUseCase: InsertRealEstateUseCase,
    private val updateRealEstateUseCase: UpdateRealEstateUseCase,
    private val updateEstatePictureUseCase: UpdateEstatePictureUseCase,
    private val storeEstatePicturesUseCase: StoreEstatePicturesUseCase,
    private val deleteEstatePictureUseCase: DeleteEstatePictureUseCase,
    private val insertEstatePictureUserCase: InsertEstatePictureUserCase,
) : ViewModel() {

    private val realEstateAddOrEditViewStateLiveData =
        MutableLiveData(RealEstateAddOrEditViewState())

    //For picture item modifications (delete/rename)
    private var pictureItemIndex = 1
    private var deletedPictureItemIndices: MutableList<Int> = mutableListOf()
    private var selectedPicture: Uri? = null

    //Selected RE from list (if not null -> editMode)
    private val selectedRealEstateId = realEstateRepository.selectedRealEstateIdStateFlow.value

    //Trigger from mainActivity for entryDate
    val entryDatePickedLiveData: LiveData<Event<String>> =
        sharedRepository.entryDatePickedChannelFromAddOrEdit.asLiveDataEvent(
            coroutineDispatcherProvider.io
        ) {
            emit(it)
        }

    //Trigger from mainActivity for saleDate
    val saleDatePickedLiveData: LiveData<Event<String>> =
        sharedRepository.saleDatePickedChannelFromAddOrEdit.asLiveDataEvent(
            coroutineDispatcherProvider.io
        ) {
            emit(it)
        }

    //For AgentSelectionSpinner
    val agentListViewStateLiveData: LiveData<List<AddAgentViewStateItem>> =
        liveData(coroutineDispatcherProvider.io) {
            getEstateAgentUseCase.invoke().collect { agents ->
                emit(agents.map {
                    AddAgentViewStateItem(
                        agentId = it.id,
                        agentName = "${it.firstName} ${it.lastName}",
                        agentPfpUrl = it.picUrl
                    )
                })
            }
        }
//list of all realEstate in Room if id selected (EditMode)
    private val realEstateWithPictureFromIdLiveData: LiveData<RealEstateWithPictureEntity> =
        liveData(coroutineDispatcherProvider.io) {
            if (selectedRealEstateId != null) {
                getRealEstateWithPicturesFromIdUseCase.invoke(selectedRealEstateId).collect {
                    emit(it)
                }
            }
        }

    private val mediatorLiveData = MediatorLiveData<RealEstateAddOrEditViewState?>()

    init {
        mediatorLiveData.addSource(realEstateWithPictureFromIdLiveData) { estateEntity ->
            combine(
                estateEntity, realEstateAddOrEditViewStateLiveData.value
            )
        }
        mediatorLiveData.addSource(realEstateAddOrEditViewStateLiveData) { estateViewStateTemp ->
            combine(
                realEstateWithPictureFromIdLiveData.value, estateViewStateTemp
            )
        }
    }

    private fun combine(
        estateEntity: RealEstateWithPictureEntity?,
        estateViewStateTemp: RealEstateAddOrEditViewState?
    ) {

        val pictureListOnView = (estateViewStateTemp?.pictureItemList
            ?: emptyList()).toMutableList()//From View (StateItem)
//Update behavior of AddPictureBarWidget
        if (pictureListOnView.isEmpty()) {
            realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.UpdateBarMessage(
                RealEstateAddOrEditPictureBarViewState(
                    noPictureTextViewVisibility = View.VISIBLE,
                    barText = NativeText.Resource(R.string.select_methode),
                    barIconTip = null
                )
            )
        } else {
            realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.UpdateBarMessage(
                RealEstateAddOrEditPictureBarViewState(
                    noPictureTextViewVisibility = View.GONE,
                    barText = NativeText.Resource(R.string.x_picture_selected),
                    barIconTip = intToBitmap(pictureListOnView.size)
                )
            )
        }

//Fulfill itemViewState If on EditMod.

        val itemViewState: RealEstateAddOrEditViewState?

        if (estateEntity != null && estateViewStateTemp?.estateAgentId == null) {

            itemViewState = RealEstateAddOrEditViewState(
                estateAgentId = estateEntity.realEstateEntity.estateAgentId,
                propertyType = estateEntity.realEstateEntity.propertyType,
                pointsOfInterest = estateEntity.realEstateEntity.pointsOfInterest,
                description = estateEntity.realEstateEntity.description,
                pictureItemList = estateEntity.estatePictureEntities.map {

                    RealEstatePictureViewStateItem(realEstateId = selectedRealEstateId!!,
                        pictureUri = Uri.parse("file://${it.path}"),
                        realEstatePictureName = it.name,
                        isStored = true,
                        isEdited = false,
                        toDelete = false,
                        onRealEstatePictureLongPress = {
                            selectedPicture = Uri.parse("file://${it.path}")
                            notifyPictureToDelete()
                        },
                        onRealEstatePictureDeleteClicked = {
                            deletePictureSelected()
                        },
                        onRealEstatePictureDeleteLongPress = {
                            selectedPicture = Uri.parse("file://${it.path}")
                            notifyPictureToDelete()
                        },
                        onRealEstatePictureNameClicked = {
                            selectedPicture = Uri.parse("file://${it.path}")
                            notifyPictureIsEdited()
                        })
                }.sortedBy{it.pictureUri}.reversed(),
                marketEntryDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                    Date(
                        estateEntity.realEstateEntity.marketEntryDate.time
                    )
                ),
                saleDate = estateEntity.realEstateEntity.saleDate?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        Date(it.time)
                    )
                }
            )
            realEstateAddOrEditViewStateLiveData.value = itemViewState

        } else itemViewState = estateViewStateTemp

        mediatorLiveData.value = itemViewState

    }


    private fun deletePictureSelected() {
        //Get currentList of picture on View else emptyList
        val picList = realEstateAddOrEditViewStateLiveData.value?.pictureItemList?.toMutableList()
            ?: mutableListOf()

        repeat(picList.count { it.toDelete }) {
            val index = picList.indexOfFirst { it.toDelete }
            if (index != -1) {
                picList.removeAt(index)
                realEstateAddOrEditViewStateLiveData.value =
                    updateItemWith(RealEstateAddOrEditViewState(pictureItemList = picList))
            }
        }
        selectedPicture = null
    }

    private fun updateItemWith(newItem: RealEstateAddOrEditViewState): RealEstateAddOrEditViewState {
        val oldItem = realEstateAddOrEditViewStateLiveData.value

        return RealEstateAddOrEditViewState(
            estateAgentId = newItem.estateAgentId ?: oldItem?.estateAgentId,
            propertyType = newItem.propertyType ?: oldItem?.propertyType,
            pointsOfInterest = newItem.pointsOfInterest ?: oldItem?.pointsOfInterest,
            description = newItem.description ?: oldItem?.description,
            pictureItemList = newItem.pictureItemList?.sortedBy{it.pictureUri}?.reversed() ?: oldItem?.pictureItemList,
            marketEntryDate = newItem.marketEntryDate ?: oldItem?.marketEntryDate,
            saleDate = newItem.saleDate ?: oldItem?.saleDate
        )
    }

    private fun notifyPictureIsEdited() {
        //Get current listOfPictures in the view
        val picList = realEstateAddOrEditViewStateLiveData.value?.pictureItemList?.toMutableList()
            ?: mutableListOf()
        val index = picList.indexOfFirst { it.pictureUri == selectedPicture }
        if (index != -1) {
            val picture = picList[index]
            picList.removeAt(index)
            picList.add(RealEstatePictureViewStateItem(realEstateId = null,
                pictureUri = picture.pictureUri,
                realEstatePictureName = picture.realEstatePictureName,
                isStored = picture.isStored,
                isEdited = !picture.isEdited,
                toDelete = false,
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
                }))

            realEstateAddOrEditViewStateLiveData.value =
                updateItemWith(RealEstateAddOrEditViewState(pictureItemList = picList))

            if (!picList.any { it.isEdited }) {
                realEstateAddFragSingleLiveEvent.value =
                    RealEstateAddOrEditEvent.CloseEditTextToChangePictureName
            } else {
                realEstateAddFragSingleLiveEvent.value =
                    RealEstateAddOrEditEvent.ShowEditTextToChangePictureName
            }
        }
    }

    val realEstateItemLiveData: LiveData<RealEstateAddOrEditViewState?>
        get() = mediatorLiveData

    fun onCameraPermissionResult(hasCameraPermission: Boolean?) {
        if (hasCameraPermission == true) realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.LaunchActivityPhotoCapture
        else realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.DisplaySnackBarMessage(
                NativeText.Resource(
                    R.string.error_camera_permission
                )
            )
    }

    fun onCloseFragmentClicked() {
        realEstateRepository.setSelectedRealEstateId(null)
        realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.CloseFragment
    }

    fun onAddPictureFromCameraSelected() {
        if (permissionChecker.hasCameraPermission()) realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.LaunchActivityPhotoCapture
        else realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.RequestCameraPermission
    }

    fun onPickPicturesFromGallerySelected() {
        realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.LaunchActivityPickVisualMedia
    }

    fun onLaunchCameraInterfaceResult(result: ActivityResult, imageUri: Uri?) {
        when (result.resultCode) {
            Activity.RESULT_OK -> addPicturesToRv(listOf(imageUri!!))
            Activity.RESULT_CANCELED -> realEstateAddFragSingleLiveEvent.value =
                RealEstateAddOrEditEvent.DisplaySnackBarMessage(
                    NativeText.Resource(
                        R.string.no_picture_selected
                    )
                )
        }
    }

    fun onPickVisualMediaResult(uris: List<Uri>?) {
        if (uris != null && uris.isNotEmpty()) {
            addPicturesToRv(uris)
        } else {
            realEstateAddFragSingleLiveEvent.value =
                RealEstateAddOrEditEvent.DisplaySnackBarMessage(
                    NativeText.Resource(
                        R.string.no_picture_selected
                    )
                )
        }
    }

    private fun addPicturesToRv(listOfImageUri: List<Uri>) {
        val picList = realEstateAddOrEditViewStateLiveData.value?.pictureItemList?.toMutableList()
            ?: mutableListOf()
        listOfImageUri.forEach {
            picList.add(RealEstatePictureViewStateItem(realEstateId = null,
                pictureUri = it,
                realEstatePictureName = getNextItemName(),
                isStored = false,
                isEdited = false,
                toDelete = false,
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
                }))
        }

        realEstateAddOrEditViewStateLiveData.value =
            updateItemWith(RealEstateAddOrEditViewState(pictureItemList = picList))
    }

    private fun getNextItemName(): String {
        val pictureName: String
        if (deletedPictureItemIndices.isEmpty()) {
            pictureName = "PIC$pictureItemIndex"
            pictureItemIndex++
        } else {
            pictureName = "PIC${deletedPictureItemIndices[0]}"
        }

        return pictureName
    }

    fun onCloseAddPictureClicked() {
        pictureItemIndex = 1
        deletedPictureItemIndices.clear()
        realEstateAddOrEditViewStateLiveData.value =
            updateItemWith(RealEstateAddOrEditViewState(pictureItemList = mutableListOf()))
    }

    fun onNewNameForPicProvided(nameForPic: String) {

        if (nameForPic.length > 1) {

            val picList =
                realEstateAddOrEditViewStateLiveData.value?.pictureItemList?.toMutableList()
                    ?: mutableListOf()

            repeat(picList.count { it.isEdited }) {
                val index = picList.indexOfFirst { it.isEdited }
                if (index != -1) {
                    val picture = picList[index]
                    picList.removeAt(index)
                    picList.add(RealEstatePictureViewStateItem(realEstateId = null,
                        pictureUri = picture.pictureUri,
                        realEstatePictureName = nameForPic,
                        isStored = false,
                        isEdited = false,
                        toDelete = false,
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
                        }))
                    realEstateAddOrEditViewStateLiveData.value =
                        updateItemWith(RealEstateAddOrEditViewState(pictureItemList = picList))
                    selectedPicture = null
                }
            }

        } else {
            notifyPictureIsEdited()
            realEstateAddFragSingleLiveEvent.value =
                RealEstateAddOrEditEvent.DisplaySnackBarMessage(
                    NativeText.Resource(
                        R.string.name_too_short
                    )
                )

        }
    }

    private fun notifyPictureToDelete() {

        val picList = realEstateAddOrEditViewStateLiveData.value?.pictureItemList?.toMutableList()
            ?: mutableListOf()
        //Todo add the edit list of pictures
        val index = picList.indexOfFirst { it.pictureUri == selectedPicture }
        if (index != -1) {
            val picture = picList[index]
            picList.removeAt(index)
            picList.add(RealEstatePictureViewStateItem(realEstateId = null,
                pictureUri = picture.pictureUri,
                realEstatePictureName = picture.realEstatePictureName,
                isStored = false,
                isEdited = false,
                toDelete = !picture.toDelete,
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
                }))

            realEstateAddOrEditViewStateLiveData.value =
                updateItemWith(RealEstateAddOrEditViewState(pictureItemList = picList))
        }
    }

    fun onAgentSelected(agentId: Long) {
        realEstateAddOrEditViewStateLiveData.value =
            updateItemWith(RealEstateAddOrEditViewState(estateAgentId = agentId))
    }

    fun onSaveRealEstateClicked() {

        //get input data from viewPager (Address + Specifications)
        val viewPagerInfos = realEstateRepository.realEstateViewPagerInfosStateItem
        //get pictureList currently loaded on view
        val picList = realEstateAddOrEditViewStateLiveData.value?.pictureItemList
        //insert (null) or update(!=null) in room
        val oldRealEstateWithPictureEntity = realEstateWithPictureFromIdLiveData.value
        var realEstateIdToSave: Long? = selectedRealEstateId

        if ((allFieldsNonNull(viewPagerInfos) && realEstateAddOrEditViewStateLiveData.value?.estateAgentId != null) || realEstateIdToSave!=null) {
            viewModelScope.launch(coroutineDispatcherProvider.io) {

                val realEstateAddress = Address(
                    street = viewPagerInfos.street!!,
                    city = viewPagerInfos.city!!,
                    state = viewPagerInfos.state!!,
                    zipCode = viewPagerInfos.zipcode!!
                )

                var thumbnail: Bitmap? = null
                var realEstateCoordinate: LatLng? = null

                //If in edit mode and Address the same, skip the Api calls for coordinate and thumbnail
                if(realEstateIdToSave==null || realEstateAddress!=oldRealEstateWithPictureEntity?.realEstateEntity?.address ) {
                    //get Coordinate from Address
                    realEstateCoordinate = getCoordinateRealEstateUseCase.invoke(realEstateAddress)
                    if (realEstateCoordinate != null) {
                        //get thumbnail Bitmap from coordinate
                        thumbnail = getThumbnailRealEstateUseCase.invoke(realEstateCoordinate)
                    }
                }

                //Then insert the realEstateEntity in Room:
                        if (realEstateIdToSave==null && thumbnail != null && realEstateCoordinate!=null) {//Insert realEstate entity in room
                            realEstateIdToSave = insertRealEstateUseCase.invoke(RealEstateEntity(
                                estateAgentId = realEstateAddOrEditViewStateLiveData.value?.estateAgentId!!,
                                propertyType = realEstateAddOrEditViewStateLiveData.value?.propertyType
                                    ?: "Unknown",
                                price = viewPagerInfos.price ?: 0.0,
                                surfaceArea = viewPagerInfos.surfaceArea ?: 0.0,
                                numberOfRooms = viewPagerInfos.numberOfRooms ?: 0,
                                numberOfBedrooms = viewPagerInfos.numberOfBedrooms ?: 0,
                                description = realEstateAddOrEditViewStateLiveData.value?.description
                                    ?: "No Description.",
                                thumbnail = thumbnail,
                                numberOfPictures = picList?.size ?:0,
                                address = realEstateAddress,
                                coordinates = "${realEstateCoordinate.latitude},${realEstateCoordinate.longitude}",
                                pointsOfInterest = if(realEstateAddOrEditViewStateLiveData.value?.pointsOfInterest?.isEmpty() != true) realEstateAddOrEditViewStateLiveData.value?.pointsOfInterest else null,
                                marketEntryDate = realEstateAddOrEditViewStateLiveData.value?.marketEntryDate?.let {
                                    toDateFormat(it)
                                } ?: Date(),
                                saleDate = realEstateAddOrEditViewStateLiveData.value?.saleDate?.let {
                                    toDateFormat(it)
                                } ))
                        }else{//or Update It...

                            val lat = realEstateCoordinate?.latitude ?: oldRealEstateWithPictureEntity?.realEstateEntity!!.coordinates.split(",")[0]
                            val long = realEstateCoordinate?.longitude ?: oldRealEstateWithPictureEntity?.realEstateEntity!!.coordinates.split(",")[1]

                            updateRealEstateUseCase.invoke(RealEstateEntity(
                                id= realEstateWithPictureFromIdLiveData.value!!.realEstateEntity.id,
                                estateAgentId = realEstateAddOrEditViewStateLiveData.value?.estateAgentId!!,
                                propertyType = realEstateAddOrEditViewStateLiveData.value?.propertyType
                                    ?: "Unknown",
                                price = viewPagerInfos.price ?: realEstateWithPictureFromIdLiveData.value!!.realEstateEntity.price,
                                surfaceArea = viewPagerInfos.surfaceArea ?: realEstateWithPictureFromIdLiveData.value!!.realEstateEntity.surfaceArea,
                                numberOfRooms = viewPagerInfos.numberOfRooms ?: realEstateWithPictureFromIdLiveData.value!!.realEstateEntity.numberOfRooms,
                                numberOfBedrooms = viewPagerInfos.numberOfBedrooms ?: realEstateWithPictureFromIdLiveData.value!!.realEstateEntity.numberOfBedrooms,
                                description = realEstateAddOrEditViewStateLiveData.value?.description
                                    ?: "No Description.",
                                thumbnail = thumbnail ?: oldRealEstateWithPictureEntity?.realEstateEntity!!.thumbnail,
                                numberOfPictures = picList?.size ?:0,
                                address = realEstateAddress,
                                coordinates = "$lat,$long",
                                pointsOfInterest = if(realEstateAddOrEditViewStateLiveData.value?.pointsOfInterest?.isEmpty() != true) realEstateAddOrEditViewStateLiveData.value?.pointsOfInterest else null,
                                marketEntryDate = realEstateAddOrEditViewStateLiveData.value?.marketEntryDate?.let {
                                    toDateFormat(it)
                                } ?: Date(),
                                saleDate = realEstateAddOrEditViewStateLiveData.value?.saleDate?.let {
                                    toDateFormat(it)
                                } )
                            )
                        }

                //PictureEntities to Room and storage

                if (picList != null && realEstateIdToSave != null) {

                    var pictureListAlreadyStored: List<RealEstatePictureViewStateItem> = emptyList()

                    if(oldRealEstateWithPictureEntity?.estatePictureEntities!=null) {//Case only when in editMode

                        pictureListAlreadyStored = picList.filter { pictureFromView ->
                            oldRealEstateWithPictureEntity.estatePictureEntities.any { oldEntity ->
                                Uri.parse("file://${oldEntity.path}") == pictureFromView.pictureUri
                            }
                        }

                        val picturesToDelete =  oldRealEstateWithPictureEntity.estatePictureEntities.filterNot { oldEntity ->
                            picList.any { pictureFromView ->
                                Uri.parse("file://${oldEntity.path}") == pictureFromView.pictureUri
                            }
                        }
                        //Delete picture(s) in room and storage
                        if(picturesToDelete.isNotEmpty()) deleteEstatePictureUseCase.invoke(picturesToDelete)
                        //if the tag isStored as been lost (false), an update on room is needed
                        val picturesToUpdate = pictureListAlreadyStored.filter { !it.isStored }
                        //Update picture(s) in room.
                        if(picturesToUpdate.isNotEmpty()) updateEstatePictureUseCase.invoke(picturesToUpdate.map { stateItem ->
                            EstatePictureEntity(
                           id = oldRealEstateWithPictureEntity.estatePictureEntities.find { it.path == stateItem.pictureUri.path }?.id!!,
                           realEstateId = oldRealEstateWithPictureEntity.estatePictureEntities.find { it.path == stateItem.pictureUri.path }?.realEstateId!!,
                           name= stateItem.realEstatePictureName,
                           path= stateItem.pictureUri.path!!
                            )
                        })
                    }

                    val pictureToSave = picList - pictureListAlreadyStored.toSet()

                    //SaveEntityToRoom and storage
                    if(pictureToSave.isNotEmpty()) storeEstatePicturesUseCase.invoke(pictureToSave, realEstateIdToSave)

                }

                val success = if(realEstateRepository.estatePicturesListEntityMutableStateFlow.value!=null) {insertEstatePictureUserCase.invoke(
                    realEstateRepository.estatePicturesListEntityMutableStateFlow.value!!)}else true


                withContext(coroutineDispatcherProvider.main) {
                    realEstateRepository.setSelectedRealEstateId(null)
                    realEstateAddFragSingleLiveEvent.value = if (success) {
                        RealEstateAddOrEditEvent.CloseFragmentWithMessage(NativeText.Resource(R.string.add_success))
                    } else {
                        RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(R.string.cant_insert_real_estate))
                    }
                }

            }

        } else {
            realEstateAddFragSingleLiveEvent.value =
                RealEstateAddOrEditEvent.DisplaySnackBarMessage(
                    NativeText.Resource(
                        R.string.please_provide_all_infos
                    )
                )
        }
    }

    fun onTypeOfPropertySelected(typeOfProperty: String) {
        realEstateAddOrEditViewStateLiveData.value =
            updateItemWith(RealEstateAddOrEditViewState(propertyType = typeOfProperty))
    }

    fun onChipSelected(listOfTypeSelected: List<String>) {
        realEstateAddOrEditViewStateLiveData.value = updateItemWith(
            RealEstateAddOrEditViewState(
                pointsOfInterest = ArrayList(
                    listOfTypeSelected
                )
            )
        )
    }

    fun onDescriptionChanged(description: String) {
        /*realEstateAddOrEditViewStateLiveData.value =
            updateItemWith(RealEstateAddOrEditViewState(description = description))*/
        realEstateAddOrEditViewStateLiveData.value?.description = description//To not enter in infinite loop when StateItem is updated and watched in the same time.
    }

    fun onEntryDateSelected(entryDate: String) {
        realEstateAddOrEditViewStateLiveData.value =
            updateItemWith(RealEstateAddOrEditViewState(marketEntryDate = entryDate))
    }

    fun onSaleDateSelected(saleDate: String) {
        realEstateAddOrEditViewStateLiveData.value =
            updateItemWith(RealEstateAddOrEditViewState(saleDate = saleDate))
    }

    fun notifyFragmentNav() {
        sharedRepository.fragmentStateFlow.value = NavigationEvent.AddOrEditRealEstateFragment
    }

    val realEstateAddFragSingleLiveEvent = SingleLiveEvent<RealEstateAddOrEditEvent>()

}