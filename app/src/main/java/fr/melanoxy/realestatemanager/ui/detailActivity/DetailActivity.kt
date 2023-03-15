package fr.melanoxy.realestatemanager.ui.detailActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.databinding.ActivityDetailBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private val binding by viewBinding { ActivityDetailBinding.inflate(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.activityDetailsFrameLayoutContainer.id, DetailFrag())
                .commitNow()
        }

    }
}