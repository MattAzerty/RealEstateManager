package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateLoanFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.data.repositories.RealEstateRepository
import fr.melanoxy.realestatemanager.data.repositories.SharedRepository
import fr.melanoxy.realestatemanager.data.utils.CoroutineDispatcherProvider
import fr.melanoxy.realestatemanager.domain.realEstate.GetRealEstateFromIdUseCase
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.viewPagerInfos.RealEstateViewPagerInfosStateItem
import fr.melanoxy.realestatemanager.ui.utils.NativeText
import fr.melanoxy.realestatemanager.ui.utils.SingleLiveEvent
import fr.melanoxy.realestatemanager.ui.utils.allFieldsNonNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.pow
import kotlin.properties.Delegates

@HiltViewModel
class RealEstateLoanViewModel @Inject constructor(
    realEstateRepository: RealEstateRepository,
    private val sharedRepository: SharedRepository,
    private val getRealEstateFromIdUseCase: GetRealEstateFromIdUseCase,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    val resultReceivedLiveData: MutableLiveData<String> = MutableLiveData()
    private var realEstatePrice by Delegates.notNull<Double>()

    init {
        val id = realEstateRepository.selectedRealEstateIdStateFlow.value
        if(id!=null){
            viewModelScope.launch(coroutineDispatcherProvider.io) {
                getRealEstateFromIdUseCase.invoke(id).collect { entity ->
                    withContext(coroutineDispatcherProvider.main) {
                        realEstatePrice = entity.price
                    }
                }
            }

        }
    }
    val singleLiveRealEstateDetailsEvent = SingleLiveEvent<RealEstateLoanEvent>()

    fun onSimulateButtonClicked(loanInfos: RealEstateLoanSimulationParamStateItem) {
        if(allFieldsNonNull(loanInfos)){
            val contribution = loanInfos.contributionAmount!!
            val principal = realEstatePrice.minus(contribution)
            val interestRate = loanInfos.interestRate ?: 2.0
            val termYears = loanInfos.loanTerm ?: 15
            val termMonths = termYears.times(12)
            val monthlyInterestRate = (interestRate/100).div(12)
            val monthlyPayment = (principal * monthlyInterestRate) / (1 - (1 + monthlyInterestRate).pow(-termMonths))
            val totalPayment = monthlyPayment * termMonths
            val interest = totalPayment-(realEstatePrice-contribution)
            resultReceivedLiveData.value =
                "Loan Details:\n------------------------\nReal estate price:$$realEstatePrice\n"+
                "Contribution amount: $$contribution\nPrincipal amount: $$principal\n"+
                "Interest rate: $interestRate\nLoan term: $termYears years\n"+
                "Monthly payment: $${String.format("%.2f", monthlyPayment)}\n"+
                "Total payment: $${String.format("%.2f", totalPayment)}\n"+
                "Total interest: $${String.format("%.2f", interest)}"

        }
        else{singleLiveRealEstateDetailsEvent.value = RealEstateLoanEvent.DisplaySnackBarMessage(
            NativeText.Resource(
                R.string.please_provide_all_infos
            )
        )}
    }

    fun onCloseClicked() {
        if(sharedRepository.isTabletStateFlow.value) {
            singleLiveRealEstateDetailsEvent.value = RealEstateLoanEvent.SwitchMainPane(R.layout.fragment_real_estate_list)
        }else{singleLiveRealEstateDetailsEvent.value = RealEstateLoanEvent.SwitchMainPane(R.layout.fragment_real_estate_details)}
    }

}