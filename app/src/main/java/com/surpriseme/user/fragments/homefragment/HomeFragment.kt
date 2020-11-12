package com.surpriseme.user.fragments.homefragment

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.searchactivity.SearchActivity
import com.surpriseme.user.databinding.FragmentHomeBinding
import com.surpriseme.user.fragments.artistbookingdetail.ArtistBookingFragment
import com.surpriseme.user.fragments.locationfragment.LocationFragment
import com.surpriseme.user.fragments.notificationfragment.NotificationFragment
import com.surpriseme.user.fragments.viewprofile.ProfileFragment
import com.surpriseme.user.fragments.wayofbookingfragment.WayOfBookingFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PaginationScrollListener
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), View.OnClickListener, ArtistListAdapter.ArtistListFace,
    ArtistListAdapter.BookBtnClick,GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener{
    private var mylocation: Location? = null
    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_CHECK_SETTINGS_GPS = 0x1
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2
    private var address = ""

    private lateinit var binding: FragmentHomeBinding
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var artistList: ArrayList<DataUserArtistList> = ArrayList()
    private var latitude = 0.0
    private  var longitude = 0.0
    private var search = ""
    private var isFirstTime = true
    private var isInvalidAuth = false
    private var artistListAdapter: ArtistListAdapter? = null
    private lateinit var layoutManager:LinearLayoutManager
    private var isLastPage = false
    private var isLoading = false
    private var currentPage: Int = 1
    private var totalPage = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onStart() {
        super.onStart()
        binding.yourLocationInfo.text = shared.getString(Constants.ADDRESS)
        artistListApi(latitude.toString(),longitude.toString(),search)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)
        setUpGClient()

        if (shared.getString(Constants.DataKey.USER_IMAGE) !="") {
            Picasso.get().load(shared.getString(Constants.DataKey.USER_IMAGE)).placeholder(R.drawable.profile_pholder)
                .resize(4000, 1500).into(binding.dashUserImg)
        }else {
            binding.dashUserImg.setImageDrawable(
                ContextCompat.getDrawable(
                    ctx,
                    R.drawable.profile_pholder
                )
            )
        }
        binding.viewProfile.setOnClickListener(this)
        binding.addressLayout.setOnClickListener(this)
        binding.searchEdt.setOnClickListener(this)
        binding.notiIcon.setOnClickListener(this)
        ((ctx as MainActivity)).showBottomNavigation()

        layoutManager = LinearLayoutManager(ctx)
        binding.artistRecycler.layoutManager = layoutManager
        binding.artistRecycler.setHasFixedSize(true)

        artistListAdapter = ArtistListAdapter(ctx, ArrayList(), this@HomeFragment, this@HomeFragment)
        binding.artistRecycler.adapter = artistListAdapter

        binding.artistRecycler.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                artistListApi(latitude.toString(),longitude.toString(),search)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        Handler().postDelayed({
            if (Constants.SAVED_LOCATION) {
                binding.yourLocationInfo.text = shared.getString(Constants.ADDRESS)
                artistListApi(
                    shared.getString(Constants.LATITUDE),
                    shared.getString(Constants.LONGITUDE),
                    search
                )
            }

        }, 2000)


        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionLocation = ContextCompat.checkSelfPermission(activity!!, ACCESS_FINE_LOCATION)
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            getMyLocation()
        }
    }
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.viewProfile -> {
                replaceFragment(ProfileFragment())
            }
            R.id.addressLayout -> {

                replaceFragment(LocationFragment())
            }
            R.id.searchEdt -> {
                val searchActivity = Intent(ctx, SearchActivity::class.java)
                startActivity(searchActivity)
            }
            R.id.notiIcon -> {
                replaceFragment(NotificationFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("fragment")
        transaction?.commit()
    }

    private fun artistListApi(lat: String, lng: String, search: String) {

        if (currentPage == 1)
        binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.artistListApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, "", lat, lng, search,currentPage.toString()
        )
            .enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            // after api successfull do your stuff....
                            artistList.clear()
                            artistList = response.body()?.data?.data!!
                            totalPage = response.body()?.data?.last_page!!
                            if (artistList.size > 0) {

                                Handler().postDelayed({
                                    run {
                                        /**
                                         * manage progress view
                                         */
                                        /**
                                         * manage progress view
                                         */
                                        if (currentPage != PaginationScrollListener.PAGE_START)
                                            artistListAdapter?.removeLoading()
                                        artistListAdapter?.addItems(artistList)
                                        if (currentPage < totalPage) {
                                            artistListAdapter?.addLoading()
                                        } else {
                                            isLastPage = true
                                        }
                                        isLoading = false
                                    }
                                }, 1500)

                                binding.homeContainer.visibility = View.VISIBLE

                            } else {
                                binding.homeContainer.visibility = View.GONE
                            }
                        }
                    } else if (response.code() == 401) {
                        isInvalidAuth = true
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
//                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                                invalidAuthPopUp()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    ctx,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Calling interface from Artist List adapter to send data from home fragment to ArtistDetail fragment with list on Item view click.
    override fun artistDetailLink(artistID: String) {
        shared.setString(Constants.ARTIST_ID, artistID)
        val fragment = ArtistBookingFragment()
        val bundle = Bundle()
        bundle.putString("artistID", artistID)
        fragment.arguments = bundle
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("homeFragment")
        transaction?.commit()
    }

    // Calling interface from Artist List adapter to send data from home fragment to ArtistDetail fragment with list on Book Button Click
    override fun btnClick(artistID: String) {
        shared.setString(Constants.ARTIST_ID, artistID)
        val fragment = WayOfBookingFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("homeFragment")
        transaction?.commit()
    }


    override fun onConnected(bundle: Bundle?) {
        checkPermissions()
    }

    override   fun onConnectionSuspended(i: Int) {
        //Do whatever you need
        //You can display a message here
    }


    override  fun onLocationChanged(location: Location) {
        mylocation = location
        if (mylocation != null) {
            latitude = mylocation?.latitude!!
            longitude = mylocation?.longitude!!
            shared.setString(Constants.LATITUDE,latitude.toString())
            shared.setString(Constants.LONGITUDE,longitude.toString())
            val geocoder: Geocoder
            var addresses: List<Address>?=null
            geocoder = Geocoder(ctx, Locale.getDefault())

            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            try {
                if (isFirstTime){
                    address = addresses!![0].getAddressLine(0)
                    val address = (addresses as MutableList<Address>?)?.get(0)
                    if (address != null) {
                        val locality = address.featureName+","+address.subLocality+","+address.locality+","+address.adminArea+","+address.postalCode+","+address.countryName
                        binding.yourLocationInfo.text = locality
                        shared.setString(Constants.ADDRESS, locality)
                        artistListApi(latitude.toString(),longitude.toString(),search)
                    }
                    isFirstTime =false
                }


                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    private fun setUpGClient() {
        googleApiClient = GoogleApiClient.Builder(ctx)
            .enableAutoManage(activity!!, 0, this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient?.connect()
    }

    override fun onPause() {
        super.onPause()
        googleApiClient?.stopAutoManage(getActivity()!!)
        googleApiClient?.disconnect()
    }

    private fun getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient?.isConnected()!!) {
                val permissionLocation = ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
                    val locationRequest = LocationRequest()
                    locationRequest.setInterval(3000)
                    locationRequest.setFastestInterval(3000)
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    val builder = LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                    builder.setAlwaysShow(true)
                    LocationServices.FusedLocationApi
                        .requestLocationUpdates(googleApiClient, locationRequest, this)
                    val result: PendingResult<LocationSettingsResult> = LocationServices.SettingsApi
                        .checkLocationSettings(googleApiClient, builder.build())
                    result.setResultCallback { result ->
                        val status: Status = result.getStatus()
                        when (status.getStatusCode()) {
                            LocationSettingsStatusCodes.SUCCESS -> {
                                // All location settings are satisfied.
                                // You can initialize location requests here.
                                val permissionLocation = ContextCompat
                                    .checkSelfPermission(
                                        activity!!,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                    mylocation = LocationServices.FusedLocationApi
                                        .getLastLocation(googleApiClient)
                                }

                            }
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                                     // Location settings are not satisfied.
                                // But could be fixed by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    // Ask to turn on GPS automatically
                                    status.startResolutionForResult(
                                        activity,
                                        REQUEST_CHECK_SETTINGS_GPS
                                    )

                                } catch (e: IntentSender.SendIntentException) {
                                    // Ignore the error.
                                }
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            }
                        }
                    }
                }
            }
        }
    }


    private fun checkPermissions(): Boolean {
        val permissionLocation = ContextCompat.checkSelfPermission(
            activity!!,
            ACCESS_FINE_LOCATION
        )
        val listPermissionsNeeded: ArrayList<String> = ArrayList()
        return if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(ACCESS_FINE_LOCATION)
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity!!,
                    listPermissionsNeeded.toArray(arrayOfNulls<String>(listPermissionsNeeded.size)), REQUEST_ID_MULTIPLE_PERMISSIONS)

            }
            false
        } else {
            getMyLocation()
            true
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    private  fun  invalidAuthPopUp() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.alert_popup_layout, null)
        val popUpWindowReport = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindowReport.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popUpWindowReport.elevation = 10f
        }
        popUpWindowReport.isTouchable = false
        popUpWindowReport.isOutsideTouchable = false

        val ok: MaterialTextView = popUp.findViewById(R.id.okTv)
        val congratsMsgTv: MaterialTextView = popUp.findViewById(R.id.congratsMsgTv)
        congratsMsgTv.text = Constants.SOMETHING_WENT_WRONG

        ok.setOnClickListener {
            if (isInvalidAuth) {
                popUpWindowReport.dismiss()
                val intent = Intent(ctx,LoginActivity::class.java)
                startActivity(intent)
                activity!!.finishAffinity()
            } else {
                popUpWindowReport.dismiss()
            }

        }
    }

}