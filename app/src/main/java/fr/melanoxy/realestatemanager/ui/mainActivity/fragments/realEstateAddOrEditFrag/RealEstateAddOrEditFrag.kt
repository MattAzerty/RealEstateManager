package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateAddBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.MainEventListener
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.realEstateSpinners.AddAgentSpinnerAdapter
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos.ViewPagerInfosAdapter
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv.RealEstatePictureAdapter
import fr.melanoxy.realestatemanager.ui.utils.CAMERA_PERMISSION
import fr.melanoxy.realestatemanager.ui.utils.REAL_ESTATE_TYPES
import fr.melanoxy.realestatemanager.ui.utils.exhaustive
import fr.melanoxy.realestatemanager.ui.utils.viewBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class RealEstateAddOrEditFrag : Fragment(R.layout.fragment_real_estate_add) {

    private val binding by viewBinding { FragmentRealEstateAddBinding.bind(it) }
    private val viewModel by viewModels<RealEstateAddOrEditViewModel>()
    private lateinit var eventListener: MainEventListener
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var arrowRotationForward: Animation
    private lateinit var arrowRotationBackward: Animation
    private lateinit var activityResultForCamera: ActivityResultLauncher<Intent>
    private lateinit var activityResultPickMultipleMediaFromGallery: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var activityResultForCameraPermissions: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_bottom)
        exitTransition = inflater.inflateTransition(R.transition.fade)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        eventListener = context as MainEventListener
// Initialize the permission launcher for picture from Camera selected
        activityResultForCamera =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                viewModel.onLaunchCameraInterfaceResult(result, imageUri)
            }

// Initialize the permission launcher for Camera permission
        activityResultForCameraPermissions =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
                viewModel.onCameraPermissionResult(permission)
            }

