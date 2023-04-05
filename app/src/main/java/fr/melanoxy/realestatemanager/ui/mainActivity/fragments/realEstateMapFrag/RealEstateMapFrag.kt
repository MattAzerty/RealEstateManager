package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateAddBinding
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateMapBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateMapFrag : Fragment(R.layout.fragment_real_estate_map),
    OnMarkerClickListener,
    OnInfoWindowClickListener,
    OnMapReadyCallback {

    private val binding by viewBinding { FragmentRealEstateMapBinding.bind(it) }
    private val viewModel by viewModels<RealEstateMapViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.googleMapView.onCreate(savedInstanceState)
        binding.googleMapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        binding.googleMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.googleMapView.onPause()
    }

    /*override fun onDestroy() {
        super.onDestroy()
        binding.googleMapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.googleMapView.onSaveInstanceState(outState)
    }*/

    override fun onMarkerClick(marker: Marker): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onInfoWindowClick(marker: Marker) {
        //TODO("Not yet implemented")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }
}