package fr.melanoxy.realestatemanager.ui.mainActivity

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.ActivityMainBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.RealEstateAddOrEditFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag.RealEstateDetailsFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateLoanFrag.RealEstateLoanFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag.RealEstateMapFrag
import fr.melanoxy.realestatemanager.ui.utils.viewBinding
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainEventListener {

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val containerMainId = binding.activityMainFrameLayoutContainerRealEstateList.id
        val containerDetailsId = binding.mainFrameLayoutContainerDetails?.id
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateListFrag())
                .commit()
        }
        if (containerDetailsId != null && supportFragmentManager.findFragmentById(containerDetailsId) == null) {
            supportFragmentManager.beginTransaction()
                .replace(containerDetailsId, RealEstateDetailsFrag())
                .commit()
        }
        //To avoid duplicated DetailsFragment or mapFragment

        if (containerDetailsId != null && supportFragmentManager.findFragmentById(containerMainId) is RealEstateDetailsFrag ||
                    supportFragmentManager.findFragmentById(containerMainId) is RealEstateMapFrag) {
            supportFragmentManager.beginTransaction()
                .replace(containerMainId, RealEstateListFrag())
                .commit()
        }
    }

    override fun displaySnackBarMessage(message: CharSequence) {
        Snackbar.make(binding.mainCl, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun hideKeyboard(view: View) {
        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun showKeyboard(view: View) {
        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun showDatePicker(type:Int) {

        val pickerEntryDate = MaterialDatePicker
            .Builder
            .datePicker()
            .setTheme(R.style.MyDatePickerStyle)
            .setTitleText(type)
            .build()

        pickerEntryDate.addOnPositiveButtonClickListener { selectedDate ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(selectedDate))
            viewModel.onDateSelected(type, formattedDate)
        }

        pickerEntryDate.show(supportFragmentManager, "DATE_PICKER")
    }

    override fun switchMainPane(id: Int) {
        val fragTransaction = supportFragmentManager.beginTransaction()
        when(id){
            R.layout.fragment_real_estate_list -> fragTransaction.replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateListFrag())
            R.layout.fragment_real_estate_add -> fragTransaction.replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateAddOrEditFrag())
            R.layout.fragment_real_estate_map -> fragTransaction.replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateMapFrag())
            R.layout.fragment_real_estate_details -> {
                fragTransaction.replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateDetailsFrag())
                fragTransaction.addToBackStack("map")//TODO
            }
            R.layout.fragment_real_estate_loan -> fragTransaction.replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateLoanFrag())
        }
        fragTransaction.commit()
    }

    override fun switchSecondPane(id: Int) {
        val fragTransaction = supportFragmentManager.beginTransaction()
        when(id){
        R.layout.fragment_real_estate_map -> binding.mainFrameLayoutContainerDetails?.id?.let {
            fragTransaction.replace(
                it, RealEstateMapFrag())
        }
        R.layout.fragment_real_estate_details -> {
            binding.mainFrameLayoutContainerDetails?.id?.let {fragTransaction.replace(it, RealEstateDetailsFrag())}
            if(supportFragmentManager.findFragmentById(binding.activityMainFrameLayoutContainerRealEstateList.id) is RealEstateDetailsFrag)switchMainPane(R.layout.fragment_real_estate_list)
        }
        }
        fragTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(resources.getBoolean(R.bool.isTablet))
    }
}