// Registers a photo picker activity launcher (PickMultipleVisualMedia(5) -> you can restrict number pic to pick
// https://developer.android.com/training/data-storage/shared/photopicker?hl=fr#kotlin
        activityResultPickMultipleMediaFromGallery =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                viewModel.onPickVisualMediaResult(uris)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
        arrowRotationForward =
            AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_arrow_180_deg_forward)
        arrowRotationBackward =
            AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_arrow_180_deg_backward)

        setupViewPager()
        setupDatePickers()
        bindAutoCompleteText()
        bindChips()
        bindButtons()
        bindRv()
        bindTv()

        viewModel.realEstateAddFragSingleLiveEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                RealEstateAddOrEditEvent.CloseFragment -> closeFragment()
                RealEstateAddOrEditEvent.RequestCameraPermission -> activityResultForCameraPermissions.launch(
                    CAMERA_PERMISSION
                )
                RealEstateAddOrEditEvent.LaunchActivityPhotoCapture -> openCameraInterface()
                RealEstateAddOrEditEvent.LaunchActivityPickVisualMedia -> pickMedia()
                RealEstateAddOrEditEvent.ShowEditTextToChangePictureName -> changePictureName()
                RealEstateAddOrEditEvent.CloseEditTextToChangePictureName -> closeEditPictureName()
                is RealEstateAddOrEditEvent.UpdateBarMessage -> updateBarView(event.barState)
                is RealEstateAddOrEditEvent.DisplaySnackBarMessage -> eventListener.displaySnackBarMessage(
                    event.message.toCharSequence(requireContext())
                )
            }.exhaustive//TODO inline?
            /*
            Any? type. It is used to ensure that when using a when expression in Kotlin, all possible cases are handled, so that the code can be considered "exhaustive".
            In this case, calling .exhaustive after the when expression is not strictly necessary. However, in cases where there are more than two cases in the enum, or if more cases are added in the future, using exhaustive after the when expression can help catch potential bugs caused by missing cases.
            The exhaustive property extension function simply returns the Unit object, which does not have any functionality. It is included only to satisfy the requirement of having an expression that forces the when expression to be in an expression form, which allows the compiler to check if all possible cases are handled. The @Suppress("unused") annotation is used to suppress the warning that the function is never used.
             */
        }

    }

    private fun bindChips() {
        binding.createNewRealEstateChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
        viewModel.onChipSelected(checkedIds)
        }
    }

    private fun bindAutoCompleteText() {
        //Agent
        val adapterForAgent = AddAgentSpinnerAdapter()
        binding.createNewRealEstateAutoCompleteTextViewAgents.setAdapter(adapterForAgent)
        binding.createNewRealEstateAutoCompleteTextViewAgents.setOnItemClickListener { _, _, position, _ ->
            adapterForAgent.getItem(position)?.let {
                viewModel.onAgentSelected(it.agentId)
            }
        }
        viewModel.agentViewStateLiveData.observe(viewLifecycleOwner) { agentViewState ->
            adapterForAgent.setData(agentViewState)
            //Type
            val adapterForType =
                ArrayAdapter(requireContext(), R.layout.item_drop_down, REAL_ESTATE_TYPES)
            binding.createNewRealEstateAutoCompleteTextViewType.setAdapter(adapterForType)
            binding.createNewRealEstateAutoCompleteTextViewType.setOnItemClickListener { _, _, position, _ ->
                adapterForType.getItem(position)?.let{
                    viewModel.onTypeOfPropertySelected(it)
            }
            }
        }
    }

    private fun bindTv() {

        binding.createNewRealEstateTvDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                viewModel.onDescriptionChanged(s.toString())
            }
        })

        binding.createNewRealEstateTlChangePictureName.setEndIconOnClickListener {
            viewModel.onNewNameForPicProvided(binding.createNewRealEstateTvChangePictureName.text.toString().trim())
            closeEditPictureName()
        }
    }

    private fun changePictureName() {
        binding.createNewRealEstateTlChangePictureName.visibility= View.VISIBLE
        binding.createNewRealEstateTlChangePictureName.requestFocus()
    }

    private fun closeEditPictureName() {
        binding.createNewRealEstateTvChangePictureName.hideKeyboard()
        binding.createNewRealEstateTvChangePictureName.text?.clear()
        binding.createNewRealEstateTlChangePictureName.visibility = View.GONE
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }


    private fun updateBarView(barState: RealEstateAddOrEditPictureBarViewState) {
        val bitmap = barState.barIconTip ?: AppCompatResources.getDrawable(requireContext(), R.drawable.vc_keyboard_arrow_left_white_24dp)
            ?.toBitmap()
        binding.createNewRealEstateNoImage.visibility = barState.noPictureTextViewVisibility
        binding.createNewRealEstateBarArrowIcon.setImageBitmap(bitmap)
        binding.createNewRealEstateBarInputText.text = barState.barText.toCharSequence(requireContext())
    }

    private fun closeFragment() {
        //requireActivity().supportFragmentManager.popBackStackImmediate()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_main_FrameLayout_container_real_estate_list, RealEstateListFrag())
        transaction.commit()
    }

    private fun pickMedia() {
        activityResultPickMultipleMediaFromGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun saveImageFromUri(name: String, uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "$name.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return file.absolutePath
    }

    private fun bindRv() {
        val adapter = RealEstatePictureAdapter()
        binding.createNewRealEstateRecyclerView.adapter = adapter
        viewModel.realEstatePicturesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun openCameraInterface() {

        val values = ContentValues()

        imageUri =
            activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
// Create camera intent
        val cameraInterfaceIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraInterfaceIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        activityResultForCamera.launch(cameraInterfaceIntent)
    }

    private fun bindButtons() {
//Close addFrag
        binding.createNewRealEstateClose.setOnClickListener { viewModel.onCloseFragmentClicked() }
        binding.createNewRealEstateButtonCancel.setOnClickListener { viewModel.onCloseFragmentClicked() }
//Camera selected
        binding.createNewRealEstateFabPhotoSelection.setOnClickListener { viewModel.onAddPictureFromCameraSelected() }
//Gallery selected
        binding.createNewRealEstateFabGallerySelection.setOnClickListener { viewModel.onPickPicturesFromGallerySelected() }
//Add new picture(s)
        binding.createNewRealEstateBarPictureAddPictureIcon.setOnClickListener {
            val view = activity?.currentFocus
            view?.clearFocus()
            view?.hideKeyboard()
            //viewModel.onAddPictureClicked()
            if (isOpen) collapse() else expand()
            animateFab()
        }
//Close new picture(s)
        binding.createNewRealEstateBarCloseIcon.setOnClickListener {
            viewModel.onCloseAddPictureClicked()
            closeEditPictureName()
            collapse()
            animateFab()
        }

        binding.createNewRealEstateButtonSaveRealEstate.setOnClickListener {
            viewModel.onSaveRealEstateClicked()
        }

    }

    private fun expand() {
        TransitionManager.beginDelayedTransition(
            binding.createNewRealEstateBarPictureCardContainer,
            transition
        )
        binding.createNewRealEstateBarPictureCardContainer.layoutParams.width = 670
//binding.searchBarCardContainer.setCardBackgroundColor(searchBarBackgroundColorFocused)
        binding.createNewRealEstateBarPictureAddPictureIcon.visibility = View.GONE
        binding.createNewRealEstateBarInputContainer.visibility = View.VISIBLE
    }

    private fun collapse() {
        TransitionManager.beginDelayedTransition(
            binding.createNewRealEstateBarPictureCardContainer,
            transition
        )
        binding.createNewRealEstateBarInputText.text = resources.getText(R.string.select_methode)
        binding.createNewRealEstateBarPictureCardContainer.layoutParams.width =
            ViewGroup.LayoutParams.WRAP_CONTENT
//binding.searchBarCardContainer.setCardBackgroundColor(searchBarBackgroundColor)
        binding.createNewRealEstateBarPictureAddPictureIcon.visibility = View.VISIBLE
        binding.createNewRealEstateBarInputContainer.visibility = View.GONE

    }

    private var transition: Transition = ChangeBounds().apply {
        duration = 200
        addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                //animateFab()
            }

            // Unused functions.
            override fun onTransitionResume(transition: Transition?) = Unit
            override fun onTransitionPause(transition: Transition?) = Unit
            override fun onTransitionCancel(transition: Transition?) = Unit
            override fun onTransitionStart(transition: Transition?) = Unit
        })
    }

    private fun animateFab() {
        if (isOpen) {//Case collapse
            //binding.createNewRealEstateBarArrowIcon.startAnimation(arrowRotationBackward)
            binding.createNewRealEstateFabGallerySelection.startAnimation(fabClose)
            binding.createNewRealEstateFabPhotoSelection.startAnimation(fabClose)
            binding.createNewRealEstateFabGallerySelection.isClickable = false
            binding.createNewRealEstateFabPhotoSelection.isClickable = false

            isOpen = false
        } else {//Case expand
            //binding.createNewRealEstateBarArrowIcon.startAnimation(arrowRotationForward)
            binding.createNewRealEstateFabGallerySelection.startAnimation(fabOpen)
            binding.createNewRealEstateFabPhotoSelection.startAnimation(fabOpen)
            binding.createNewRealEstateFabGallerySelection.isClickable = true
            binding.createNewRealEstateFabPhotoSelection.isClickable = true

            isOpen = true
        }
    }

    private fun setupDatePickers() {

        binding.createNewRealEstateButtonMarketEntryDate.setOnClickListener {
            val pickerEntryDate = MaterialDatePicker
                .Builder
                .datePicker()
                .setTheme(R.style.MyDatePickerStyle)
                .setTitleText("Select market entry date")
                .build()

            pickerEntryDate.addOnPositiveButtonClickListener { selectedDate ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(selectedDate))
                binding.createNewRealEstateButtonMarketEntryDate.text =
                    "Market entry date: $formattedDate"
                binding.createNewRealEstateButtonMarketEntryDate.elevation = 0F
                viewModel.onEntryDateSelected(formattedDate)
            }

            pickerEntryDate.show(childFragmentManager, "DATE_ENTRY_PICKER")
        }

        binding.createNewRealEstateButtonSaleDate.setOnClickListener {
            val pickerSaleDate = MaterialDatePicker
                .Builder
                .datePicker()
                .setTheme(R.style.MyDatePickerStyle)
                .setTitleText("Select sale date")
                .build()

            pickerSaleDate.addOnPositiveButtonClickListener { selectedDate ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(selectedDate))
                binding.createNewRealEstateButtonSaleDate.text = "Sale entry date: $formattedDate"
                binding.createNewRealEstateButtonSaleDate.elevation = 0F
                viewModel.onSaleDateSelected(formattedDate)
            }

            pickerSaleDate.show(childFragmentManager, "DATE_SALE_PICKER")
        }

    }

    private fun setupViewPager() {
        binding.createNewRealEstateViewPager.adapter = ViewPagerInfosAdapter(this)

        TabLayoutMediator(
            binding.createNewRealEstateTabLayout,
            binding.createNewRealEstateViewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Address"
                    tab.icon = activity?.let {
                        ContextCompat.getDrawable(
                            it.applicationContext,
                            R.drawable.vc_looks_one_white_24dp
                        )
                    }
                }
                1 -> {
                    tab.text = "Specifications"
                    tab.icon = activity?.let {
                        ContextCompat.getDrawable(
                            it.applicationContext,
                            R.drawable.vc_looks_two_white_24dp
                        )
                    }
                }
            }
        }.attach()
    }
}
