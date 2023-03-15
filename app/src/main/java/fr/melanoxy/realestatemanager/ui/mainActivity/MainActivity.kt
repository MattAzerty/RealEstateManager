package fr.melanoxy.realestatemanager.ui.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.databinding.ActivityMainBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.realEstateListFrag.RealEstateListFrag
import fr.melanoxy.realestatemanager.ui.utils.viewBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }//TODO understand this
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.activityMainFrameLayoutContainerRealEstateList.id, RealEstateListFrag())
                .commitNow()
        }


    }

}//END of MainActivity