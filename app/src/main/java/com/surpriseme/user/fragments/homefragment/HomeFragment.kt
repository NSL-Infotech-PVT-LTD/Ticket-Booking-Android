package com.surpriseme.user.fragments.homefragment

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentHomeBinding
import com.surpriseme.user.activity.searchactivity.SearchActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.fragments.artistbookingdetail.ArtistBookingFragment
import com.surpriseme.user.fragments.locationfragment.LocationFragment
import com.surpriseme.user.fragments.viewprofile.ProfileFragment
import com.surpriseme.user.fragments.wayofbookingfragment.WayOfBookingFragment
import com.surpriseme.user.fragments.notificationfragment.NotificationFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), View.OnClickListener, ArtistListAdapter.ArtistListFace,
    ArtistListAdapter.BookBtnClick {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var artistList: ArrayList<DataUserArtistList> = ArrayList()
    private var latitude = ""
    private var longitude = ""
    private var search = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)

        if (shared.getString(Constants.DataKey.USER_IMAGE) !="") {
            Picasso.get().load(shared.getString(Constants.DataKey.USER_IMAGE)).placeholder(R.drawable.profile_pholder)
                .resize(4000,1500).into(binding.dashUserImg)
        }else {
            binding.dashUserImg.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.profile_pholder))
        }
        binding.viewProfile.setOnClickListener(this)
        binding.refresh.setOnClickListener(this)
        binding.addressLayout.setOnClickListener(this)
        binding.searchEdt.setOnClickListener(this)
        binding.notiIcon.setOnClickListener(this)
        ((ctx as MainActivity)).showBottomNavigation()
        initializeArtistRecycler()
        Handler().postDelayed( {
            if (Constants.SAVED_LOCATION) {
                binding.yourLocationInfo.text = shared.getString(Constants.ADDRESS)
                latitude = shared.getString(Constants.LATITUDE)
                longitude = shared.getString(Constants.LONGITUDE)
                artistListApi(latitude,longitude,search)

            }else {
                getLocation()
            }


            binding.searchEdt.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search = searchEdt.text.toString().trim()
                    artistListApi(latitude, longitude, search)
                }
                false
            }
        },2000)


        return view
    }
    // latlong
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
        if (ActivityCompat.checkSelfPermission(
                ctx,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                ctx,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermission()
        } else {
        // enableLocationSettings();
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()

                shared.setString(Constants.LATITUDE, latitude)
                shared.setString(Constants.LONGITUDE, longitude)

                val geocoder: Geocoder?
                val addresses: List<Address>?
                geocoder = Geocoder(ctx, Locale.getDefault())
                try {
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = (addresses as MutableList<Address>?)?.get(0)
                    if (address != null) {
                        val locality = address.subLocality+ ","+address.featureName+","+address.locality+","+address.adminArea+","+address.postalCode+","+address.countryName
                        binding.yourLocationInfo.text = locality
                        shared.setString(Constants.ADDRESS,locality)
                        artistListApi(latitude,longitude,search)
                    }
                } catch (e: Exception) {
                    Toast.makeText(ctx,"" +e.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        }
    }

    private fun locationPermission() {
        ActivityCompat.requestPermissions((ctx as Activity), arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), PackageManager.PERMISSION_GRANTED)
        getLocation()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.viewProfile -> {
                replaceFragment(ProfileFragment())
            }
            R.id.refresh -> {
                binding.searchEdt.setText("")
                binding.noDataFound.visibility = View.GONE
                binding.refresh.visibility = View.GONE
                search = ""
                artistListApi(latitude,longitude,search)
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

    private fun initializeArtistRecycler() {
        val layoutManager = LinearLayoutManager(ctx)
        binding.artistRecycler.layoutManager = layoutManager
    }

    private fun artistListApi(lat:String,lng:String,search:String) {

        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.artistListApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, "", lat, lng,search)
            .enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            // after api successfull do your stuff....
                            artistList = response.body()?.data?.data!!
                            if (artistList.isNotEmpty()) {
                                binding.noDataFound.visibility = View.GONE
                                binding.refresh.visibility = View.GONE
                                val adapter = ArtistListAdapter(ctx, artistList, this@HomeFragment, this@HomeFragment)
                                binding.artistRecycler.adapter = adapter
                            } else {
                                binding.noDataFound.visibility = View.VISIBLE
                                binding.refresh.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
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

    override fun artistDetailLink(artistID: String) {
        // Calling interface from Artist List adapter to send data from home fragment to ArtistDetail fragment with list on Item view click.
        shared.setString(Constants.ARTIST_ID,artistID)
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
        shared.setString(Constants.ARTIST_ID,artistID)
        val fragment = WayOfBookingFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("homeFragment")
        transaction?.commit()
    }

}