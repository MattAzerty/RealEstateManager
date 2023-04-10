package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentAddressFieldsBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class AddressFieldsFragment : Fragment(R.layout.fragment_address_fields) {

    private val binding by viewBinding { FragmentAddressFieldsBinding.bind(it) }
    private val viewModel by viewModels<ViewPagerInfosViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.realEstateViewPagerInfosStateItemLiveData.observe(viewLifecycleOwner) {
            binding.fragAddressFieldEtStreet.setText(it.street)
            binding.fragAddressFieldEtCity.setText(it.city)
            binding.fragAddressFieldEtState.setText(it.state)
            binding.fragAddressFieldEtZipcode.setText(it.zipcode)
        }


        binding.fragAddressFieldEtStreet.doAfterTextChanged {
            if(!it.isNullOrEmpty())
            viewModel.onStreetFieldChanged(it.toString())
        }
        binding.fragAddressFieldEtCity.doAfterTextChanged {
            if(!it.isNullOrEmpty())
            viewModel.onCityFieldChanged(it.toString())
        }
        binding.fragAddressFieldEtState.doAfterTextChanged {
            if(!it.isNullOrEmpty())
            viewModel.onStateChanged(it.toString())
        }
        binding.fragAddressFieldEtZipcode.doAfterTextChanged {
            if(!it.isNullOrEmpty())
            viewModel.onZipcode(it.toString())
        }
    }
}