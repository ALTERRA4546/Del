package com.example.deliveryapplication.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.deliveryapplication.R
import com.example.deliveryapplication.SharedData
import com.example.deliveryapplication.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.runBlocking

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private lateinit var mMap: GoogleMap
    private lateinit var sharedData: SharedData

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        sharedData = ViewModelProvider(requireActivity()).get(SharedData::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        googleMap

        if (sharedData.displaySinglePoint && sharedData.latitude != null && sharedData.longitude != null) {
            val location = LatLng(sharedData.latitude!!, sharedData.longitude!!)
            mMap.addMarker(MarkerOptions().position(location).title(sharedData.title))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
        } else {
            val postomats = sharedData.postomatList

            for (postomat in postomats) {
                val location = LatLng(postomat.longitude.toDouble(), postomat.width.toDouble())
                mMap.addMarker(MarkerOptions().position(location).title(postomat.title))
            }

            if (postomats.isNotEmpty()) {
                val firstLocation = LatLng(postomats[0].longitude.toDouble(), postomats[0].width.toDouble())
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 8.5f))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}