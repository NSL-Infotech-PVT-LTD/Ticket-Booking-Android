package com.surpriseme.user.fragments.homefragment

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
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
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.searchactivity.SearchActivity
import com.surpriseme.user.data.model.UpdateProfileModel
import com.surpriseme.user.databinding.FragmentHomeBinding
import com.surpriseme.user.fragments.artistbookingdetail.ArtistBookingFragment
import com.surpriseme.user.fragments.locationfragment.LocationDataList
import com.surpriseme.user.fragments.locationfragment.LocationFragment
import com.surpriseme.user.fragments.locationfragment.LocationListAdapter
import com.surpriseme.user.fragments.locationfragment.LocationListModel
import com.surpriseme.user.fragments.notificationfragment.NotificationFragment
import com.surpriseme.user.fragments.selectdateofbookingfragment.SelectDateFragment
import com.surpriseme.user.fragments.viewprofile.ProfileFragment
import com.surpriseme.user.fragments.viewprofile.UserViewProfile
import com.surpriseme.user.fragments.viewprofile.ViewProfileModel
import com.surpriseme.user.fragments.wayofbookingfragment.WayOfBookingFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), View.OnClickListener, ArtistListAdapter.ArtistListFace,
    ArtistListAdapter.BookBtnClick {
    private var mylocation: Location? = null
    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_CHECK_SETTINGS_GPS = 0x1
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2
    private var address = ""
    var currencyvalue = ""

    private lateinit var binding: FragmentHomeBinding
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var artistList: ArrayList<DataUserArtistList> = ArrayList()
    private var latitude = 0.0
    private var longitude = 0.0
    private var search = ""
    private var isInvalidAuth = false
    private var artistListAdapter: ArtistListAdapter? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var isLastPage = false
    private var isLoading = false
    private var currentPage: Int = 1
    private var totalPage = 0
    private var locationList: ArrayList<LocationDataList> = ArrayList()
    private var categoryList: ArrayList<Int> = ArrayList()
    private var userModel: UserViewProfile? = null
    private var userImage = ""
    private var userName = ""
    private var userEmail = ""
    private var currencyList: ArrayList<CurrencyList> = ArrayList()
    private var currencyAdapter: CurrencyAdapter? = null
    private var currencyRecycler: RecyclerView? = null
    private var popUpWindowCurrency: PopupWindow? = null
    private var invalidAuth: InvalidAuth? = null
    private var prefManager:PrefManger?=null

    private var isCheck = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context

    }

    override fun onStart() {
        super.onStart()

        getProfileApi()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)
        prefManager = PrefManger(ctx)

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString()
        invalidAuth = InvalidAuth()

        binding.viewProfile.setOnClickListener(this)
        binding.addressLayout.setOnClickListener(this)
        binding.searchEdt.setOnClickListener(this)
        binding.notiIcon.setOnClickListener(this)
        binding.virtualTv.setOnClickListener(this)
        binding.inPersonTv.setOnClickListener(this)
        ((ctx as MainActivity)).showBottomNavigation()

        layoutManager = LinearLayoutManager(ctx)
        binding.artistRecycler.layoutManager = layoutManager
        binding.artistRecycler.setHasFixedSize(true)
        artistListAdapter =
            ArtistListAdapter(ctx, ArrayList(), this@HomeFragment, this@HomeFragment)
        binding.artistRecycler.adapter = artistListAdapter

        binding.artistRecycler.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                if (Constants.SHOW_TYPE == context?.resources?.getString(R.string.digital)) {
                    artistListApiWithoutLatlng(search)
                } else
                    artistListApi(latitude.toString(), longitude.toString(), search)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })


        return view
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        val permissionLocation = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION)
//        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
////            getMyLocation()
//        }
//    }


    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
