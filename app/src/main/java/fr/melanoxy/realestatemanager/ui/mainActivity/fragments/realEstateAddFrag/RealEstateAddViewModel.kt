package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.PermissionChecker
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RealEstateAddViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker
) : ViewModel() {

    fun onCameraPermissionResult(hasCameraPermission: Boolean?) {
        if(hasCameraPermission == true) realEstateAddFragSingleLiveEvent.value = RealEstateAddEvent.LaunchActivityPhotoCapture
        else realEstateAddFragSingleLiveEvent.value = RealEstateAddEvent.DisplaySnackBarMessage(NativeText.Resource(
            R.string.error_camera_permission))
    }

    fun onCloseFragmentClicked() {
        realEstateAddFragSingleLiveEvent.value= RealEstateAddEvent.CloseFragment
    }

    fun onAddPictureFromCameraSelected() {
        if(permissionChecker.hasCameraPermission()) realEstateAddFragSingleLiveEvent.value = RealEstateAddEvent.LaunchActivityPhotoCapture
        else realEstateAddFragSingleLiveEvent.value = RealEstateAddEvent.RequestCameraPermission
    }

    fun onLaunchCameraInterfaceResult(result: ActivityResult, imageUri: Uri?) {
        when (result.resultCode){
            Activity.RESULT_OK ->{
                imageUri
                realEstateAddFragSingleLiveEvent.value = RealEstateAddEvent.DisplaySnackBarMessage(NativeText.Resource(
                R.string.picture_acquired))}
            Activity.RESULT_CANCELED -> realEstateAddFragSingleLiveEvent.value = RealEstateAddEvent.DisplaySnackBarMessage(NativeText.Resource(
                R.string.picture_not_acquired))
        }
    }

    val realEstateAddFragSingleLiveEvent = SingleLiveEvent<RealEstateAddEvent>()

}