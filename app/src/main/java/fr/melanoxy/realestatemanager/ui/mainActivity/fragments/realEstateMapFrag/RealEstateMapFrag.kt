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
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateDetailsFrag.RealEstateDetailsFrag
import fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.RealEstateListFrag
import fr.melanoxy.realestatemanager.ui.utils.exhaustive
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateMapFrag : Fragment(R.layout.fragment_real_estate_map),
    OnMarkerClickListener,
    OnInfoWindowClickListener,
    OnMapReadyCallback {

    private val binding by viewBinding { FragmentRealEstateMapBinding.bind(it) }
    private val viewModel by viewModels<RealEstateMapViewModel>()
    private var myPositionMaker: Marker? = null
    private lateinit var mMap: GoogleMap
    private val realEstatesMarker: List<Marker> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.notifyFragmentNav()
        //FAB
        binding.fragmentRealEstateMapFabPosition
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.fragment_real_estate_map_fab_position)}
        binding.fragmentRealEstateMapFabClose
            .setOnClickListener {viewModel.onFabButtonClicked(R.id.fragment_real_estate_map_fab_close)}
        //googleMap
        binding.googleMapView.onCreate(savedInstanceState)
        binding.googleMapView.getMapAsync(this)
        //Event
        viewModel.singleLiveRealEstateMapEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                RealEstateMapEvent.CloseFragment -> closeFragment()
                RealEstateMapEvent.CloseSecondPaneFragment -> closeSecondPaneFragment()
                RealEstateMapEvent.CenterCameraOnUserPosition -> centerCameraOnPosition()
            }.exhaustive

        }

    }

    private fun closeSecondPaneFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_FrameLayout_container_details, RealEstateDetailsFrag())
        transaction.commit()
    }

    private fun centerCameraOnPosition() {
        val markerLatLng = LatLng(myPositionMaker?.position?.latitude ?:0.0, myPositionMaker?.position?.longitude ?:0.0)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(markerLatLng, 15f)
        mMap.animateCamera(cameraUpdate)
    }

    private fun closeFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_main_FrameLayout_container_real_estate_list, RealEstateListFrag())
        transaction.commit()
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
        viewModel.stop
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

        mMap = googleMap

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