////            getMyLocation()
//        }
//    }
//    }
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
            R.id.virtualTv -> {

                artistListAdapter?.clear()
                binding.artistNotFoundLayout.visibility = View.GONE

                Constants.SHOW_TYPE = context?.resources?.getString(R.string.digital)!!
                binding.virtualTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_blue)
                binding.inPersonTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                binding.addressLayout.visibility = View.GONE
                artistListApiWithoutLatlng(search)
            }
            R.id.inPersonTv -> {

                Constants.IS_SWITCH_TO_VIRTUAL = false

                artistListAdapter?.clear()
                binding.artistNotFoundLayout.visibility = View.GONE
                Constants.SHOW_TYPE = context?.resources?.getString(R.string.live)!!
                binding.virtualTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                binding.inPersonTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_pink)
                binding.addressLayout.visibility = View.VISIBLE

                if (locationList.isNotEmpty()) {
                    artistListApi(locationList[0].latitude, locationList[0].longitude, search)
                } else {
                    val fragment = LocationFragment()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameContainer, fragment)
                    transaction?.commit()
                }

            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("fragment")
        transaction?.commit()
    }

    // Get Profile Api....
    private fun getProfileApi() {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.getProfileApi(shared.getString(Constants.DataKey.AUTH_VALUE))
            .enqueue(object : Callback<ViewProfileModel> {
                override fun onResponse(
                    call: Call<ViewProfileModel>,
                    response: Response<ViewProfileModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            userModel = response.body()?.data?.user

                            if (userModel != null) {

                                userImage =
                                    Constants.ImageUrl.BASE_URL + Constants.ImageUrl.USER_IMAGE_URL + userModel?.image
                                userName = userModel?.name.toString()
                                userEmail = userModel?.email.toString()
                                Picasso.get().load(userImage)
                                    .resize(4000, 1500)
                                    .placeholder(R.drawable.profile_pholder)
                                    .onlyScaleDown()
                                    .into(binding.dashUserImg)

                                shared.setString(
                                    Constants.DataKey.USER_IMAGE,
                                    Constants.ImageUrl.BASE_URL + Constants.ImageUrl.USER_IMAGE_URL
                                            + userModel?.image
                                ) // is  used to store user image.
                                shared.setString(
                                    Constants.DataKey.USER_NAME,
                                    userModel?.name
                                ) // is used to store user name.
                                shared.setString(
                                    Constants.DataKey.USER_EMAIL,
                                    userModel?.email
                                ) // is used to store user email.

                                if (prefManager?.getString1(Constants.DataKey.CURRENCY) == "") {
                                    popupSelectCurrency()
                                } else {
                                    locationListApi()
                                }

                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
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

                override fun onFailure(call: Call<ViewProfileModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })

    }

    // location list api....
    private fun locationListApi() {

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

                            binding.viewProfile.visibility = View.VISIBLE

                            locationList.clear()
                            locationList = response.body()?.data!!
                            if (locationList.isNotEmpty()) {

                                if (Constants.SAVED_LOCATION) {
                                    binding.yourLocationInfo.text =
                                        shared.getString(Constants.ADDRESS)
                                } else {
                                    binding.yourLocationInfo.text = locationList[0].street_address
                                }
                            }
                            if (Constants.SHOW_TYPE == "") {
                                binding.virtualTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                                binding.inPersonTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                                popupShowType()

                            } else if (Constants.SHOW_TYPE == ctx.resources.getString(R.string.digital)) {
                                binding.virtualTv.background =
                                    ContextCompat.getDrawable(
                                        ctx,
                                        R.drawable.corner_round_5_blue
                                    )
                                binding.inPersonTv.background =
                                    ContextCompat.getDrawable(
                                        ctx,
                                        R.drawable.corner_round_5_grey
                                    )
                                binding.addressLayout.visibility = View.GONE
                                artistListApiWithoutLatlng(search)
                            } else {
                                if (locationList.size > 0) {

                                    binding.addressLayout.visibility = View.VISIBLE
                                    binding.virtualTv.background =
                                        ContextCompat.getDrawable(
                                            ctx,
                                            R.drawable.corner_round_5_grey
                                        )
                                    binding.inPersonTv.background =
                                        ContextCompat.getDrawable(
                                            ctx,
                                            R.drawable.corner_round_5_pink
                                        )
                                    artistListApi(
                                        locationList[0].latitude,
                                        locationList[0].longitude,
                                        search
                                    )
                                } else {
                                    binding.addressLayout.visibility = View.GONE
                                    binding.virtualTv.background =
                                        ContextCompat.getDrawable(
                                            ctx,
                                            R.drawable.corner_round_5_blue
                                        )
                                    binding.inPersonTv.background =
                                        ContextCompat.getDrawable(
                                            ctx,
                                            R.drawable.corner_round_5_grey
                                        )
                                    artistListApiWithoutLatlng(search)
                                }
                            }

                        }
                    } else {
                        // When Api will not successfull then error to display in json
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

                override fun onFailure(call: Call<LocationListModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    if (t is SocketTimeoutException)
                        Toast.makeText(ctx, "" + Constants.TIME_OUT, Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }   // End of location list api....

    // artist list api with latitude, longitude....
    private fun artistListApi(lat: String, lng: String, search: String) {

        if (currentPage == 1)
            binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.artistListApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, "", lat, lng, search, currentPage.toString()
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

                                binding.searchCard.visibility = View.VISIBLE
                                binding.artistNotFoundLayout.visibility = View.GONE

                            } else {
                                binding.searchCard.visibility = View.GONE
                                binding.artistNotFoundLayout.visibility = View.VISIBLE

                            }
                        }
                    } else {

                        invalidAuth?.invalidAuth(requireActivity(), response.code(), isInvalidAuth)

                    }
                }

                override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    // artist list api without latitude, longitude....
    private fun artistListApiWithoutLatlng(search: String) {

        if (currentPage == 1)
            binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.artistListApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, "", search, currentPage.toString()
        )
            .enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

//                            invalidAuth?.invalidAuth(requireActivity(),401,isInvalidAuth)
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
                    } else {

                        response.code() == 401
                        if (response.code() == 401) {
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
        Constants.IS_ADDED_TO_BACKSTACK = true
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
        val fragment = SelectDateFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("homeFragment")
        transaction?.commit()
    }

    // popup will display to select show type.
    private fun popupShowType() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.popup_show_type, null)
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

        val virtual: MaterialButton = popUp.findViewById(R.id.virtualSelectBtn)
        val live: MaterialButton = popUp.findViewById(R.id.inPersonSelectBtn)
        val showTypeLayout: ConstraintLayout = popUp.findViewById(R.id.showTypeLayout)
        val virtualLayout: ConstraintLayout = popUp.findViewById(R.id.virtualLayout)
        val liveLayout: ConstraintLayout = popUp.findViewById(R.id.liveLayout)

        virtual.setOnClickListener {
            virtualLayout.visibility = View.VISIBLE
            showTypeLayout.visibility = View.GONE

            Handler().postDelayed({
                popUpWindow.dismiss()
                artistListApiWithoutLatlng(search)
                Constants.SHOW_TYPE = ctx.resources.getString(R.string.digital)
                binding.virtualTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_blue)
                binding.inPersonTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                binding.addressLayout.visibility = View.GONE
            }, 3000)
        }
        live.setOnClickListener {
            liveLayout.visibility = View.VISIBLE
            showTypeLayout.visibility = View.GONE
            Handler().postDelayed({
                popUpWindow.dismiss()
                Constants.SHOW_TYPE = ctx.resources.getString(R.string.live)
                binding.inPersonTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_pink)
                binding.virtualTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                if (locationList.size > 0) {
                    artistListApi(locationList[0].latitude, locationList[0].longitude, search)
                    binding.addressLayout.visibility = View.VISIBLE
                } else {
                    val fragment = LocationFragment()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameContainer, fragment)
                    transaction?.commit()
                }
            }, 3000)

        }
    }

    // popup will display to select currency.
    private fun popupSelectCurrency() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.popup_select_currency, null)
        popUpWindowCurrency = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindowCurrency?.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popUpWindowCurrency?.elevation = 10f
        }
        popUpWindowCurrency?.isTouchable = false
        popUpWindowCurrency?.isOutsideTouchable = false

        currencyRecycler = popUp.findViewById(R.id.recyclerCurrency)

        val proceedBtn = popUp.findViewById<MaterialButton>(R.id.proceedBtn)


