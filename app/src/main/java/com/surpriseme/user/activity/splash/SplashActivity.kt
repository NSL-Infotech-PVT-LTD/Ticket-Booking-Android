package com.surpriseme.user.activity.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.chooselanguage.ChooseLanguageActivity
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.splashwalkthrough.SplashGetStartedActivity
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefManger
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val splashTime: Long = 3000
    private lateinit var shared:PrefrenceShared
    private lateinit var prefManager:PrefManger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)
        shared = PrefrenceShared(this@SplashActivity)
        prefManager = PrefManger(this@SplashActivity)
        setLocale(prefManager.getString1("language"))

//        FacebookSdk.sdkInitialize(getApplicationContext())
//        AppEventsLogger.activateApp(this@SplashActivity)
        init()

    }

    private fun init() {

//        Picasso.get().load(R.drawable.spalsh_bg1)
//            .resize(500,1500)
//            .onlyScaleDown()
//            .into(splashBg)

        Picasso.get().load(R.drawable.login_2)
            .resize(500,500)
            .onlyScaleDown()
            .into(splashImg)


        Handler().postDelayed({
            startActivity(intent)
            finish()
            val intent = Intent(applicationContext, SplashGetStartedActivity ::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finishAffinity()
            },splashTime)

    }

    fun setLocale(lang: String?) {

        val config = resources.configuration
        val locale = Locale(lang!!)
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
//        val myLocale = Locale(lang)
//        val res: Resources = resources
//        val dm: DisplayMetrics = res.getDisplayMetrics()
//        val conf: Configuration = res.getConfiguration()
//        conf.locale = myLocale
//        res.updateConfiguration(conf, dm)
//        val refresh = Intent(this, LoginActivity::class.java)
//        finish()
//        startActivity(refresh)
    }

}