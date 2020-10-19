package com.surpriseme.user.activity.mainactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.surpriseme.user.R
import com.surpriseme.user.fragments.artistbookingdetail.ArtistBookingFragment
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailFragment
import com.surpriseme.user.fragments.bookingfragment.BookingFragment
import com.surpriseme.user.fragments.chatfragment.ChatFragment
import com.surpriseme.user.fragments.homefragment.HomeFragment
import com.surpriseme.user.fragments.viewprofile.ProfileFragment
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

        if (intent.hasExtra("bookingModel")) {
            val model = intent.getSerializableExtra("bookingModel")
            val bundle = Bundle()
            val fragment = BookingDetailFragment()
            bundle.putSerializable("bookingModel", model)
            fragment.arguments = bundle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameContainer,fragment)
            transaction.commit()
        }
        loadFragment(HomeFragment())
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {

                R.id.navHome -> {
                    Constants.SAVED_LOCATION = false
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navBooking -> {
                    loadFragment(BookingFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navChat -> {
                    loadFragment(ChatFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }

        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.addToBackStack("main")
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