package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateListBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.MainEventListener
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.RealEstateAddOrEditFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv.RealEstateAdapter
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateListFrag : Fragment(R.layout.fragment_real_estate_list) {

    private val binding by viewBinding { FragmentRealEstateListBinding.bind(it) }
    private val viewModel by viewModels<RealEstateListViewModel>()
    private lateinit var eventListener: MainEventListener
    private var isExpanded=false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as MainEventListener
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
                is RealEstateListEvent.ReplaceCurrentFragment -> switchFragment(event.fragmentId)
                else -> {//TODO: something went wrong
                }
            }
        }


    }

    private fun isTablet() {
        viewModel.isTabletLiveData.observe(viewLifecycleOwner) {
            if (it) {
                binding.searchBarRoot.visibility = View.GONE
                //binding.fragRealEstateListFabAdd.size = FloatingActionButton.SIZE_MINI
                binding.fragRealEstateListFabMap.size= FloatingActionButton.SIZE_MINI
            }else{
                binding.searchBarRoot.visibility = View.VISIBLE
                //binding.fragRealEstateListFabAdd.size = FloatingActionButton.SIZE_NORMAL
                binding.fragRealEstateListFabMap.size= FloatingActionButton.SIZE_NORMAL
            }
        }
    }

    private fun switchFragment(fragmentId: Int) {

        val transaction = parentFragmentManager.beginTransaction()

        when(fragmentId){
            R.id.frag_real_estate_list_fab_add -> transaction.replace(R.id.activity_main_FrameLayout_container_real_estate_list, RealEstateAddOrEditFrag())

        }
        //transaction.addToBackStack(null)
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }


    private fun bindFab() {

        binding.fragRealEstateListFabAdd
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_add)}//1000004
        binding.fragRealEstateListFabMap
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_map)}//1000003

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

    }

    private fun expand() {
        TransitionManager.beginDelayedTransition(binding.searchBarCardContainer, transition)
        binding.searchBarCardContainer.layoutParams.width = 0
        //binding.searchBarCardContainer.setCardBackgroundColor(searchBarBackgroundColorFocused)
        binding.searchBarSearchIcon.visibility = View.GONE
        binding.searchBarInputContainer.visibility = View.VISIBLE

        binding.fragRealEstateListFabAdd.hide()
        binding.fragRealEstateListFabMap.hide()
    }

    private fun collapse() {
        TransitionManager.beginDelayedTransition(binding.searchBarCardContainer, transition)
        binding.searchBarInputText.text.clear()
        binding.searchBarCardContainer.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        //binding.searchBarCardContainer.setCardBackgroundColor(searchBarBackgroundColor)
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
                    binding.searchBarInputText.requestFocus()
                    eventListener.showKeyboard(binding.searchBarInputText)
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
       viewModel.realEstatesLiveData.observe(viewLifecycleOwner) {
           adapter.submitList(it)
       }
    }
}