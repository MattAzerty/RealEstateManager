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
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateListBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag.RealEstateAddFrag
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateListFrag : Fragment(R.layout.fragment_real_estate_list) {

    private val binding by viewBinding { FragmentRealEstateListBinding.bind(it) }
    private val viewModel by viewModels<RealEstateListViewModel>()
    private var isExpanded=false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun switchFragment(fragmentId: Int) {

        val transaction = parentFragmentManager.beginTransaction()

        when(fragmentId){
            R.id.frag_real_estate_list_fab_add -> transaction.replace(R.id.activity_main_FrameLayout_container_real_estate_list, RealEstateAddFrag())

        }
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }


    private fun bindFab() {

        binding.fragRealEstateListFabAdd
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_add)}//1000004
        binding.fragRealEstateListFabEdit
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_edit)}//1000002
        binding.fragRealEstateListFabMap
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_map)}//1000003
        //binding.fragRealEstateListFabSearch
            //.setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_search)}//1000001

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
        binding.fragRealEstateListFabEdit.hide()
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
        binding.fragRealEstateListFabEdit.show()
        binding.fragRealEstateListFabMap.show()
    }


    private var transition: Transition = ChangeBounds().apply {
        duration = 300
        addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                if (isExpanded) {
                    binding.searchBarInputText.requestFocus()
                    showKeyboard(binding.searchBarInputText)
                } else  {
                    hideKeyboard(binding.searchBarInputText)
                }
            }

            // Unused functions.
            override fun onTransitionResume(transition: Transition?) = Unit
            override fun onTransitionPause(transition: Transition?) = Unit
            override fun onTransitionCancel(transition: Transition?) = Unit
            override fun onTransitionStart(transition: Transition?) = Unit
        })
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun bindRecyclerView() {
        //TODO("Not yet implemented")
        /*val adapter = MailsAdapter()
       binding.mailsRecyclerView.adapter = adapter
       viewModel.mailsLiveData.observe(viewLifecycleOwner) {
           adapter.submitList(it)
       }*/
    }
}