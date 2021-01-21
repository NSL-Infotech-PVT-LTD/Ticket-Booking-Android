package com.surpriseme.user.fragments.locationfragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.FragmentLocationBinding
import com.surpriseme.user.fragments.googlemapfragment.MapFragment
import com.surpriseme.user.fragments.homefragment.HomeFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList


class LocationFragment : Fragment(), View.OnClickListener,
    LocationListAdapter.DisplayAddToDashboard, LocationListAdapter.DeleteAddress,
    LocationListAdapter.EditAddress {

    private lateinit var binding: FragmentLocationBinding
    private lateinit var tbackpress: MaterialTextView
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    var locationList: ArrayList<LocationDataList> = ArrayList()
    private var addressDashboard = ""
    private var latitude: String = ""
    private var longitude: String = ""
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var address = ""
    private var locationName = ""
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var latlng: LatLng? = null
    private var addAddressBtn: MaterialButton? = null
    private var fullAddress = ""
    private var landmark = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        ((ctx as MainActivity)).locationFragmentContext(this@LocationFragment)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)

        requireActivity().getWindow()?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        tbackpress = view.findViewById(R.id.backpress) // Toolbar back arrow click listner....
        tbackpress.setOnClickListener(this) //  Initializing the Toolbar

        addAddressBtn = view.findViewById(R.id.addAddressBtn)
        addAddressBtn?.setOnClickListener(this) //  Initializing the Add Address Button click
        binding.addAddressBtnLayout.setOnClickListener(this)

        Places.initialize(ctx, ctx.resources.getString(R.string.places_api_key))
        val placesClient = Places.createClient(ctx)

        init(view)
        return view
    }

    private fun init(view: View) {

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text = Utility.randomString(ctx)

        ((ctx as MainActivity)).hideBottomNavigation() // Hide bottom navigation on Location Fragment....
        initializeLocationRecycler()    // Initializing Location Recycler View....
        locationListApi()

    }

    private fun initializeLocationRecycler() {
        val layoutManager =
            LinearLayoutManager(ctx)    // Getting Instance of Linear layout manager for recycler view....
        binding.locationRecycler.layoutManager =
            layoutManager  // Setting layout manager to recycler view....
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.backpress -> {
                if (locationList.size >0) {
                    if (shared.getString(Constants.ADDRESS) == "") {
                        Utility.alertErrorMessage(ctx, getString(R.string.SELECT_ADDRESS_ERROR))
                    } else {
                        replaceFragment(HomeFragment())
                    }
                } else {
                    shared.setString(Constants.ADDRESS, "")
                    shared.setString(Constants.LATITUDE, "")
                    shared.setString(Constants.LONGITUDE, "")
                    replaceFragment(HomeFragment())
                }




            }
            R.id.addAddressBtn -> {
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

            R.id.addAddressBtnLayout -> {
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
                        Constants.LATLNG = latlng
                        Constants.WantToAddLocation = true
                        Constants.WantToUpdateAddress = false
                        address = place.address.toString()
                        lat = latlng?.latitude!!
                        lng = latlng?.longitude!!
                        Constants.WantToAddLocation = true

                        val fragment = MapFragment()
                        val bundle = Bundle()
                        bundle.putDouble("lat", lat)
                        bundle.putDouble("lng", lng)
                        bundle.putString("locationName", locationName)
                        bundle.putString("address", address)
                        fragment.arguments = bundle
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.frameContainer, fragment)
                        transaction?.addToBackStack("locationFragment")
                        transaction?.commit()

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {

                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
//                        Log.i(TAG, status.statusMessage)
                        Toast.makeText(
                            ctx,
                            "" + status.statusMessage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun replaceFragment(fragment: Fragment) {

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("LocationManager")
        transaction?.commit()
    }

    fun locationListApi() {

        binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.addressListApi(shared.getString(Constants.DataKey.AUTH_VALUE))
            .enqueue(object : Callback<LocationListModel> {
                override fun onResponse(
                    call: Call<LocationListModel>,
                    response: Response<LocationListModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            locationList.clear()
                            Constants.locationList.clear()
                            locationList = response.body()?.data!!
                            Constants.locationList = locationList
                            if (locationList.isNotEmpty()) {
                                addAddressBtn?.visibility = View.VISIBLE

                                // When location list will not empty then display location list to Location recycler view....

                                val locationListAdapter =
                                    LocationListAdapter(
                                        shared,
                                        ctx,
                                        locationList,
                                        this@LocationFragment,
                                        this@LocationFragment,
                                        this@LocationFragment
                                    )
                                binding.locationRecycler.adapter = locationListAdapter
                                binding.locationErrorContainer.visibility = View.GONE
                            } else {
                                // When location list will empty then display No data found and refresh button will display
                                binding.locationErrorContainer.visibility = View.VISIBLE
                                addAddressBtn?.visibility = View.GONE
                            }
                        }
                    } else {
                        // When Api will not successfull then error to display in json
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    ctx,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<LocationListModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    if (t is SocketTimeoutException)
                        Toast.makeText(ctx, "" + Constants.TIME_OUT, Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                }
            })
    }   // End of location list api....

    override fun dispAddressDashboard(address: String, lat: String, lng: String, name: String) {

        Handler().postDelayed({
            Constants.SAVED_LOCATION = true
            addressDashboard = address
            latitude = lat
            longitude = lng
            shared.setString(Constants.ADDRESS, addressDashboard)
            shared.setString(Constants.LATITUDE, latitude)
            shared.setString(Constants.LONGITUDE, longitude)
            shared.setString(Constants.NAME, name)
            val fragment = HomeFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameContainer, fragment)
            transaction?.commit()
        }, 500)

    }

    // Delete address api....
    private fun deleteAddressApi(id: String) {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.addressDeleteApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, id
        )
            .enqueue(object : Callback<DeleteAddressModel> {
                override fun onResponse(
                    call: Call<DeleteAddressModel>,
                    response: Response<DeleteAddressModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                ctx,
                                "" + response.body()?.data?.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            locationListApi()
                        }
                    } else {
                        // When Api will not successfull then error to display in json
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    ctx,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<DeleteAddressModel>, t: Throwable) {
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Override function from Location Adapter to delete address....
    override fun deleteAdd(id: String) {

        alertPopUp(id)
    }

    // Display Alert pop when user will delete address...
    private fun alertPopUp(id: String) {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.logout_popup, null)
        val popUpWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popUpWindow.elevation = 10f
        }
        popUpWindow.isTouchable = false
        popUpWindow.isOutsideTouchable = false

        val ok: MaterialTextView = popUp.findViewById(R.id.yes)
        val cancelTv: MaterialTextView = popUp.findViewById(R.id.cancel)

        ok.setOnClickListener {
            deleteAddressApi(id)
            popUpWindow.dismiss()
            if (Constants.ADDRESS_ID == id){
                shared.setInt("myValue", -1)
                shared.setString(Constants.ADDRESS,"")
                shared.setString(Constants.LATITUDE,"")
                shared.setString(Constants.LONGITUDE,"")
            }

        }
        cancelTv.setOnClickListener {
            popUpWindow.dismiss()
        }
    }   // End of Alert popup window....

    override fun updateAddress(locationDataList: LocationDataList) {

        Constants.LATLNG =
            LatLng(locationDataList.latitude.toDouble(), locationDataList.longitude.toDouble())
        locationName = locationDataList.name
        address = locationDataList.street_address
        Constants.WantToAddLocation = false
        Constants.WantToUpdateAddress = true
        latitude = locationDataList.latitude
        longitude = locationDataList.longitude
        fullAddress = locationDataList.state
        landmark = locationDataList.country

        val fragment = MapFragment()
        val bundle = Bundle()
        bundle.putDouble("lat", latitude.toDouble())
        bundle.putDouble("lng", longitude.toDouble())
        bundle.putString("locationName", locationName)
        bundle.putString("address", address)
        bundle.putString("fullAddress", fullAddress)
        bundle.putString("landmark", landmark)
        bundle.putString("addressID", locationDataList.id.toString())
        fragment.arguments = bundle
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("locationFragment")
        transaction?.commit()

    }

}