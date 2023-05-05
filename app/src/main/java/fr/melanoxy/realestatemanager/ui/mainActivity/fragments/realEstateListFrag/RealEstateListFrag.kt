package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateListBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.MainEventListener
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv.RealEstateAdapter
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarSpinnerAdapter
import fr.melanoxy.realestatemanager.ui.utils.*

@AndroidEntryPoint
class RealEstateListFrag : Fragment(R.layout.fragment_real_estate_list) {

    private val binding by viewBinding { FragmentRealEstateListBinding.bind(it) }
    private val viewModel by viewModels<RealEstateListViewModel>()
    private lateinit var eventListener: MainEventListener
    private lateinit var activityResultForLocationPermission: ActivityResultLauncher<String>
    private var isExpanded=false

 //Fragment and mainActivity communicate with interface MainEventListener and callback mechanisms.

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as MainEventListener

        // Initialize the permission launcher for location permission
        activityResultForLocationPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
                viewModel.onLocationPermissionResult(permission)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.notifyFragmentNav()
        isTablet()
        bindFab()
        bindSearchBar()
        bindRecyclerView()

        viewModel.singleLiveRealEstateListEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is RealEstateListEvent.AddChip -> addChipToGroup(event.tag)
                is RealEstateListEvent.ReplaceCurrentFragment -> eventListener.switchMainPane(event.layoutId)
                is RealEstateListEvent.ReplaceSecondPaneFragment -> eventListener.switchSecondPane(event.layoutId)
                RealEstateListEvent.RequestLocationPermission -> activityResultForLocationPermission.launch(
                    LOCATION_PERMISSION
                )
                is RealEstateListEvent.DisplaySnackBarMessage -> eventListener.displaySnackBarMessage(
                    event.message.toCharSequence(requireContext())
                )
                RealEstateListEvent.ShowSaleDatePicker -> eventListener.showDatePicker(R.string.saleDate)
                RealEstateListEvent.ShowMarketEntryDatePicker -> eventListener.showDatePicker(R.string.entryDate)
                is RealEstateListEvent.ShowSearchBarKeyboard -> showSearchBarKeyboard(event.inputType)
                RealEstateListEvent.ShowPOISelector -> binding.searchBarChipGroupHvPoi.visibility = View.VISIBLE
            }.exhaustive
        }


    }

    private fun showSearchBarKeyboard(inputType: Int) {
        binding.searchBarInputText.inputType = inputType
        binding.searchBarInputText.requestFocus()
        eventListener.showKeyboard(binding.searchBarInputText)
    }

    private fun isTablet() {
        viewModel.isTabletLiveData.observe(viewLifecycleOwner) { isTablet ->
            if (isTablet) {//CASE IN TABLET MODE
                binding.fragRealEstateListClRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.searchBarRoot.visibility = View.GONE
                binding.searchBarChipGroupHv.visibility = View.GONE
                binding.listCityLandscape.visibility = View.GONE
                binding.realEstateListDivider1.dividerColor = ContextCompat.getColor(requireContext(), R.color.white)
                binding.fragRealEstateListRecyclerView.setMargins(left = null, right = null, top = 70)
                binding.fragRealEstateListRecyclerView.setPadding(0)
                binding.fragRealEstateListFabAdd.setMargins( top = 20)
                binding.fragRealEstateListFabMap.setMargins( top = 20)
                binding.fragRealEstateListFabMap.size= FloatingActionButton.SIZE_MINI
                binding.fragRealEstateListFabAdd.show()
                binding.fragRealEstateListFabMap.show()
            }else{//CASE VERTICAL
                binding.fragRealEstateListClRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.searchBarRoot.visibility = View.VISIBLE
                binding.searchBarChipGroupHv.visibility = View.VISIBLE
                binding.listCityLandscape.visibility = View.VISIBLE
                binding.realEstateListDivider1.dividerColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                binding.fragRealEstateListRecyclerView.setMargins(left = null, right = null, top = 100)
                binding.fragRealEstateListRecyclerView.setPadding(20)
                binding.fragRealEstateListFabAdd.setMargins( top = 0)
                binding.fragRealEstateListFabMap.setMargins( top = 0)
                binding.fragRealEstateListFabMap.size= FloatingActionButton.SIZE_NORMAL
                //Add filter tag if there is some:
                val currentChipsTag = viewModel.getCurrentChips()
                if(currentChipsTag.isNotEmpty()){
                    expand()
                    isExpanded = true
                    currentChipsTag.forEach { addChipToGroup(it) }
                }
            }
        }
    }


    private fun bindFab() {
        binding.fragRealEstateListFabAdd
            .setOnClickListener {viewModel.onFabButtonClicked(R.layout.fragment_real_estate_add)}
        binding.fragRealEstateListFabMap
            .setOnClickListener {viewModel.onFabButtonClicked(R.layout.fragment_real_estate_map)}
    }

    private fun bindSearchBar() {

        //LISTENER
        binding.searchBarSearchIcon.setOnClickListener {
            if (!isExpanded) {
                expand()
                isExpanded = true
            }
        }
        binding.searchBarBackIcon.setOnClickListener {
            if (isExpanded) {
                viewModel.onCollapseClicked()
                binding.searchBarChipGroup.removeAllViews()
                collapse()
                isExpanded = false
            }
        }
        binding.searchBarInputText.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                collapse()
                isExpanded = false
            }
        }
        binding.searchBarInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                collapse()
                isExpanded = false
                true
            } else {
                false
            }
        }

        //Dropdown menu
        val adapterForTags = RealEstateSearchBarSpinnerAdapter()
        binding.searchBarInputText.setAdapter(adapterForTags)
        viewModel.filterListLiveData.observe(viewLifecycleOwner) {
            adapterForTags.setData(it)
        }
        binding.searchBarInputText.setOnItemClickListener { _, v, position, _ ->
            adapterForTags.getItem(position)?.let {
                viewModel.onTagSelected(it.tag)
            }
        }
        //onInputTextForQuery selected
        binding.searchBarInputText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus && binding.searchBarInputText.text.isEmpty()){
                binding.searchBarInputText.clearFocus()
                binding.searchBarInputText.showDropDown()
            }
        }
        //Add chip on icon click
        binding.searchBarChipIcon.setOnClickListener {
            binding.searchBarInputText.clearFocus()
            viewModel.onAddChipCriteria(binding.searchBarInputText.text.trim().toString())
        }
        //Add chip on searchKeyboard click
        binding.searchBarInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchBarInputText.clearFocus()
                viewModel.onAddChipCriteria(binding.searchBarInputText.text.trim().toString())
                true
            } else {
                false
            }
        }
        //CHIPS for POI
        binding.chipClose.setOnClickListener {
            binding.searchBarChipGroupHvPoi.visibility = View.GONE
        }
        binding.chipPark.setOnClickListener {binding.searchBarInputText.text.append("Park|")}
        binding.chipSchool.setOnClickListener {binding.searchBarInputText.text.append("School|")}
        binding.chipStore.setOnClickListener {binding.searchBarInputText.text.append("Store|")}
        binding.chipMall.setOnClickListener {binding.searchBarInputText.text.append("Mall|")}
        binding.chipHospital.setOnClickListener {binding.searchBarInputText.text.append("Hospital|")}
        //DATE Picker
        viewModel.entryDatePickedLiveData.observe(viewLifecycleOwner) { event ->
            event.handleContent {
                binding.searchBarInputText.inputType = InputType.TYPE_CLASS_TEXT
                binding.searchBarInputText.text.append(it)
                viewModel.onAddChipCriteria(binding.searchBarInputText.text.trim().toString())
            }}
        viewModel.saleDatePickedLiveData.observe(viewLifecycleOwner) { event ->
            event.handleContent {
                binding.searchBarInputText.inputType = InputType.TYPE_CLASS_TEXT
                binding.searchBarInputText.text.append(it)
                viewModel.onAddChipCriteria(binding.searchBarInputText.text.trim().toString())
            }}
    }

    private fun expand() {
        binding.fragRealEstateListRecyclerView.setMargins(left = null, right = null, top = 220)
        binding.searchBarCardContainer.strokeWidth = 4
        binding.realEstateListDivider1.dividerColor = ContextCompat.getColor(requireContext(), R.color.black)
        TransitionManager.beginDelayedTransition(binding.searchBarCardContainer, transition)
        binding.searchBarCardContainer.layoutParams.width = 0
        binding.searchBarSearchIcon.visibility = View.GONE
        binding.searchBarInputContainer.visibility = View.VISIBLE

        binding.fragRealEstateListFabAdd.hide()
        binding.fragRealEstateListFabMap.hide()
    }

    private fun collapse() {
        binding.fragRealEstateListRecyclerView.setMargins(left = null, right = null, top = 100)
        binding.searchBarCardContainer.strokeWidth = 0
        binding.realEstateListDivider1.dividerColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        TransitionManager.beginDelayedTransition(binding.searchBarCardContainer, transition)
        binding.searchBarInputText.text.clear()
        binding.searchBarCardContainer.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.searchBarSearchIcon.visibility = View.VISIBLE
        binding.searchBarInputContainer.visibility = View.GONE

        binding.fragRealEstateListFabAdd.show()
        binding.fragRealEstateListFabMap.show()
    }


    private var transition: Transition = ChangeBounds().apply {
        duration = 300
        addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                if (isExpanded) {
                    //binding.searchBarInputText.requestFocus()
                    //eventListener.showKeyboard(binding.searchBarInputText)
                } else  {
                    eventListener.hideKeyboard(binding.searchBarInputText)
                }
            }

            // Unused functions.
            override fun onTransitionResume(transition: Transition?) = Unit
            override fun onTransitionPause(transition: Transition?) = Unit
            override fun onTransitionCancel(transition: Transition?) = Unit
            override fun onTransitionStart(transition: Transition?) = Unit
        })
    }

    private fun bindRecyclerView() {

       val adapter = RealEstateAdapter()
       binding.fragRealEstateListRecyclerView.adapter = adapter
       viewModel.realEstateListLiveData.observe(viewLifecycleOwner) {
           adapter.submitList(it)
       }
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(context)
        chip.text = tag
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        chip.setChipBackgroundColorResource(R.color.colorAccent)
        chip.closeIconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_background)
        chip.isChipIconVisible = false
        chip.isCloseIconVisible = true
        // necessary to get single selection working
        chip.isClickable = true
        chip.isCheckable = false
        binding.searchBarChipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener {
            binding.searchBarChipGroup.removeView(chip as View)
            updateChipList()
        }
        binding.searchBarInputText.text.clear()
        binding.searchBarChipGroupHvPoi.visibility = View.GONE
        eventListener.hideKeyboard(binding.searchBarInputText)
        updateChipList()
    }

    private fun updateChipList() {
        val tagList:MutableList<String> = mutableListOf()
        for (i in 0 until binding.searchBarChipGroup.childCount) {
            // Get the child view at the specified index
            val childView = binding.searchBarChipGroup.getChildAt(i)
            // Check if the child view is a Chip
            if (childView is Chip) {
                val chipName = childView.text.toString()
                tagList.add(chipName)
            }
        }
        viewModel.onChipGroupUpdate(tagList)
    }
}