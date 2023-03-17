package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateAddBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateAddFrag  : Fragment(R.layout.fragment_real_estate_add) {

    private val binding by viewBinding { FragmentRealEstateAddBinding.bind(it) }
    private val viewModel by viewModels<RealEstateAddViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val adapter = MailsAdapter()
        binding.mailsRecyclerView.adapter = adapter
        viewModel.mailsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }*/
    }
}