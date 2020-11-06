package com.surpriseme.user.activity.mainactivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.surpriseme.user.fragments.viewprofile.ProfileFragment
import com.surpriseme.user.fragments.wayofbookingfragment.WayOfBookingFragment
import com.surpriseme.user.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import net.alhazmy13.mediapicker.Image.ImagePicker

class MainActivity : AppCompatActivity() {

//    private var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var profileFragment:ProfileFragment
    private lateinit var artistBookingFragment:ArtistBookingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bottomNav.menu.findItem(R.id.navHome).setTitle("")
        bottomNav.menu.findItem(R.id.navHome).icon = ContextCompat.getDrawable(this@MainActivity,R.drawable.home_icon)
        bottomNav.menu.findItem(R.id.navBooking).setTitle(R.string.booking)
        bottomNav.menu.findItem(R.id.navBooking).icon = null
        bottomNav.menu.findItem(R.id.navChat).setTitle(R.string.chat)
        bottomNav.menu.findItem(R.id.navChat).icon = null

        if (intent.hasExtra("bookingModel")) {
            val model = intent.getSerializableExtra("bookingModel")
            val bundle = Bundle()
            val fragment = BookingDetailFragment()
            bundle.putSerializable("bookingModel", model)
            fragment.arguments = bundle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer,fragment)
            transaction.commit()
        } else if (intent.hasExtra("bookingId")) {
            val bookingID = intent.getStringExtra("bookingId")
            if (bookingID != null) {
                val bundle = Bundle()
                bundle.putString("bookingId", bookingID)
                val fragment = BookingDetailFragment()
                fragment.arguments = bundle
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameContainer, fragment)
                transaction.commit()
            }
        }else if (intent.hasExtra("chatId")) {
            val chatID = intent.getStringExtra("chatId")
            if (chatID !=null) {
                val bundle = Bundle()
                bundle.putString("chatId", chatID)
                val fragment = ChatFragment()
                fragment.arguments = bundle
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameContainer, fragment)
                transaction.commit()
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
            val fragment = WayOfBookingFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer, fragment)
            transaction.commit()
        } else {
            loadFragment(HomeFragment())
        }
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {

                R.id.navHome -> {
                    Constants.SAVED_LOCATION = false
                    loadFragment(HomeFragment())
                    bottomNav.menu.findItem(R.id.navHome).setTitle("")
                    bottomNav.menu.findItem(R.id.navHome).icon = ContextCompat.getDrawable(this@MainActivity,R.drawable.home_icon)

                    bottomNav.menu.findItem(R.id.navBooking).setTitle(R.string.booking)
                    bottomNav.menu.findItem(R.id.navBooking).icon = null

                    bottomNav.menu.findItem(R.id.navChat).setTitle(R.string.chat)
                    bottomNav.menu.findItem(R.id.navChat).icon = null

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navBooking -> {
                    Constants.SAVE_BOOKING_LIST.clear()
                    Constants.COMING_FROM_DETAIL = false
                    loadFragment(BookingFragment())
                    bottomNav.menu.findItem(R.id.navBooking).setTitle("")
                    bottomNav.menu.findItem(R.id.navBooking).icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.calender_icon)

                    bottomNav.menu.findItem(R.id.navHome).setTitle(R.string.home)
                    bottomNav.menu.findItem(R.id.navHome).icon = null

                    bottomNav.menu.findItem(R.id.navChat).setTitle(R.string.chat)
                    bottomNav.menu.findItem(R.id.navChat).icon = null

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navChat -> {
                    loadFragment(ChatListFragment())
                    bottomNav.menu.findItem(R.id.navChat).setTitle("")
                    bottomNav.menu.findItem(R.id.navChat).icon = ContextCompat.getDrawable(this@MainActivity,R.drawable.chat_icon)

                    bottomNav.menu.findItem(R.id.navHome).setTitle(R.string.home)
                    bottomNav.menu.findItem(R.id.navHome).icon = null

                    bottomNav.menu.findItem(R.id.navBooking).setTitle(R.string.booking)
                    bottomNav.menu.findItem(R.id.navBooking).icon = null
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }

        }



    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.commit()

    }


//    override fun onBackPressed() {
//
//        if (doubleBackToExitPressedOnce)
//            super.onBackPressed()
//        doubleBackToExitPressedOnce = true
//
//        Toast.makeText(
//            applicationContext,
//            "" + Constants.PLEASE_CLICK_BACK_AGAIN_To_EXIT,
//            Toast.LENGTH_SHORT
//        )
//            .show()
//        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
//
//    }

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
        val permissionLocation = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            loadFragment(HomeFragment())
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.RECOVERY_REQUEST) {

//                artistBookingFragment.getYouTubePlayerProvider()

            }
            if (data != null) {

                val mPath: ArrayList<String> = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)!!

                profileFragment.getImageBitmap(mPath)

            } else {

                Toast.makeText(applicationContext, "Unable to get image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Craate context for Profile Fragment ....
    fun profileFragmentContext(fragment: ProfileFragment) {
        this.profileFragment = fragment
    }
    fun artistBookingContext(fragment:ArtistBookingFragment){
        this.artistBookingFragment = fragment
    }

    interface SendImageBitmap {
        fun getImageBitmap(mPaths: List<String?>?)
    }

}