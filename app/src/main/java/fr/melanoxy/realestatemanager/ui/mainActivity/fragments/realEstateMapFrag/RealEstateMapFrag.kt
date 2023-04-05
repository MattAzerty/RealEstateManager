package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateMapBinding
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateMapFrag : Fragment(R.layout.fragment_real_estate_map),
    OnMarkerClickListener,
    OnInfoWindowClickListener,
    OnMapReadyCallback {

    private val binding by viewBinding { FragmentRealEstateMapBinding.bind(it) }
    private val viewModel by viewModels<RealEstateMapViewModel>()
    private var myPositionMaker: Marker? = null
    private val realEstatesMarker: List<Marker> = ArrayList()

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

        viewModel.userPositionLiveData.observe(viewLifecycleOwner) {
            if(it!=null){
                val latLng = LatLng(it.latitude, it.longitude)
                addMyLocationMarker(googleMap, latLng) //place on the map the userCurrentPosition

            }
        }
    }

    private fun addMyLocationMarker(googleMap: GoogleMap, latLng: LatLng) {
        if (myPositionMaker != null) {
            myPositionMaker!!.remove() //To avoid duplicate marker
        }

        myPositionMaker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng).icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
                .snippet("(lat:" + latLng.latitude + ", long:" + latLng.longitude + ")")
                .title(getString(R.string.my_position_marker))
        )

        // and move the map's camera to the same location with a zoom of 15.
        if (realEstatesMarker.isEmpty()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }
}