package com.zabava.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.zabava.happyplaces.adapters.HappyPlacesAdapter
import com.zabava.happyplaces.database.DatabaseHandler
import com.zabava.happyplaces.databinding.ActivityMainBinding
import com.zabava.happyplaces.models.HappyPlaceModel

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getHappyPlacesListFromLocalDB()
        binding?.fabAddHappyPlaces?.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun setupHappyPlacesRecyclerView(
        happyPlaceList: ArrayList<HappyPlaceModel>
    ) {

        binding?.rvHappyPlaces?.layoutManager =
            LinearLayoutManager(this)
        binding?.rvHappyPlaces?.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(happyPlaceList)
        binding?.rvHappyPlaces?.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })

    }

    private fun getHappyPlacesListFromLocalDB() {
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

        if (getHappyPlaceList.size > 0) {
            for (i in getHappyPlaceList) {
                binding?.rvHappyPlaces?.visibility = View.VISIBLE
                binding?.tvNoRecords?.visibility = View.GONE
                setupHappyPlacesRecyclerView(happyPlaceList = getHappyPlaceList)
            }
        } else {
            binding?.rvHappyPlaces?.visibility = View.GONE
            binding?.tvNoRecords?.visibility = View.VISIBLE
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlacesListFromLocalDB()
            }
        } else {
            Log.e("Activity", "Cancelled or Back pressed")
        }
    }

    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}