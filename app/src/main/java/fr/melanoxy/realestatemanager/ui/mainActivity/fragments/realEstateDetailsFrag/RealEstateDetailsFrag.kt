package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateDetailsBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.MainEventListener
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.RealEstateAddOrEditEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListEvent
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar.RealEstateSearchBarSpinnerAdapter
import fr.melanoxy.realestatemanager.ui.utils.exhaustive
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateDetailsFrag : Fragment(R.layout.fragment_real_estate_details) {

    private val binding by viewBinding { FragmentRealEstateDetailsBinding.bind(it) }
    private val viewModel by viewModels<RealEstateDetailsViewModel>()
    private lateinit var eventListener: MainEventListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as MainEventListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindSearchBarForTabletMode()

        viewModel.singleLiveRealEstateDetailsEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is RealEstateDetailsEvent.AddChip -> addChipToGroup(event.tag)
                is RealEstateDetailsEvent.DisplaySnackBarMessage -> eventListener.displaySnackBarMessage(
                    event.message.toCharSequence(requireContext())
                )
            }.exhaustive
        }

        }

    private fun bindSearchBarForTabletMode() {

        //Dropdown menu
        val adapterForTags = RealEstateSearchBarSpinnerAdapter()
        adapterForTags.setData(viewModel.getTags())
        binding.searchBarInputText.setAdapter(adapterForTags)
        binding.searchBarInputText.setOnItemClickListener { _, v, position, _ ->
            binding.searchBarInputText.requestFocus()
            eventListener.showkeyboard(binding.searchBarInputText)
            adapterForTags.getItem(position)?.let {
                    viewModel.onTagSelected(it.id)
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
        binding.searchBarInputText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus && binding.searchBarInputText.text.isEmpty()){
                binding.searchBarInputText.clearFocus()
                binding.searchBarInputText.showDropDown()
            }
        }

        binding.searchBarChipGroup.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            val tagList:MutableList<String> = mutableListOf()
            if(checkedIds.isNotEmpty()){
            checkedIds.forEach {
                val checkedChip = binding.root.findViewById<Chip>(it)
                val chipName = checkedChip.text.toString()
                tagList.add(chipName)
            }}
            viewModel.onChipSelected(tagList)
        }

    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(context)
        chip.text = tag
        chip.setChipBackgroundColorResource(R.color.colorAccent)
        chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_background)
        chip.isChipIconVisible = false
        chip.isCloseIconVisible = true
        // necessary to get single selection working
        chip.isClickable = true
        chip.isCheckable = false
        binding.searchBarChipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener { binding.searchBarChipGroup.removeView(chip as View) }
        binding.searchBarInputText.text.clear()
        eventListener.hideKeyboard(binding.searchBarInputText)
    }

    }