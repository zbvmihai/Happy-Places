package com.zabava.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.zabava.happyplaces.R
import com.zabava.happyplaces.databinding.ActivityMapBinding
import com.zabava.happyplaces.models.HappyPlaceModel

@Suppress("DEPRECATION")
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mHappyPlaceDetails: HappyPlaceModel? = null

    private var binding: ActivityMapBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {


            mHappyPlaceDetails =
                intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel

            if (mHappyPlaceDetails != null){
                setSupportActionBar(binding?.tbMap)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = mHappyPlaceDetails!!.title

                binding?.tbMap?.setNavigationOnClickListener {
                    finish()
                }


                val supportMapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(
                    R.id.map) as SupportMapFragment

                supportMapFragment.getMapAsync(this)
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val position = LatLng(mHappyPlaceDetails!!.latitude,mHappyPlaceDetails!!.longitude)
        googleMap!!.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetails!!.location))
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position,15f)
        googleMap.animateCamera(newLatLngZoom)



    }
}