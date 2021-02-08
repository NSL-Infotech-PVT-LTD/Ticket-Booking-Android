package com.surpriseme.user.activity.mainactivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.surpriseme.user.R
import com.surpriseme.user.fragments.artistbookingdetail.ArtistBookingFragment
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailFragment
import com.surpriseme.user.fragments.bookingfragment.BookingFragment
import com.surpriseme.user.fragments.chatFragment.ChatFragment
import com.surpriseme.user.fragments.chatListfragment.ChatListFragment
import com.surpriseme.user.fragments.homefragment.HomeFragment
import com.surpriseme.user.fragments.locationfragment.LocationFragment
import com.surpriseme.user.fragments.selectdateofbookingfragment.SelectDateFragment
import com.surpriseme.user.fragments.viewprofile.ProfileFragment
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.activity_main.*
import net.alhazmy13.mediapicker.Image.ImagePicker


class MainActivity : AppCompatActivity() {

//    val TAG_FRAGMENT = "TAG_FRAGMEN"
    private var doubleBackToExitPressedOnce: Boolean = false
    private var profileFragment:ProfileFragment?=null
    private lateinit var locationFragment:LocationFragment
    private lateinit var artistBookingFragment:ArtistBookingFragment
    private lateinit var shared: PrefrenceShared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hashFromSHA1("8F:FB:39:D7:01:58:17:BE:F8:CD:C7:72:83:64:64:50:BE:D6:35:6B")
        shared = PrefrenceShared(this@MainActivity)
       if (intent.hasExtra("chatId")) {
            val chatID = intent.getStringExtra("chatId")
            if (chatID !=null) {
                val intent = Intent(this, ChatFragment::class.java)
                intent.putExtra("chatId", chatID)
                startActivity(intent)
            }
        } else if (intent.hasExtra("artistID")) {
            val artistID = intent.getStringExtra("artistID")
            val fragment = ArtistBookingFragment()
            val bundle = Bundle()
            bundle.putString("artistID", artistID)
            fragment.arguments = bundle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragment)
            transaction.commit()
        } else if (intent.hasExtra("artistBook")) {
            val fragment = SelectDateFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragment)
            transaction.commit()
        } else if (intent.hasExtra("bookingId")){
           Constants.BOOKING_ID = intent.getStringExtra("bookingId")!!
           val intent = Intent(this@MainActivity, BookingDetailFragment::class.java)
           startActivity(intent)
       } else {
            loadFragment(HomeFragment())
        }
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {

                R.id.navHome -> {
                    Constants.SAVED_LOCATION = false
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navBooking -> {
                    Constants.SAVE_BOOKING_LIST.clear()
                    Constants.COMING_FROM_DETAIL = false
                    loadFragment(BookingFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navChat -> {
                    loadFragment(ChatListFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }

        }
    }
    fun hashFromSHA1(sha1: String) {
        val arr = sha1.split(":").toTypedArray()
        val byteArr = ByteArray(arr.size)
        for (i in arr.indices) {
            byteArr[i] = Integer.decode("0x" + arr[i]).toByte()
        }
        Log.e("hash : ", Base64.encodeToString(byteArr, Base64.NO_WRAP))
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.commit()
    }
    override fun onBackPressed() {
        if (Constants.PROFILE_FRAGMENT) {
            profileFragment?.updateProfilePopup()
        } else if (Constants.locationList.size >0) {
            if (shared.getString(Constants.ADDRESS) == "") {
                Utility.alertErrorMessage(this, getString(R.string.SELECT_ADDRESS_ERROR))
            } else {
                replaceFragment(HomeFragment())
            }
        }else {
            replaceFragment(HomeFragment())
        }
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
    private fun replaceFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.addToBackStack("LocationManager")
        transaction.commit()
    }
    fun hideBottomNavigation() {
        bottomNav.visibility = View.GONE
    }
    fun showBottomNavigation() {
        bottomNav.visibility = View.VISIBLE
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            loadFragment(HomeFragment())
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val mPath: ArrayList<String> = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)!!
                profileFragment?.getImageBitmap(mPath)
            } else {
                Toast.makeText(applicationContext, "Unable to get image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Craate context for Profile Fragment ....
    fun profileFragmentContext(fragment: ProfileFragment) {
        this.profileFragment = fragment
    }
    fun artistBookingContext(fragment: ArtistBookingFragment){
        this.artistBookingFragment = fragment
    }
    fun locationFragmentContext(fragment: LocationFragment){
        this.locationFragment = fragment
    }
    interface SendImageBitmap {
        fun getImageBitmap(mPaths: List<String?>?)
    }

}