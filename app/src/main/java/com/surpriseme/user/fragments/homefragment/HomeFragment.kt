package com.surpriseme.user.fragments.homefragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
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
import com.surpriseme.user.fragments.locationfragment.LocationListModel
import com.surpriseme.user.fragments.notificationfragment.NotificationFragment
import com.surpriseme.user.fragments.selectdateofbookingfragment.SelectDateFragment
import com.surpriseme.user.fragments.viewprofile.ProfileFragment
import com.surpriseme.user.fragments.viewprofile.UserViewProfile
import com.surpriseme.user.fragments.viewprofile.ViewProfileModel
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), View.OnClickListener, ArtistListAdapter.ArtistListFace,
    ArtistListAdapter.BookBtnClick {

    private var currencyvalue = ""
    private lateinit var binding: FragmentHomeBinding
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var artistList: ArrayList<DataUserArtistList> = ArrayList()
    private var search = ""
    private var isInvalidAuth = false
    private var artistListAdapter: ArtistListAdapter? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var isLastPage = false
    private var isLoading = false
    private var currentPage: Int = 1
    private var totalPage = 0
    private var locationList: ArrayList<LocationDataList> = ArrayList()
    private var userModel: UserViewProfile? = null
    private var userImage = ""
    private var userName = ""
    private var userEmail = ""
    private var currencyList: ArrayList<CurrencyList> = ArrayList()
    private var currencyAdapter: CurrencyAdapter? = null
    private var currencyRecycler: RecyclerView? = null
    private var invalidAuth: InvalidAuth? = null
    private var prefManager: PrefManger? = null
    private var popUpWindowCurrency: PopupWindow? = null
    private var popUpShowTypeWindow: PopupWindow? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onStart() {
        super.onStart()
        popUpShowTypeWindow?.dismiss()
        popUpWindowCurrency?.dismiss()
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

        ((ctx as MainActivity)).hideBottomNavigation()

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text = Utility.randomString(ctx)
        invalidAuth = InvalidAuth()

        binding.viewProfile.setOnClickListener(this)
        binding.addressLayout.setOnClickListener(this)
        binding.searchEdt.setOnClickListener(this)
        binding.notiIcon.setOnClickListener(this)
        binding.virtualTv.setOnClickListener(this)
        binding.inPersonTv.setOnClickListener(this)

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
                if (Constants.SHOW_TYPE == "digital") {
                    artistListApiWithoutLatlng(search)
                } else
                    artistListApi(
                        shared.getString(Constants.LATITUDE),
                        shared.getString(Constants.LONGITUDE),
                        search
                    )
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
                binding.virtualTv.isEnabled = false
                Constants.SHOW_TYPE = "digital"
                binding.virtualTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_blue)
                binding.inPersonTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                binding.addressLayout.visibility = View.GONE
                currentPage = 1
                isLoading = false
                isLastPage = false
                artistListAdapter?.clear()
                artistListApiWithoutLatlng(search)
                binding.artistNotFoundLayout.visibility = View.GONE
                binding.virtualTv.postDelayed({
                    binding.virtualTv.isEnabled = true
                },2000)
            }
            R.id.inPersonTv -> {

                binding.inPersonTv.isEnabled = false
                Constants.IS_SWITCH_TO_VIRTUAL = false
                artistListAdapter?.clear()
                binding.artistNotFoundLayout.visibility = View.GONE
                Constants.SHOW_TYPE = "live"
                binding.virtualTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                binding.inPersonTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_pink)
                binding.addressLayout.visibility = View.VISIBLE
                binding.yourLocationInfo.text = shared.getString(Constants.ADDRESS)

                if (locationList.isNotEmpty()) {
                    displayAddress()
                } else {
                    val fragment = LocationFragment()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frameContainer, fragment)
                    transaction.commit()
                }

                binding.inPersonTv.postDelayed({
                    binding.inPersonTv.isEnabled = true
                },2000)



            }
        }
    }

    private fun displayAddress() {
        if (shared.getString(Constants.ADDRESS) != "") {
            binding.yourLocationInfo.text = shared.getString(Constants.ADDRESS)
            shared.setString(Constants.ADDRESS, shared.getString(Constants.ADDRESS))
            shared.setString(Constants.DEFAULT_LATITUDE, shared.getString(Constants.LATITUDE))
            shared.setString(Constants.DEFAULT_LONGITUDE, shared.getString(Constants.LONGITUDE))
            artistListApi(
                shared.getString(Constants.LATITUDE),
                shared.getString(Constants.LONGITUDE),
                search
            )
        } else {
            replaceFragment(LocationFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.addToBackStack("fragment")
        transaction.commit()
    }

    // Get Profile Api....
    private fun getProfileApi() {
        binding.loaderLayout.visibility = View.VISIBLE
        ((ctx as MainActivity)).hideBottomNavigation()
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
                                    .placeholder(R.drawable.profile_pholder)
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
                    ((ctx as MainActivity)).showBottomNavigation()
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

                            binding.homeContainer.visibility = View.VISIBLE
                            ((ctx as MainActivity)).showBottomNavigation()
//                            binding.viewProfile.visibility = View.VISIBLE

                            locationList.clear()
                            locationList = response.body()?.data!!

                            if (Constants.SHOW_TYPE == "digital") {
                                binding.addressLayout.visibility = View.GONE
                            } else {
                                binding.addressLayout.visibility = View.VISIBLE
                            }

                            if (Constants.SHOW_TYPE == "") {
                                binding.virtualTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                                binding.inPersonTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                                popupShowType()

                            } else if (Constants.SHOW_TYPE == "digital") {
                                binding.virtualTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_blue)
                                binding.inPersonTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                                binding.addressLayout.visibility = View.GONE
                                isLastPage = false
                                isLoading = false
                                currentPage = 1
                                artistListApiWithoutLatlng(search)
                            } else {
                                binding.virtualTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                                binding.inPersonTv.background =
                                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_pink)
                                if (locationList.size > 0) {
                                    Constants.SHOW_TYPE = "live"
                                    binding.inPersonTv.background = ContextCompat.getDrawable(
                                        ctx,
                                        R.drawable.corner_round_5_pink
                                    )
                                    binding.virtualTv.background = ContextCompat.getDrawable(
                                        ctx,
                                        R.drawable.corner_round_5_grey
                                    )
                                    binding.addressLayout.visibility = View.VISIBLE

                                    if (locationList.isNotEmpty()) {
                                        displayAddress()
                                    } else {
                                        val fragment = LocationFragment()
                                        val transaction =
                                            requireActivity().supportFragmentManager.beginTransaction()
                                        transaction.replace(R.id.frameContainer, fragment)
                                        transaction.commit()
                                    }
                                } else {
                                    binding.addressLayout.visibility = View.GONE
                                    binding.virtualTv.background = ContextCompat.getDrawable(
                                        ctx,
                                        R.drawable.corner_round_5_blue
                                    )
                                    binding.inPersonTv.background = ContextCompat.getDrawable(
                                        ctx,
                                        R.drawable.corner_round_5_grey
                                    )
                                    isLastPage = false
                                    isLoading = false
                                    currentPage = 1
                                    Constants.SHOW_TYPE = "digital"
                                    artistListApiWithoutLatlng(search)
                                    Toast.makeText(
                                        ctx,
                                        "" + ctx.resources.getString(R.string.no_address_right_now_switching_to_virtual),
                                        Toast.LENGTH_SHORT
                                    ).show()
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

                            callingGetPaymentKeyFunction()

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
            Constants.DataKey.CONTENT_TYPE_VALUE, "20", search, currentPage.toString()
        )
            .enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            callingGetPaymentKeyFunction()


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

                                binding.searchCard.visibility = View.VISIBLE
                                binding.artistNotFoundLayout.visibility = View.GONE

                            } else {
                                binding.searchCard.visibility = View.GONE
                                binding.artistNotFoundLayout.visibility = View.VISIBLE
                            }
                        }
                    } else {

                        if (response.code() == 401) {
                            isInvalidAuth = true
                            invalidAuthPopUp()
                        } else {

                            val jsonObject: JSONObject
                            if (response.errorBody() != null) {
                                try {
                                    jsonObject = JSONObject(response.errorBody()?.string()!!)
                                    val errorMessage = jsonObject.getString(Constants.ERRORS)
                                    Utility.alertErrorMessage(ctx, errorMessage)
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
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.addToBackStack("homeFragment")
        transaction.commit()
    }

    // Calling interface from Artist List adapter to send data from home fragment to ArtistDetail fragment with list on Book Button Click
    override fun btnClick(artistID: String) {
        shared.setString(Constants.ARTIST_ID, artistID)
        val fragment = SelectDateFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.addToBackStack("homeFragment")
        transaction.commit()
    }

    // popup will display to select show type.
    private fun popupShowType() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View =
            layoutInflater.inflate(R.layout.popup_show_type, binding.homeContainer, false)
        popUpShowTypeWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpShowTypeWindow?.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popUpShowTypeWindow?.elevation = 10f
        }
        popUpShowTypeWindow?.isTouchable = false
        popUpShowTypeWindow?.isOutsideTouchable = false

        val virtual: TextView = popUp.findViewById(R.id.virtualSelectBtn)
        val live: TextView = popUp.findViewById(R.id.inPersonSelectBtn)
        val showTypeLayout: ConstraintLayout = popUp.findViewById(R.id.showTypeLayout)
        val virtualLayout: ConstraintLayout = popUp.findViewById(R.id.virtualLayout)
        val liveLayout: ConstraintLayout = popUp.findViewById(R.id.liveLayout)

        virtual.setOnClickListener {
            virtualLayout.visibility = View.VISIBLE
            showTypeLayout.visibility = View.GONE
            liveLayout.visibility = View.GONE

            Handler().postDelayed({
                popUpShowTypeWindow?.dismiss()
                artistListApiWithoutLatlng(search)
                Constants.SHOW_TYPE = "digital"
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
            virtualLayout.visibility = View.GONE
            Handler().postDelayed({
                popUpShowTypeWindow?.dismiss()
                Constants.SHOW_TYPE = "live"
                binding.inPersonTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_pink)
                binding.virtualTv.background =
                    ContextCompat.getDrawable(ctx, R.drawable.corner_round_5_grey)
                binding.addressLayout.visibility = View.VISIBLE

                if (locationList.isNotEmpty()) {
                    displayAddress()
                } else {
                    val fragment = LocationFragment()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frameContainer, fragment)
                    transaction.commit()
                }
            }, 3000)

        }
    }

    // popup will display to select currency.
    private fun popupSelectCurrency() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View =
            layoutInflater.inflate(R.layout.popup_select_currency, binding.homeContainer, false)
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

        val layoutManager = LinearLayoutManager(ctx)
        currencyRecycler?.layoutManager = layoutManager
        // calling Currency List api....
        currencyListApi()
        proceedBtn.setOnClickListener {

            if (prefManager?.getInt("myCurrencyAdp") == -1) {
                Utility.alertErrorMessage(
                    ctx,
                    ctx.resources.getString(R.string.please_choose_currency)
                )
            } else {
                popUpWindowCurrency?.dismiss()
                // hit Update profile api to save currency ....
                updateProfileApi(currencyvalue)
                prefManager?.setString1(Constants.DataKey.CURRENCY, currencyvalue)
            }
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

                                currencyAdapter =
                                    CurrencyAdapter(prefManager!!, ctx, currencyList, this)
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

        val popUp: View =
            layoutInflater.inflate(R.layout.alert_popup_layout, binding.homeContainer, false)
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

    // Api to get Payment (Published key)....
    private fun getPaymentKeyApi() {

        RetrofitClient.api.getPaymentKeyApi(shared.getString(Constants.DataKey.AUTH_VALUE))
            .enqueue(object : Callback<PaymentConfigModel> {
                override fun onResponse(
                    call: Call<PaymentConfigModel>,
                    response: Response<PaymentConfigModel>
                ) {
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            Constants.PUBLIC_KEY = response.body()?.data?.public_key!!
                            Constants.SECRET_KEY = response.body()?.data?.seceret_key!!
                            Constants.CLIENT_ID = response.body()?.data?.client_id!!
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<PaymentConfigModel>, t: Throwable) {
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun callingGetPaymentKeyFunction() {
        Handler().postDelayed({
            getPaymentKeyApi()
        }, 3000)
    }


}