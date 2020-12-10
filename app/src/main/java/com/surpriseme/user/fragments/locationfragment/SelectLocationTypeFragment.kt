package com.surpriseme.user.fragments.locationfragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentSelectLocationTypeBinding
import com.surpriseme.user.fragments.googlemapfragment.MapFragment
import com.surpriseme.user.util.Constants


class SelectLocationTypeFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSelectLocationTypeBinding
    private lateinit var tbackpress:MaterialTextView
    private lateinit var ctx:Context

    // var for Place autopicker....
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var latLng: LatLng
    private var latitude = 0.0
    private var longitude = 0.0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_location_type, container, false)
        val view = binding.root

        tbackpress = view.findViewById(R.id.backpress)
        tbackpress.setOnClickListener(this)
        init()

        return view
    }

    private fun init() {

        val apiKey = getString(R.string.places_api_key)
        // Initialize the SDK
        Places.initialize(ctx, apiKey)
        binding.currentLocationBtn.setOnClickListener(this)
        binding.selectOtherLocationBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.currentLocationBtn -> {
                Constants.WantToUpdateAddress = false
                Constants.WantToAddLocation = false
                replaceFragment(MapFragment())
            }
            R.id.selectOtherLocationBtn -> {
//                replaceFragment(MapFragment())
                /*
                When user will click on select other location the it will be redirected to place picker dialog then after selecting the location, it will be
                redirected to the map. Selected location will be displayed on map then.
                */

                // Set the fields to specify which types of place data to
// return after the user has made a selection.
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS_COMPONENTS,
                    Place.Field.ADDRESS
                )
// Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(ctx)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
            R.id.backpress -> {
                val fragment = LocationFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameContainer,fragment)
                transaction?.addToBackStack("selectLocationFragment")
                transaction?.commit()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {

                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(
                            "TAG",
                            "Place: ${place.name}, ${place.id}, ${place.addressComponents}, ${place.address}"
                        )

                        latLng = place.latLng!!
                        latitude = latLng.latitude
                        longitude = latLng.longitude

                        val fragment = MapFragment()
                        Constants.WantToUpdateAddress = false
                        Constants.WantToAddLocation = true
                        val bundle = Bundle()
                        bundle.putString("latitude",latitude.toString())
                        bundle.putString("longitude",longitude.toString())
                        fragment.arguments = bundle
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.frameContainer,fragment)
                        transaction?.addToBackStack("locationType")
                        transaction?.commit()

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {

                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG", status.statusMessage!!)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("LocationManager")
        transaction?.commit()
    }

}