package com.surpriseme.user.fragments.googlemapfragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.GeoPoint
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentMapBinding
import com.surpriseme.user.fragments.locationfragment.LocationDataList
import com.surpriseme.user.fragments.locationfragment.LocationFragment
import com.surpriseme.user.fragments.locationfragment.UpdateAddressModel
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.fragment_map.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.nio.channels.UnresolvedAddressException
import java.util.*
import kotlin.collections.ArrayList


class MapFragment : Fragment(), OnMapReadyCallback, View.OnClickListener
//    GoogleApiClient.ConnectionCallbacks,
//    GoogleApiClient.OnConnectionFailedListener,
//    LocationListener
{

    private lateinit var binding: FragmentMapBinding
    private lateinit var shared: PrefrenceShared
    private lateinit var mMap: GoogleMap
    private  var isFirst: Boolean = true
    private lateinit var ctx: Context
    private lateinit var tbackpress: MaterialTextView
    private lateinit var centerLatlng: LatLng
    private var googleApiClient: GoogleApiClient? = null
    private var locationName = ""
    private var mAddress = ""
    private var additionalDetails: String? = null
    private var landmark: String? = null
    private var isEditUpdatedLocation = false
    private var addressID = ""


    // variables for hit api....
    private var name = ""
    private var streetAddress = ""
    private var city = "city"
    private var state = "n/a"
    private var zip = "zip"
    private var country = "n/a"
    private var latitude = 0.0
    private var longitude = 0.0

    // var for Place sdk....
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var latlng: LatLng? = null
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private val ERROR_DIALOG_REQUEST = 9001
    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    private var isOtherAddress = false

    // var to get intent while update address....
    private lateinit var locationDataList: LocationDataList



    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)

        Places.initialize(ctx, ctx.resources.getString(R.string.places_api_key))
        val placesClient = Places.createClient(ctx)

        if (isServicesOk()) {
            init()
        }

        tbackpress = view.findViewById(R.id.backpress)
        tbackpress.setOnClickListener(this)



        if (!isEditUpdatedLocation) {
            if (Constants.WantToAddLocation) {
                latitude = arguments?.getDouble("lat")!!
                longitude = arguments?.getDouble("lng")!!
                locationName = arguments?.getString("locationName")!!
                streetAddress = arguments?.getString("address")!!
                binding.mapAddressTxt.text = streetAddress


            } else if (Constants.WantToUpdateAddress) {

                latitude = arguments?.getDouble("lat")!!
                longitude = arguments?.getDouble("lng")!!
                locationName = arguments?.getString("locationName")!!
                streetAddress = arguments?.getString("address")!!
                additionalDetails = arguments?.getString("fullAddress")
                landmark = arguments?.getString("landmark")
                addressID = arguments?.getString("addressID")!!
                binding.mapAddressTxt.text = streetAddress
                if (additionalDetails == "n/a") {
                    binding.flatEdt.setText("")
                } else {
                    binding.flatEdt.setText(additionalDetails)
                }
                if (landmark == "n/a") {
                    binding.landmarkEdt.setText("")
                } else {
                    binding.landmarkEdt.setText(landmark)
                }

            }
        } else {
            latitude = arguments?.getDouble("lat")!!
            longitude = arguments?.getDouble("lng")!!
            locationName = arguments?.getString("locationName")!!
            streetAddress = arguments?.getString("address")!!
            binding.mapAddressTxt.text = streetAddress
        }
        displayAddressType(locationName)

        // save text while write addition details....
        binding.flatEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                additionalDetails = binding.flatEdt.text.toString().trim()

            }

        })

        binding.landmarkEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                landmark = binding.landmarkEdt.text.toString().trim()

            }

        })

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(ctx)



        return view
    }

    private fun displayAddressType(locationName: String) {
        when (locationName) {
            Constants.HOME_ADDRESS -> {
                name = Constants.HOME_ADDRESS
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherTypeEdt.visibility = View.GONE
                binding.closeIcon.visibility = View.GONE
            }
            Constants.WORK_ADDRESS -> {
                name = Constants.WORK_ADDRESS
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherTypeEdt.visibility = View.GONE
                binding.closeIcon.visibility = View.GONE
            }
            else -> {
                if (Constants.WantToAddLocation) {
                    binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                    binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                    binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                    binding.otherTypeEdt.visibility = View.GONE
                    binding.closeIcon.visibility = View.GONE

                } else {
                    binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                    binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                    binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                    binding.otherTypeEdt.visibility = View.VISIBLE
                    binding.otherTypeEdt.setText(locationName)
                    isOtherAddress = true
                }
            }
        }

    }

    private fun isServicesOk(): Boolean {
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx)
        if (available == ConnectionResult.SUCCESS) {
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            val dialog: Dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                activity,
                available,
                ERROR_DIALOG_REQUEST
            )
            dialog.show()
        } else {
            Toast.makeText(ctx, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
        return false

    }

    private fun init() {

        binding.homeBtn.setOnClickListener(this)
        binding.workBtn.setOnClickListener(this)
        binding.otherBtn.setOnClickListener(this)
        binding.saveAddressBtn.setOnClickListener(this)
        binding.closeIcon.setOnClickListener(this)
        binding.editAddressIcon.setOnClickListener(this)

        initializingGoogleMap()

    }

    // Initializing Google Map here....
    private fun initializingGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapFragment)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
