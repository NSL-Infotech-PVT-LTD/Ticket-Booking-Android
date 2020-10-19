package com.surpriseme.user.activity.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.surpriseme.user.R
import com.surpriseme.user.activity.splashwalkthrough.SplashGetStartedActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared

class SplashActivity : AppCompatActivity() {

     private val splashTime: Long = 3000
    private lateinit var shared:PrefrenceShared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)
        shared = PrefrenceShared(this@SplashActivity)
        init()

    }

    private fun init() {


        Handler().postDelayed({

                if (shared.getString(Constants.DataKey.AUTH_VALUE).equals("")) {

                    val intent = Intent(applicationContext, SplashGetStartedActivity ::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finishAffinity()

                } else {
                    val intent = Intent(applicationContext, MainActivity ::class.java)

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finishAffinity()
                }

            },splashTime)

    }

}