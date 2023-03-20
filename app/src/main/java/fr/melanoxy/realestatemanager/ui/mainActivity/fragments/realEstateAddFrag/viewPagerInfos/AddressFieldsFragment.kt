package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddFrag.viewPagerInfos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentAddressFieldsBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class AddressFieldsFragment : Fragment(R.layout.fragment_address_fields) {

    private val binding by viewBinding { FragmentAddressFieldsBinding.bind(it) }
    //private val viewModel by viewModels<RealEstateAddViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}