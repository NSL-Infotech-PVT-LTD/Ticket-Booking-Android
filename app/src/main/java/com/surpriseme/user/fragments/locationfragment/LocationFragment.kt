package com.surpriseme.user.fragments.locationfragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentLocationBinding
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.fragments.googlemapfragment.MapFragment
import com.surpriseme.user.fragments.homefragment.HomeFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


class LocationFragment : Fragment(), View.OnClickListener,
    LocationListAdapter.DisplayAddToDashboard, LocationListAdapter.DeleteAddress,
    LocationListAdapter.EditAddress {

    private lateinit var binding: FragmentLocationBinding
    private lateinit var tbackpress: ImageView
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var locationList: ArrayList<LocationDataList> = ArrayList()
    private var addressDashboard = ""
    private var latitude:String = ""
    private var longitude:String = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)

        tbackpress = view.findViewById(R.id.tbackpress) // Toolbar back arrow click listner....
        tbackpress.setOnClickListener(this) //  Initializing the Toolbar
        binding.addAddressBtn.setOnClickListener(this) //  Initializing the Add Address Button click

        init()
        return view
    }

    private fun init() {


        ((ctx as MainActivity)).hideBottomNavigation() // Hide bottom navigation on Location Fragment....
        initializeLocationRecycler()    // Initializing Location Recycler View....
        locationListApi()

    }

    private fun initializeLocationRecycler() {
        binding.refresh.setOnClickListener(this)
        val layoutManager =
            LinearLayoutManager(ctx)    // Getting Instance of Linear layout manager for recycler view....
        binding.locationRecycler.layoutManager =
            layoutManager  // Setting layout manager to recycler view....
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.tbackpress -> {

                replaceFragment(HomeFragment())
            }
            R.id.addAddressBtn -> {
                Constants.WantToUpdateAddress = false
                replaceFragment(SelectLocationTypeFragment())

            }
            R.id.refresh -> {
                binding.noDataFound.visibility = View.GONE
                binding.refresh.visibility = View.GONE
                locationListApi()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("LocationManager")
        transaction?.commit()
    }

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

                            locationList.clear()
                            locationList = response.body()?.data!!
                            if (locationList.isNotEmpty()) {

                                // When location list will not empty then display location list to Location recycler view....
                                binding.noDataFound.visibility = View.GONE
                                binding.refresh.visibility = View.GONE
                                val locationListAdapter =
                                    LocationListAdapter(
                                        ctx,
                                        locationList,
                                        this@LocationFragment,
                                        this@LocationFragment,
                                        this@LocationFragment
                                    )
                                binding.locationRecycler.adapter = locationListAdapter
                            } else {
                                // When location list will empty then display No data found and refresh button will display
                                binding.noDataFound.visibility = View.VISIBLE
                                binding.refresh.visibility = View.VISIBLE
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

    override fun dispAddressDashboard(address: String,lat:String,lng:String) {

        Constants.SAVED_LOCATION = true
        addressDashboard = address
        latitude = lat
        longitude = lng
        shared.setString(Constants.ADDRESS, addressDashboard)
        shared.setString(Constants.LATITUDE, latitude)
        shared.setString(Constants.LONGITUDE, longitude)

        val fragment = HomeFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.commit()
    }

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
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT).show()
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

        val popUp: View = layoutInflater.inflate(R.layout.lopout_popup, null)
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

        val dispMsgForPopUp = "Do you Want to delete?"

        val ok: MaterialTextView = popUp.findViewById(R.id.okTv)
        val cancelTv: MaterialTextView = popUp.findViewById(R.id.cancelTv)
        val messageTxt: MaterialTextView = popUp.findViewById(R.id.congratsMsgTv)
        messageTxt.text = dispMsgForPopUp

        ok.setOnClickListener {
            deleteAddressApi(id)
            popUpWindow.dismiss()
        }
        cancelTv.setOnClickListener {
            popUpWindow.dismiss()
        }
    }   // End of Alert popup window....

    override fun updateAddress(locationDataList: LocationDataList) {

        Constants.WantToUpdateAddress = true
        Constants.WantOtherLocation = false
        Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show()
        val fragment = MapFragment()
        val bundle = Bundle()
        bundle.putSerializable("addressList", locationDataList)
        fragment.arguments = bundle
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("locationFragment")
        transaction?.commit()

    }

}