//                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true)
            }
        } else {
//            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
//        val geocoder: Geocoder?
//        val addresses: List<Address>?
//        geocoder = Geocoder(ctx, Locale.getDefault())
        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1)
//            val address = (addresses as MutableList<Address>?)?.get(0)
//            var locality = ""
//            var addCheckLocality = ""
//            if (address != null) {
//                var thoroughFare = address.thoroughfare + "," + address.featureName
//                var postalCode = address.postalCode
//                var locality = address.locality
//                var countryname = address.countryName
//
//                if (address.thoroughfare == null || thoroughFare == "Unnamed Road") {
//                    thoroughFare = address.featureName  + ", "+ address.subLocality
//
//                } else if (postalCode == null) {
//                    postalCode == ""
//                }else if (locality == null) {
//                    locality = ""
//                }else if (countryname == null) {
//                    countryname = ""
//                }
//
//                locality = thoroughFare + ", " + postalCode  + ", " + locality + "," + countryname
                mMap.setOnCameraChangeListener(null)
//
//            }
//            val markerOptions = MarkerOptions()
//            markerOptions.position(Constants.LATLNG!!)
//            markerOptions.title(address?.locality)
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_drop_icon))
//            mCurrLocationMarker = mMap.addMarker(markerOptions)

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Constants.LATLNG))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18f))
            mMap.setOnCameraChangeListener(null)