//        popUpWindowCurrency?.setOnDismissListener {
//            Handler().postDelayed({
//                popupShowType()
//            }, 1000)
//        }


        val layoutManager = LinearLayoutManager(ctx)
        currencyRecycler?.layoutManager = layoutManager


        // calling Currency List api....
        currencyListApi()

        proceedBtn.setOnClickListener {
            popUpWindowCurrency?.dismiss()
            // hit Update profile api to save currency ....

            updateProfileApi(currencyvalue)
            prefManager?.setString1(Constants.DataKey.CURRENCY, currencyvalue)

        }

    }

    private fun currencyListApi() {
        RetrofitClient.api.currencyList(shared.getString(Constants.DataKey.AUTH_VALUE))
            .enqueue(object : Callback<CurrencyListModel>, CurrencyAdapter.CurrencyAdpClick {
                override fun onResponse(
                    call: Call<CurrencyListModel>,
                    response: Response<CurrencyListModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            currencyList = response.body()?.data?.list!!
                            if (currencyList.isNotEmpty()) {

                                currencyAdapter = CurrencyAdapter(shared, ctx, currencyList, this)
                                currencyRecycler?.adapter = currencyAdapter
                                currencyRecycler?.setHasFixedSize(true)
                            }
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

                override fun onFailure(call: Call<CurrencyListModel>, t: Throwable) {
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun currencyClick(currency: String) {

                    currencyvalue = currency

                }

            })
    }

    private fun updateProfileApi(currency: String) {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.updateProfileApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            currency
        )
            .enqueue(object : Callback<UpdateProfileModel> {
                override fun onResponse(
                    call: Call<UpdateProfileModel>,
                    response: Response<UpdateProfileModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            shared.setString(
                                Constants.DataKey.USER_IMAGE,
                                Constants.ImageUrl.BASE_URL + Constants.ImageUrl.USER_IMAGE_URL + response.body()?.data?.user?.image
                            )
                            Handler().postDelayed({
                                locationListApi()
                            }, 1000)

                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()

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

                override fun onFailure(call: Call<UpdateProfileModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                }
            })
    }

    // Display popup while Invalid auth will come.
    private fun invalidAuthPopUp() {

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
                val intent = Intent(ctx, LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            } else {
                popUpWindowReport.dismiss()
            }
        }
    }


}