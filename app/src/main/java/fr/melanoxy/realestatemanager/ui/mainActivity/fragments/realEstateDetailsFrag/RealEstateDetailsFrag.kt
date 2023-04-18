package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateDetailsBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.MainEventListener
import fr.melanoxy.realestatemanager.ui.mainActivity.NavigationEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstatePictureRv.RealEstatePictureAdapter
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarSpinnerAdapter
import fr.melanoxy.realestatemanager.ui.utils.exhaustive
import fr.melanoxy.realestatemanager.ui.utils.setMargins
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateDetailsFrag : Fragment(R.layout.fragment_real_estate_details) {

    private val binding by viewBinding { FragmentRealEstateDetailsBinding.bind(it) }
    private val viewModel by viewModels<RealEstateDetailsViewModel>()
    private lateinit var eventListener: MainEventListener
    private var isExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as MainEventListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isTabletLiveData.observe(viewLifecycleOwner) { isTablet ->
            when(isTablet){
                false ->  adaptDetailsView()
                true -> bindSearchBarForTabletMode()
            }
        }

        viewModel.singleLiveRealEstateDetailsEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is RealEstateDetailsEvent.AddChip -> addChipToGroup(event.tag)
                is RealEstateDetailsEvent.DisplaySnackBarMessage -> eventListener.displaySnackBarMessage(
                    event.message.toCharSequence(requireContext())
                )
                is RealEstateDetailsEvent.CloseFragment -> eventListener.switchMainPane(event.layoutId)
                RealEstateDetailsEvent.ShowSaleDatePicker -> eventListener.showDatePicker(R.string.saleDate)
                RealEstateDetailsEvent.ShowMarketEntryDatePicker -> eventListener.showDatePicker(R.string.entryDate)
                RealEstateDetailsEvent.ShowSearchBarKeyboard -> showSearchBarKeyboard()
                RealEstateDetailsEvent.ShowPOISelector -> binding.searchBarChipGroupHvPoi.visibility = View.VISIBLE
                RealEstateDetailsEvent.PopToBackStack -> requireActivity().supportFragmentManager.popBackStack()
            }.exhaustive
        }
        bindView()
        }

    private fun adaptDetailsView() {
        binding.realEstateDetailsClRoot.setMargins(top=20, left = 20)
        binding.realEstateDetailsCoordRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.onePaneLayout.visibility= View.VISIBLE
        binding.realEstateDetailsDivider1.setMargins(top=null)
        binding.searchBarCardContainer.setMargins(left = 0, right = 0)
        binding.searchBarCardContainer2.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.searchBarCardContainer2.elevation=0F
        binding.searchBarCardContainer2.strokeColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.searchBarCardContainer.strokeColor = ContextCompat.getColor(requireContext(), R.color.shadow)
        binding.searchBarDropdownMenu.setImageResource(R.drawable.vc_arrow_back_white_24dp)
        binding.searchBarChipIcon.setImageResource(R.drawable.vc_align_vertical_bottom_white_24dp)
        binding.searchBarInputText.setText("REAL ESTATE DETAILS ")
        binding.searchBarInputText.setTypeface(null, Typeface.BOLD)
        binding.searchBarInputText.gravity = Gravity.CENTER
        binding.searchBarInputText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.searchBarInputText.textSize = 20F
        binding.searchBarInputText.isEnabled = false
        binding.searchBarInputText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))

        binding.searchBarDropdownMenu.setOnClickListener {
            viewModel.onCloseFragmentClicked()
        }

        binding.searchBarChipIcon.setOnClickListener {
            viewModel.onLoanButtonClicked()
        }

        binding.realEstateDetailsFabLocation.setOnClickListener {
            viewModel.onLocateRealEstateClicked()
        }
    }

    private fun bindView() {
        viewModel.detailsOfRealEstateStateItemLiveData.observe(viewLifecycleOwner) {
            //Recap
            binding.realEstateItemTvType.text = it.type
            binding.realEstateItemTvCity.text = it.city
            binding.realEstateItemTvPrice.text = it.price
            Glide.with(binding.realEstateItemIvMap)
                .load(it.thumbnail)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.realEstateItemIvMap)
            binding.realEstateItemIvMap
            //Pictures list
            val adapter = RealEstatePictureAdapter()
            binding.realEstateDetailsRecyclerViewPictures.adapter = adapter
            adapter.submitList(it.pictureList)
            //Description
            binding.realEstateDetailsTvDescriptionContent.text = it.description
            //Infos
            binding.realEstateDetailsInfosTvAgent.text = it.agentName
            binding.realEstateDetailsInfosTvSurface.text = it.surface
            binding.realEstateDetailsInfosTvRoom.text = it.room
            binding.realEstateDetailsInfosTvBedroom.text = it.bedroom
            binding.realEstateDetailsInfosTvPoi.text = it.nearPOI
            binding.realEstateDetailsInfosTvPosition.text = it.locationCoordinate

        }
    }

    private fun bindSearchBarForTabletMode() {
        binding.realEstateDetailsCoordRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.realEstateDetailsClRoot.setMargins(top=20, left = 20)
        binding.onePaneLayout.visibility= View.GONE
        binding.realEstateDetailsDivider1.setMargins(top=20)
        binding.searchBarCardContainer.setMargins(left = 200, right = 50)
        binding.searchBarCardContainer.strokeColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.searchBarCardContainer2.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        binding.searchBarCardContainer2.strokeColor = ContextCompat.getColor(requireContext(), R.color.colorAccentDark)
        binding.searchBarDropdownMenu.setImageResource(R.drawable.vc_keyboard_arrow_down_white_24dp)
        binding.searchBarChipIcon.setImageResource(R.drawable.vc_manage_search_white_24dp)
        binding.searchBarInputText.text.clear()
        binding.searchBarInputText.setTypeface(null, Typeface.ITALIC)
        binding.searchBarInputText.gravity = Gravity.CENTER_VERTICAL
        binding.searchBarInputText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.searchBarInputText.textSize = 20F
        binding.searchBarInputText.isEnabled = true
        binding.searchBarInputText.background = ContextCompat.getDrawable(requireContext(),R.drawable.shape_edittext_rounded_corner)

        viewModel.fragmentNavigationLiveData.observe(viewLifecycleOwner) {event ->
            when (event) {
                NavigationEvent.RealEstateListFragment -> expand()
                NavigationEvent.AddOrEditRealEstateFragment -> collapse()
                NavigationEvent.RealEstateDetailsFragment -> closeMode()
                else -> {}
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

        //Dropdown icon click
        binding.searchBarDropdownMenu.setOnClickListener { binding.searchBarInputText.showDropDown() }
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
        binding.searchBarInputText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus && binding.searchBarInputText.text.isEmpty()){
                binding.searchBarInputText.clearFocus()
                binding.searchBarInputText.showDropDown()
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
                binding.searchBarInputText.text.append(it)
                viewModel.onAddChipCriteria(binding.searchBarInputText.text.trim().toString())
            }}
        viewModel.saleDatePickedLiveData.observe(viewLifecycleOwner) { event ->
            event.handleContent {
                binding.searchBarInputText.text.append(it)
                viewModel.onAddChipCriteria(binding.searchBarInputText.text.trim().toString())
            }}
    }

    private fun closeMode() {
        collapse()
        binding.searchBarCardContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        binding.searchBarCardContainer.strokeColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        binding.searchBarSearchIcon.setImageResource(R.drawable.vc_view_list_white_24dp)
        binding.searchBarSearchIcon.setOnClickListener {
            viewModel.onCloseFragmentClicked()
        }
    }

    private fun expand() {
        isExpanded=true
        binding.searchBarCardContainer.strokeColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.searchBarCardContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        TransitionManager.beginDelayedTransition(binding.searchBarCardContainer, transition)
        binding.searchBarChipGroup.visibility = View.VISIBLE
        binding.searchBarCardContainer.layoutParams.width = 0
        binding.searchBarCardContainer2.layoutParams.width = 700
        //binding.searchBarCardContainer.setCardBackgroundColor(searchBarBackgroundColorFocused)
        binding.searchBarSearchIcon.visibility = View.GONE
        binding.searchBarInputContainer.visibility = View.VISIBLE
    }

    private fun collapse() {
        isExpanded=false
        binding.searchBarCardContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        TransitionManager.beginDelayedTransition(binding.searchBarCardContainer2, transition)
        binding.searchBarCardContainer2.layoutParams.width = 50
        TransitionManager.beginDelayedTransition(binding.searchBarCardContainer, transition)
        binding.searchBarChipGroup.visibility = View.INVISIBLE
        binding.searchBarInputText.text.clear()
        binding.searchBarCardContainer.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        //binding.searchBarCardContainer.setCardBackgroundColor(searchBarBackgroundColor)
        binding.searchBarSearchIcon.visibility = View.VISIBLE
        binding.searchBarInputContainer.visibility = View.GONE
    }

    private var transition: Transition = ChangeBounds().apply {
        duration = 200
        addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                if (isExpanded) {
                    //
                } else  {
                    binding.searchBarCardContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.searchBarCardContainer.strokeColor = ContextCompat.getColor(requireContext(), R.color.black)
                }
            }

            // Unused functions.
            override fun onTransitionResume(transition: Transition?) = Unit
            override fun onTransitionPause(transition: Transition?) = Unit
            override fun onTransitionCancel(transition: Transition?) = Unit
            override fun onTransitionStart(transition: Transition?) = Unit
        })
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(context)
        chip.text = tag
        chip.setChipBackgroundColorResource(R.color.black)
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

    private fun showSearchBarKeyboard() {
    binding.searchBarInputText.requestFocus()
    eventListener.showKeyboard(binding.searchBarInputText)
    }
}