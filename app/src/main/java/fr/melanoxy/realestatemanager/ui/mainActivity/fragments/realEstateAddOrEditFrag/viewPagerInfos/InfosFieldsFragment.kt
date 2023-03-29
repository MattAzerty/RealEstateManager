package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentAddressFieldsBinding
import fr.melanoxy.realestatemanager.databinding.FragmentInfosFieldsBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class InfosFieldsFragment : Fragment(R.layout.fragment_infos_fields) {

    private val binding by viewBinding { FragmentInfosFieldsBinding.bind(it) }
    private val viewModel by viewModels<ViewPagerInfosViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragInfosFieldEtPrice.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrEmpty()) viewModel.onPriceFieldChanged(text.toString()) }
        binding.fragInfosFieldEtRoom.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrEmpty()) viewModel.onNumberOfRoomChanged(text.toString()) }
        binding.fragInfosFieldEtBedroom.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrEmpty()) viewModel.onNumberOfBedroomChanged(text.toString()) }
        binding.fragInfosFieldEtSurface.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrEmpty()) viewModel.onSurfaceFieldChanged(text.toString()) }

    }
}