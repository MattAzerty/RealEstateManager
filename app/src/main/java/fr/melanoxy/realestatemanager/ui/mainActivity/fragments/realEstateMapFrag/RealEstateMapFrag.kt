package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateMapFrag

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import fr.melanoxy.realestatemanager.R
import fr.melanoxy.realestatemanager.databinding.FragmentRealEstateMapBinding
import fr.melanoxy.realestatemanager.ui.mainActivity.MainEventListener
import fr.melanoxy.realestatemanager.ui.utils.exhaustive
import fr.melanoxy.realestatemanager.ui.utils.viewBinding

@AndroidEntryPoint
class RealEstateMapFrag : Fragment(R.layout.fragment_real_estate_map),
    OnMarkerClickListener,
    OnInfoWindowClickListener,
    OnMapReadyCallback {

    private val binding by viewBinding { FragmentRealEstateMapBinding.bind(it) }
    private val viewModel by viewModels<RealEstateMapViewModel>()
    private lateinit var eventListener: MainEventListener
    private var myPositionMaker: Marker? = null
    private var mSelectedId: Long? = null
    private val realEstateMarkers: MutableList<Marker> = mutableListOf()
    private var mBounds: LatLngBounds? = null
    private lateinit var mMap: GoogleMap

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as MainEventListener
    }

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
                RealEstateMapEvent.CloseFragment -> eventListener.switchMainPane(R.layout.fragment_real_estate_list)
                RealEstateMapEvent.CloseSecondPaneFragment -> eventListener.switchSecondPane(R.layout.fragment_real_estate_details)
                RealEstateMapEvent.CenterCameraOnUserPosition -> centerCameraOnPosition()
                RealEstateMapEvent.OpenDetailsFragment -> eventListener.switchMainPane(R.layout.fragment_real_estate_details)
            }.exhaustive
        }
    }

    private fun centerCameraOnPosition() {
        val markerLatLng = LatLng(myPositionMaker?.position?.latitude ?:0.0, myPositionMaker?.position?.longitude ?:0.0)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(markerLatLng, 15f)
        mMap.animateCamera(cameraUpdate)
    }

    override fun onResume() {
        super.onResume()
        binding.googleMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.googleMapView.onPause()//TODO on rotation screen to avoid leaks
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commit()
    }

    /*override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }*/

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.googleMapView.onSaveInstanceState(outState)
    }*/

    override fun onMarkerClick(marker: Marker): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onInfoWindowClick(marker: Marker) {
        viewModel.onInfoWindowClicked(marker.tag as Long)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        viewModel.userPositionLiveData.observe(viewLifecycleOwner) {
            if(it!=null){
                val latLng = LatLng(it.latitude, it.longitude)
                addMyLocationMarker(latLng) //place on the map the userCurrentPosition
            }
        }

        viewModel.realEstatesPositionsLiveData.observe(viewLifecycleOwner) { realEstateMarkers ->
            if(realEstateMarkers!=null){
                addRealEstateMarkers(realEstateMarkers)
            }
        }

        viewModel.selectedRealEstatePositionLiveData.observe(viewLifecycleOwner) { estateId ->
            mSelectedId = estateId
            boundAroundSelectedId(estateId)
        }
    }

    private fun boundAroundSelectedId(estateId:Long?) {
        if(estateId!=null){
        realEstateMarkers.find { it.tag == estateId}
            ?.let { CameraUpdateFactory.newLatLngZoom(it.position, 15f) }
            ?.let { mMap.moveCamera(it) }
        }else{
            mBounds?.let { CameraUpdateFactory.newLatLngBounds(it, 50) }
                ?.let { mMap.moveCamera(it) }
        }
    }

    private fun addRealEstateMarkers(realEstateMarkersStateItem: List<RealEstateMarkerStateItem>) {
        if (realEstateMarkers.isNotEmpty()) { //If previous markers exist, we clear the map before adding updated markers
            realEstateMarkers.clear()
            addMyLocationMarker(myPositionMaker!!.position)
        }

        val builderBounds = LatLngBounds.Builder()

        for (markerInfos in realEstateMarkersStateItem) {
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(markerInfos.coordinates.latitude, markerInfos.coordinates.longitude))
                    .title(markerInfos.realEstateName)
                    .snippet(markerInfos.realEstatePrice)
                    .icon(
                        vectorToBitmap(
                            R.drawable.vc_apartment_white_72dp,
                            if (markerInfos.isSold) resources.getColor(R.color.black) else resources.getColor(
                                R.color.colorAccent
                            )
                        )
                    )
                    .infoWindowAnchor(0.5f, 0.5f)
            )
            builderBounds.include(marker!!.position)
            marker.tag = markerInfos.id
            realEstateMarkers.add(marker)
        }

        // Set a listener for marker click.
        mMap.setOnInfoWindowClickListener(this)
        //Bound camera around all cursors
        boundsCameraAroundMarkers(builderBounds)
    }

    private fun boundsCameraAroundMarkers(builderBounds: LatLngBounds.Builder) {
        val bounds = builderBounds.build()
        if(mSelectedId==null) {
            if (mBounds != bounds) { //when bounds are new we animate the camera
                mBounds = bounds
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
            } else { //when it's a "on resume" case we don't
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
            }
        }else{mSelectedId?.let { boundAroundSelectedId(it) }}
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addMyLocationMarker(latLng: LatLng) {
        if (myPositionMaker != null) {
            myPositionMaker!!.remove() //To avoid duplicate marker
        }

        myPositionMaker = mMap.addMarker(
            MarkerOptions()
                .position(latLng).icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
                .snippet("(lat:" + latLng.latitude + ", long:" + latLng.longitude + ")")
                .title(getString(R.string.my_position_marker))
        )

        // and move the map's camera to the same location with a zoom of 15.
        if (realEstateMarkers.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }
}