//            val latLngBounds = LatLngBounds.Builder().include(Constants.LATLNG).build()
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 30))


        } catch (e: java.lang.Exception) {

            Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT).show()

        }

        mMap.setOnCameraChangeListener { p0 ->
            if(isFirst){
                isFirst = false
                return@setOnCameraChangeListener
            }
            if (p0?.target!!.latitude == 0.0)
                centerLatlng = Constants.LATLNG!!
            else
                centerLatlng = p0.target!!

            val geocoder: Geocoder?
            val addresses: List<Address>?
            geocoder = Geocoder(ctx, Locale.getDefault())
            try {
                addresses = geocoder.getFromLocation(centerLatlng.latitude, centerLatlng.longitude, 1)
                val address = (addresses as MutableList<Address>?)?.get(0)

                if (address != null) {
                    var thoroughFare = address.thoroughfare + "," + address.featureName
                        var postalCode = address.postalCode
                        var locality = address.locality
                        var countryname = address.countryName

                        if (address.thoroughfare == null || thoroughFare == "Unnamed Road") {
                            thoroughFare = address.featureName  + ", "+ address.subLocality

                        } else if (postalCode == null) {
                            postalCode == ""
                        }else if (locality == null) {
                            locality = ""
                        }else if (countryname == null) {
                            countryname = ""
                        }

                    locality = thoroughFare + ", " + postalCode  + ", " + locality + "," + countryname

                    binding.mapAddressTxt.text = locality
                    latitude = address.latitude
                    longitude = address.longitude

                    mMap.clear()

                } else {
                    Toast.makeText(ctx, "No Location Found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {

            }
        }  // end of get location while moving camera....

    }

//    @Synchronized
//    protected fun buildGoogleApiClient() {
//        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
//            .addConnectionCallbacks(this)
//            .addOnConnectionFailedListener(this)
//            .addApi(LocationServices.API).build()
//        mGoogleApiClient!!.connect()
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {

                val fragment = LocationFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameContainer, fragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
            R.id.homeBtn -> {
                name = Constants.HOME_ADDRESS
                isOtherAddress = false
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherTypeEdt.visibility = View.GONE
                binding.closeIcon.visibility = View.GONE
                binding.otherTypeEdt.text.clear()

            }   // end of home button....
            R.id.workBtn -> {
                name = Constants.WORK_ADDRESS
                isOtherAddress = false
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherTypeEdt.visibility = View.GONE
                binding.closeIcon.visibility = View.GONE
                binding.otherTypeEdt.text.clear()

            }   // end of work button....
            R.id.otherBtn -> {

                isOtherAddress = true
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                binding.otherTypeEdt.visibility = View.VISIBLE
                binding.closeIcon.visibility = View.VISIBLE
                binding.otherTypeEdt.setText(locationName)
//                binding.homeBtn.visibility = View.GONE
//                binding.workBtn.visibility = View.GONE
//                binding.otherBtn.visibility = View.GONE


            }   // end of other button....
            R.id.closeIcon -> {
//                binding.homeBtn.visibility = View.VISIBLE
//                binding.workBtn.visibility = View.VISIBLE
//                binding.otherBtn.visibility = View.VISIBLE
                binding.otherTypeEdt.visibility = View.GONE
                binding.closeIcon.visibility = View.GONE
            }
            R.id.saveAddressBtn -> {

                if (!additionalDetails.isNullOrEmpty())
                    state = additionalDetails!!
                if (!landmark.isNullOrEmpty()) {
                    country = landmark!!
                }

                    if (isOtherAddress) {
                        name = otherTypeEdt.text.toString().trim()
                        if (name.isEmpty())
                        name = "Other"
                            if (Constants.WantToAddLocation) {
                                createAddressApi()
                            } else {
                                updateAddressList()
                            }


                } else {

                    if (Constants.WantToAddLocation) {
                        createAddressApi()
                    } else {
                        updateAddressList()
                    }
                }


            }   // end of Save Address Button....
            R.id.editAddressIcon -> {

                Constants.WantToUpdateAddress = false
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(ctx)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        latlng = place.latLng
                        streetAddress = place.address.toString()
                        Constants.LATLNG = latlng
                        Constants.WantToAddLocation = false
                        Constants.WantToUpdateAddress = true
                        lat = latlng?.latitude!!
                        lng = latlng?.longitude!!
                        isEditUpdatedLocation = true


//                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                        val fragment = MapFragment()
                        val bundle = Bundle()
                        bundle.putDouble("lat", lat)
                        bundle.putDouble("lng", lng)
                        bundle.putString("locationName", locationName)
                        bundle.putString("address", streetAddress)
                        bundle.putString("fullAddress", additionalDetails)
                        bundle.putString("landmark", landmark)
                        bundle.putString("addressID", addressID)
                        fragment.arguments = bundle
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.frameContainer, fragment)
                        transaction?.commit()

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {

                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
//                        Log.i(TAG, status.statusMessage)
                        Toast.makeText(ctx, "" + status.statusMessage.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Create Address Api....
    private fun createAddressApi() {

        if (!isOtherAddress) {
            if (name != "Home" && name != "Work") {
                name = "Other"
            }
        }
        binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.createAddress(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE,
            name,
            streetAddress,
            city,
            state,
            zip,
            country,
            latitude.toString(),
            longitude.toString()
        )
            .enqueue(object : Callback<CreateLocationModel> {

                override fun onResponse(
                    call: Call<CreateLocationModel>,
                    response: Response<CreateLocationModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            val fragment = LocationFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.frameContainer, fragment)
                            transaction?.commit()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CreateLocationModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Update Address Api....
    private fun updateAddressList() {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.updateAddressApi(shared.getString(Constants.DataKey.AUTH_VALUE), Constants.DataKey.CONTENT_TYPE_VALUE, addressID,
            name,
            streetAddress,
            city,
            state,
            zip,
            country,
            latitude.toString(),
            longitude.toString()
        )
            .enqueue(object : Callback<UpdateAddressModel> {
                override fun onResponse(
                    call: Call<UpdateAddressModel>,
                    response: Response<UpdateAddressModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            Toast.makeText(ctx, "" + response.body()?.data?.Message.toString(), Toast.LENGTH_SHORT).show()
                            if (Constants.ADDRESS_ID == addressID) {
                                shared.setString(Constants.ADDRESS, streetAddress)
                                shared.setString(Constants.LATITUDE, latitude.toString())
                                shared.setString(Constants.LONGITUDE, longitude.toString())

                            }
                            val fragment = LocationFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.frameContainer, fragment)
                            transaction?.commit()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<UpdateAddressModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }


}
//
//    override fun onConnected(bundle: Bundle?) {
//        mLocationRequest = LocationRequest()
//        mLocationRequest!!.interval = 1000
//        mLocationRequest!!.fastestInterval = 1000
//        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//        if (ContextCompat.checkSelfPermission(
//                ctx,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//            == PackageManager.PERMISSION_GRANTED
//        ) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient,
//                mLocationRequest,
//                this
//            )
//        }
//
//    }
//
//    override fun onConnectionSuspended(p0: Int) {
//    }
//
//    override fun onConnectionFailed(p0: ConnectionResult) {
//    }
//
//    override fun onPause() {
//        super.onPause()
//        googleApiClient?.stopAutoManage(requireActivity())
//        googleApiClient?.disconnect()
//    }
//
//    override fun onLocationChanged(location: Location?) {
//
//        val latLng: LatLng
//        mLastLocation = location
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker!!.remove()
//        }
//
//        latLng = LatLng(latitude,longitude)
//
////        if ((Constants.WantToUpdateAddress) || (Constants.WantOtherLocation)) {
////            latLng = LatLng(latitude, longitude)
////        } else {
////            latLng = LatLng(location?.latitude!!, location.longitude)
////        }
//
//        val markerOptions = MarkerOptions()
//        markerOptions.position(latLng)
//        markerOptions.title("Current Position")
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//        mCurrLocationMarker = mMap.addMarker(markerOptions)
//
//        //move map camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11F))
//
//        //stop location updates
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
//        }
//
//    }

