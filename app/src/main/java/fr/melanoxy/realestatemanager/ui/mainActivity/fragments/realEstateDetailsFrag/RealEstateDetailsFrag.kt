package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateDetailsBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateDetailsFrag : Fragment(R.layout.fragment_real_estate_details) {

    private val binding by viewBinding { FragmentRealEstateDetailsBinding.bind(it) }
    private val viewModel by viewModels<RealEstateDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        }
    }