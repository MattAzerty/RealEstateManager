package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateLoanFrag

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateLoanBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.MainEventListener
import fr.melanoxy.realestatemanager.ui.utils.exhaustive
import fr.melanoxy.realestatemanager.ui.utils.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RealEstateLoanFrag : Fragment(R.layout.fragment_real_estate_loan) {

    private val binding by viewBinding { FragmentRealEstateLoanBinding.bind(it) }
    private val viewModel by viewModels<RealEstateLoanViewModel>()
    private lateinit var eventListener: MainEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_bottom)
        exitTransition = inflater.inflateTransition(R.transition.fade)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as MainEventListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.singleLiveRealEstateDetailsEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is RealEstateLoanEvent.DisplaySnackBarMessage -> eventListener.displaySnackBarMessage(
                    event.message.toCharSequence(requireContext())
                )
                is RealEstateLoanEvent.SwitchMainPane -> closeFrag()
            }.exhaustive
        }

        binding.fragmentLoanFabClose.setOnClickListener {
        viewModel.onCloseClicked()
        }

        binding.fragmentLoanFabCalculate.setOnClickListener {
            viewModel.onSimulateButtonClicked(RealEstateLoanSimulationParamStateItem(
                contributionAmount= binding.fragmentLoanFieldEtContribution.text.toString().toDoubleOrNull(),
                interestRate=binding.fragmentLoanFieldEtInterest.text.toString().toDoubleOrNull(),
                loanTerm= binding.fragmentLoanFieldEtLoanTerm.text.toString().toIntOrNull()
            ))
        }

        viewModel.resultReceivedLiveData.observe(viewLifecycleOwner) {
            binding.fragmentLoanResultBox.typeWrite(viewLifecycleOwner, it, 33L)
        }

        binding.fragmentLoanFieldEtLoanTerm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                eventListener.hideKeyboard(binding.fragmentLoanFieldEtLoanTerm)
                binding.fragmentLoanFieldEtLoanTerm.clearFocus()
                viewModel.onSimulateButtonClicked(RealEstateLoanSimulationParamStateItem(
                    contributionAmount= binding.fragmentLoanFieldEtContribution.text.toString().toDoubleOrNull(),
                    interestRate=binding.fragmentLoanFieldEtInterest.text.toString().toDoubleOrNull(),
                    loanTerm= binding.fragmentLoanFieldEtLoanTerm.text.toString().toIntOrNull()
                ))
                true
            } else {
                false
            }
        }

    }

    private fun closeFrag() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commit()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun TextView.typeWrite(lifecycleOwner: LifecycleOwner, text: String, intervalMs: Long) {
        this@typeWrite.text = ""
        lifecycleOwner.lifecycleScope.launch {
            repeat(text.length) {
                delay(intervalMs)
                this@typeWrite.text = text.take(it + 1)
            }
        }
    }

}