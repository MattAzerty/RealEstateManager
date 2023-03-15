package fr.melanoxy.realestatemanager.ui.mainActivity.realEstateListFrag

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

        /*val adapter = MailsAdapter()
        binding.mailsRecyclerView.adapter = adapter
        viewModel.mailsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }*/
    }
}