package com.surpriseme.user.fragments.googlemapfragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentMapBinding
import com.surpriseme.user.fragments.locationfragment.LocationDataList
import com.surpriseme.user.fragments.locationfragment.LocationFragment
import com.surpriseme.user.fragments.locationfragment.UpdateAddressModel
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, View.OnClickListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var shared: PrefrenceShared
    private lateinit var mMap: GoogleMap
    private lateinit var ctx: Context
    private lateinit var tbackpress: MaterialTextView
    private lateinit var centerLatlng: LatLng
    private var googleApiClient: GoogleApiClient? = null

    // variables for hit api....
    private var name = ""
    private var streetAddress = ""
    private var city = ""
    private var state = ""
    private var zip = ""
    private var country = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private val ERROR_DIALOG_REQUEST = 9001
    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    private var isOtherAddress = false

    // var to get intent while update address....
    private lateinit var locationDataList: LocationDataList
    private var addressID = ""


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

        if (isServicesOk()) {
            init()
        }
        tbackpress = view.findViewById(R.id.backpress)
        tbackpress.setOnClickListener(this)

        if (Constants.WantToUpdateAddress) {
            locationDataList = arguments?.getSerializable("addressList") as LocationDataList
            if (locationDataList != null) {
                latitude = locationDataList.latitude.toDouble()
                longitude = locationDataList.longitude.toDouble()
                addressID = locationDataList.id.toString()
                name = locationDataList.name
                if (name!=null) {
                    binding.otherTypeEdt.setText(name)
                    displayAddressType()
                }

            }
        } else if (Constants.WantOtherLocation) {
            latitude = arguments?.getString("latitude")?.toDouble()!!
            longitude = arguments?.getString("longitude")?.toDouble()!!
        }
        binding.otherTypeEdt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                name = binding.otherTypeEdt.text.toString().trim()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        return view
    }

    private fun displayAddressType() {
        when (name) {
            Constants.HOME_ADDRESS -> {
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
            }
            Constants.WORK_ADDRESS -> {
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
            }
            else -> {
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_color))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
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

        initializingGoogleMap()

    }

    // Initializing Google Map here....
    private fun initializingGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapFragment)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true)
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnCameraChangeListener { p0 ->
            centerLatlng = p0?.target!!

            val geocoder: Geocoder?
            val addresses: List<Address>?
            geocoder = Geocoder(ctx, Locale.getDefault())
            try {
                addresses = geocoder.getFromLocation(
                    centerLatlng.latitude,
                    centerLatlng.longitude,
                    1
                )
                val address = (addresses as MutableList<Address>?)?.get(0)
                var locality = ""
                var checkLocality = ""
                if (address != null) {
                    var subLocality = address.subLocality
                    checkLocality = address.locality
                    if (subLocality == null) {
                        subLocality = ""
                    }
                    if (checkLocality == null)
                        checkLocality = ""

                    if (subLocality == "" || checkLocality =="") {
                        locality =
                            address.featureName + "," + address.adminArea + "," + address.postalCode + "," + address.countryName
                    }else {
                        locality =
                            subLocality + "," + address.featureName + "," + address.locality + "," + address.adminArea + "," + address.postalCode + "," + address.countryName
                    }

                    binding.mapAddressTxt.text = locality
                    streetAddress = locality
                    city = locality
                    state = address.adminArea.toString()
                    zip = address.postalCode.toString()
                    country = address.countryName.toString()
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

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

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
                Toast.makeText(ctx, "" + name, Toast.LENGTH_SHORT).show()
            }   // end of home button....
            R.id.workBtn -> {
                name = Constants.WORK_ADDRESS
                isOtherAddress = false
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx,R.color.grey_color))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx,R.color.colorPrimary))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx,R.color.grey_color))
                Toast.makeText(ctx, "" + name, Toast.LENGTH_SHORT).show()

            }   // end of work button....
            R.id.otherBtn -> {

                isOtherAddress = true
                binding.homeBtn.setBackgroundColor(ContextCompat.getColor(ctx,R.color.grey_color))
                binding.workBtn.setBackgroundColor(ContextCompat.getColor(ctx,R.color.grey_color))
                binding.otherBtn.setBackgroundColor(ContextCompat.getColor(ctx,R.color.colorPrimary))
                binding.homeBtn.visibility = View.GONE
                binding.workBtn.visibility = View.GONE
                binding.otherBtn.visibility = View.GONE
                binding.otherTypeEdt.visibility = View.VISIBLE
                binding.closeIcon.visibility = View.VISIBLE

            }   // end of other button....
            R.id.closeIcon -> {
                binding.homeBtn.visibility = View.VISIBLE
                binding.workBtn.visibility = View.VISIBLE
                binding.otherBtn.visibility = View.VISIBLE
                binding.otherTypeEdt.visibility = View.GONE
                binding.closeIcon.visibility = View.GONE
            }
            R.id.saveAddressBtn -> {
                if (name == "" && isOtherAddress == false) {

                    Toast.makeText(
                        ctx,
                        "" + getString(R.string.please_select_address_type),
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (name == "" && isOtherAddress == true) {
                    name = binding.otherTypeEdt.text.toString().trim()
                    if (name == "") {
                        Toast.makeText(
                            ctx,
                            "" + getString(R.string.please_enter_other_address),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {

                if (!Constants.WantToUpdateAddress || Constants.WantOtherLocation) {
                        createAddressApi()
                    } else {
                        updateAddressList()
                    }
                }






//                if (name == "") {
//                    Toast.makeText(
//                        ctx,
//                        "" + getString(R.string.please_select_address_type),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else if (other == "") {
//                    Toast.makeText(
//                        ctx,
//                        "" + getString(R.string.please_enter_other_address),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }

            }   // end of Save Address Button....

        }
    }

    // Create Address Api....
    private fun createAddressApi() {

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
                    displayAddressType()
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
        RetrofitClient.api.updateAddressApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE,
            addressID,
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
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<UpdateAddressModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx,"" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onPause() {
        super.onPause()
        googleApiClient?.stopAutoManage(activity!!)
        googleApiClient?.disconnect()
    }

    override fun onLocationChanged(location: Location?) {

        val latLng: LatLng
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        if ((Constants.WantToUpdateAddress) || (Constants.WantOtherLocation)) {
            latLng = LatLng(latitude,longitude)
        } else {
            latLng = LatLng(location?.latitude!!,location.longitude)
        }

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap.addMarker(markerOptions)

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11F))

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }

    }

}