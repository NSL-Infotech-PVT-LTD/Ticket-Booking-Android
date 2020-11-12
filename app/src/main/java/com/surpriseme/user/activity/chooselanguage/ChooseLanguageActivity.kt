package com.surpriseme.user.activity.chooselanguage

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.splashwalkthrough.SplashGetStartedActivity
import com.surpriseme.user.databinding.ActivityChooseLanguageBinding
import com.surpriseme.user.util.PrefrenceShared
import java.util.*
import kotlin.collections.ArrayList


class ChooseLanguageActivity : AppCompatActivity(), View.OnClickListener, ChooseLanguageAdapter.ChangeLocale {

    private var binding:ActivityChooseLanguageBinding?=null
    private var shared:PrefrenceShared?=null
    private var layoutManager:RecyclerView.LayoutManager?=null
    private var languageList:ArrayList<LanguageModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choose_language)
        binding = DataBindingUtil.setContentView(
            this@ChooseLanguageActivity,
            R.layout.activity_choose_language
        )
        shared = PrefrenceShared(this@ChooseLanguageActivity)

        init()

    }

    private fun init() {

        // initialize listener.....
        binding?.saveButton?.setOnClickListener(this)
        initializeRecycler()

    }

    private fun initializeRecycler() {

        layoutManager = LinearLayoutManager(this@ChooseLanguageActivity)
        binding?.languageRecycler?.layoutManager = layoutManager
        binding?.languageRecycler?.setHasFixedSize(true)
        displayLanguage()
    }
    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.saveButton -> {
                val intent = Intent(applicationContext, SplashGetStartedActivity ::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    private fun displayLanguage() {

        languageList.add(LanguageModel("English", "en"))
        languageList.add(LanguageModel("Dutch", "nl"))
        languageList.add(LanguageModel("German", "de"))
        languageList.add(LanguageModel("Spanish", "es"))

        val adapter = ChooseLanguageAdapter(this@ChooseLanguageActivity, languageList,this)
        binding?.languageRecycler?.adapter = adapter
        binding?.languageRecycler?.setHasFixedSize(true)

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

    override fun language(language: String) {
        setLocale(language)
        val intent = Intent(this@ChooseLanguageActivity,LoginActivity ::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finishAffinity()
    }




}