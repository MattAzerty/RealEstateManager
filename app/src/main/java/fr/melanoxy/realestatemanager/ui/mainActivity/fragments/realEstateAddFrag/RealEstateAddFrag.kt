package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateAddBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag.viewPagerInfos.MyPagerAdapter
import fr.melanoxy.realestatemanager.ui.utils.viewBinding
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class RealEstateAddFrag  : Fragment(R.layout.fragment_real_estate_add) {

    private val binding by viewBinding { FragmentRealEstateAddBinding.bind(it) }
    private val viewModel by viewModels<RealEstateAddViewModel>()
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var arrowRotationForward: Animation
    private lateinit var arrowRotationBackward: Animation
    private var isOpen = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
        arrowRotationForward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_arrow_180_deg_forward)
        arrowRotationBackward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_arrow_180_deg_backward)

        setupViewPager()
        setupDatePickers()
        bindFab()

        /*viewModel.realEstateAddFragSingleLiveEvent.observe(viewLifecycleOwner) { tasksEvent ->
            when (tasksEvent) {
                TasksEvent.NavigateToAddTask -> navigationListener.displayAddTaskDialog()
            }.exhaustive
        }*/

        /*val adapter = MailsAdapter()
        binding.mailsRecyclerView.adapter = adapter
        viewModel.mailsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }*/
    }

    private fun bindFab() {
        binding.createNewRealEstateBarPictureAddPictureIcon.setOnClickListener {
            //viewModel.onAddPictureClicked()
            if(isOpen) collapse() else expand()
            animateFab()
        }
        binding.createNewRealEstateBarCloseIcon.setOnClickListener {
            collapse()
            animateFab()
        }
    }

    private fun expand() {
        TransitionManager.beginDelayedTransition(binding.createNewRealEstateBarPictureCardContainer, transition)
        binding.createNewRealEstateBarPictureCardContainer.layoutParams.width = 670
        //binding.searchBarCardContainer.setCardBackgroundColor(searchBarBackgroundColorFocused)
        binding.createNewRealEstateBarPictureAddPictureIcon.visibility = View.GONE
        binding.createNewRealEstateBarInputContainer.visibility = View.VISIBLE
    }

    private fun collapse() {
        TransitionManager.beginDelayedTransition(binding.createNewRealEstateBarPictureCardContainer, transition)
        binding.createNewRealEstateBarInputText.text = resources.getText(R.string.select_methode)
        binding.createNewRealEstateBarPictureCardContainer.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
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
        if (isOpen){//Case collapse
            binding.createNewRealEstateBarArrowIcon.startAnimation(arrowRotationBackward)
            binding.createNewRealEstateFabPictureSelection.startAnimation(fabClose)
            binding.createNewRealEstateFabPhotoSelection.startAnimation(fabClose)
            binding.createNewRealEstateFabPictureSelection.isClickable = false
            binding.createNewRealEstateFabPhotoSelection.isClickable = false

            isOpen=false
        }else {//Case expand
            binding.createNewRealEstateBarArrowIcon.startAnimation(arrowRotationForward)
            binding.createNewRealEstateFabPictureSelection.startAnimation(fabOpen)
            binding.createNewRealEstateFabPhotoSelection.startAnimation(fabOpen)
            binding.createNewRealEstateFabPictureSelection.isClickable = true
            binding.createNewRealEstateFabPhotoSelection.isClickable = true

            isOpen=true
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
                binding.createNewRealEstateButtonMarketEntryDate.text = "Market entry date: $formattedDate"
                binding.createNewRealEstateButtonMarketEntryDate.elevation=0F
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
                    binding.createNewRealEstateButtonSaleDate.elevation=0F
                }

            pickerSaleDate.show(childFragmentManager, "DATE_SALE_PICKER")
        }

    }

    private fun setupViewPager() {
        binding.createNewRealEstateViewPager.adapter = MyPagerAdapter(this)

        TabLayoutMediator(binding.createNewRealEstateTabLayout, binding.createNewRealEstateViewPager) { tab, position ->
            when (position) {
            0-> {tab.text = "Address"
                tab.icon = activity?.let { ContextCompat.getDrawable(it.applicationContext,R.drawable.vc_looks_one_white_24dp)
                }
            }
             1-> {tab.text = "Specifications"
                 tab.icon = activity?.let { ContextCompat.getDrawable(it.applicationContext,R.drawable.vc_looks_two_white_24dp)
                 }
             }
            }
        }.attach()
    }
    }
