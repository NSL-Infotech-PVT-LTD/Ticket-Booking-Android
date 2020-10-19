package com.surpriseme.user.activity.splashwalkthrough

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash_get_started.*
import java.util.*
import kotlin.collections.ArrayList


class SplashGetStartedActivity : AppCompatActivity() {

    private lateinit var imageSliderPager: ViewPager
    private var currentPage = 0
    private var numPager = 0
    private val imageSlide = arrayOf(
        R.drawable.slider1,
        R.drawable.slider2,
        R.drawable.slider3
    )
    private val textSlide = arrayOf(
        R.string.title_text_1,
        R.string.title_text_2,
        R.string.title_text_3
    )
    private val imagesArray = ArrayList<Int>()
    private var textArray = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_get_started)

        textArray.add(getString(R.string.title_text_1))
        textArray.add(getString(R.string.title_text_2))
        textArray.add(getString(R.string.title_text_3))

        // Adding Walk Through screen images here in array list...
        for(element in imageSlide){
            imagesArray.add(element)



        imageSliderPager = findViewById(R.id.imageSlider)
            imageSliderPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
            override fun onPageSelected(position: Int) {
                currentPage = position
            }
        })}
        imageSliderPager.adapter = ImageAdapter(this@SplashGetStartedActivity, imagesArray,textArray )
        numPager = imagesArray.size
        indicator.setViewPager(imageSliderPager)
        // making Slider auto....
//        autoPlay()
        getStarted.setOnClickListener {
            startActivity(Intent(this@SplashGetStartedActivity, LoginActivity::class.java))

        }

    }
    private fun autoPlay() {
        val handler = Handler()
        val update = Runnable {
            if (currentPage == numPager) {
                currentPage = 0
            }
            imageSliderPager.setCurrentItem(currentPage++, true)

        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 2000, 2000)

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}