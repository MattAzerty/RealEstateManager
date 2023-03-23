package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.data.RealEstateRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.estateAgent.GetEstateAgentUseCase
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.estatePicture.GetPictureOfRealEstateUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.realEstateSpinners.AddAgentViewStateItem
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv.RealEstatePictureViewStateItem
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import fr.melanoxy.realestatemanager.ui.utils.intToBitmap
import javax.inject.Inject

@HiltViewModel
class RealEstateAddOrEditViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    realEstateRepository: RealEstateRepository,
    private val getPictureOfRealEstateUseCase: GetPictureOfRealEstateUseCase,
    private val getEstateAgentUseCase: GetEstateAgentUseCase
) : ViewModel() {

    private var itemIndex = 1
    private var deletedItemIndices: MutableList<Int> = mutableListOf()
    private var selectedPicture: Uri?=null

    private val selectedRealEstateId =realEstateRepository.selectedRealEstateIdMutableSharedFlow.value
    private val tempPictureListItemLiveData = MutableLiveData<List<RealEstatePictureViewStateItem>>()

    //TODO Change this to globalViewState
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
                RealEstateAddPictureBarViewState(
                    noPictureTextViewVisibility = View.VISIBLE,
                    barText = NativeText.Resource(R.string.select_methode),
                    barIconTip = null
                )
            )
        }else{realEstateAddFragSingleLiveEvent.value =
            RealEstateAddOrEditEvent.UpdateBarMessage(
                RealEstateAddPictureBarViewState(
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


    /*private var listPictureEntityOfRealEstate: List<EstatePictureEntity> = emptyList()
    private val selectedId =realEstateRepository.selectedRealEstateIdMutableSharedFlow.value

    init {
        if (selectedId!=null) {
            viewModelScope.launch(coroutineDispatcherProvider.io) {
                listPictureEntityOfRealEstate = getPictureEntityListOfRealEstate(selectedId)
            }
        }
    }


    private suspend fun getPictureEntityListOfRealEstate(selectedId: Long): List<EstatePictureEntity> {
        return withContext(coroutineDispatcherProvider.io) {
            getPictureOfRealEstateUseCase.invoke(selectedId).flatMapConcat { it.asFlow() }.toList()
        }
    }*/

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
        Log.e("MyViewModel", "agentId$agentId")
    }


    val realEstateAddFragSingleLiveEvent = SingleLiveEvent<RealEstateAddOrEditEvent>()

}