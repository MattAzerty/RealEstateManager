package fr.melanoxy.realestatemanager.ui.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.ActivityMainBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag.RealEstateDetailsFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListFrag
import fr.melanoxy.realestatemanager.ui.utils.viewBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainEventListener {

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateListFrag())
                .commitNow()
        }
        val containerDetailsId = binding.mainFrameLayoutContainerDetails?.id
        if (containerDetailsId != null && supportFragmentManager.findFragmentById(containerDetailsId) == null) {
            supportFragmentManager.beginTransaction()
                .replace(containerDetailsId, RealEstateDetailsFrag())
                .commitNow()
        }


    }

    override fun displaySnackBarMessage(message: CharSequence) {
        Snackbar.make(binding.mainCl, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(resources.getBoolean(R.bool.isTablet))
    }

}