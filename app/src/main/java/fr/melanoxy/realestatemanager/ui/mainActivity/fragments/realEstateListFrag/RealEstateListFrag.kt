package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateListBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateListFrag : Fragment(R.layout.fragment_real_estate_list) {

    private val binding by viewBinding { FragmentRealEstateListBinding.bind(it) }
    private val viewModel by viewModels<RealEstateListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindFab()
        bindRecyclerView()


    }

    private fun bindFab() {

        binding.fragRealEstateListFabAdd
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_add)}//1000004
        binding.fragRealEstateListFabEdit
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_edit)}//1000002
        binding.fragRealEstateListFabMap
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_map)}//1000003
        binding.fragRealEstateListFabSearch
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.frag_real_estate_list_fab_search)}//1000001


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