package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.data.RealEstateRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.estatePicture.GetPictureOfRealEstateUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv.RealEstatePictureViewStateItem
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateAddOrEditViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    realEstateRepository: RealEstateRepository,
    private val getPictureOfRealEstateUseCase: GetPictureOfRealEstateUseCase
) : ViewModel() {

    private val selectedId =realEstateRepository.selectedRealEstateIdMutableSharedFlow.value
    private val tempPictureListItemLiveData = MutableLiveData<List<RealEstatePictureViewStateItem>>()

    private val pictureEntityListLiveData: LiveData<List<EstatePictureEntity>> = liveData(coroutineDispatcherProvider.io){
        if(selectedId!=null){
            getPictureOfRealEstateUseCase.invoke(selectedId).collect {
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
                realEstateId = selectedId!!,
                pictureUri = Uri.parse(it.path),
                realEstatePictureName = it.name,
                isStored = true
            ) { joke() }//TODO change this


            }

        }

        mediatorLiveData.value = listOfLoadedPicture + listFromRealEstate

    }

    private fun joke(){}

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

    fun onLaunchCameraInterfaceResult(result: ActivityResult, imageUri: Uri?) {
        when (result.resultCode){
            Activity.RESULT_OK -> addPictureToRv(imageUri!!)
            Activity.RESULT_CANCELED -> realEstateAddFragSingleLiveEvent.value = RealEstateAddOrEditEvent.DisplaySnackBarMessage(NativeText.Resource(
                R.string.no_picture_selected))
        }
    }

    private fun addPictureToRv(imageUri: Uri) {
        val previousPic = tempPictureListItemLiveData.value?.toMutableList() ?: mutableListOf()
        previousPic.add(
            RealEstatePictureViewStateItem(
                realEstateId = null,
                pictureUri = imageUri,
                realEstatePictureName = "Picture",
                isStored = false
            ) { joke() }//TODO change this

        )

        tempPictureListItemLiveData.value = previousPic
    }

    val realEstateAddFragSingleLiveEvent = SingleLiveEvent<RealEstateAddOrEditEvent>()

}