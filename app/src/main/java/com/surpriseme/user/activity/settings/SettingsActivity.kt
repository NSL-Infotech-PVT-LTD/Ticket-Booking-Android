package com.surpriseme.user.activity.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.chooselanguage.ChooseLanguageActivity
import com.surpriseme.user.activity.termprivacyhelp.TermPrivacyHelpActivity
import com.surpriseme.user.databinding.ActivitySettingsBinding
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefManger
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.activity_login.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var detailTxt:MaterialTextView
    private lateinit var tbackpress:MaterialTextView
    private var languageRecycler:RecyclerView?=null
    private var changeLanguageBtn:MaterialButton?=null
    private var languageBackpress:ImageView?=null
    private var prefManager:PrefManger?=null
    private var shared:PrefrenceShared?=null
    private var languageWindow:PopupWindow?=null
    private var binding:ActivitySettingsBinding?=null

    override fun onStart() {
        languageWindow?.dismiss()
        super.onStart()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_settings)

        binding = DataBindingUtil.setContentView(this@SettingsActivity,R.layout.activity_settings)
        shared = PrefrenceShared(this@SettingsActivity)
        prefManager = PrefManger(this@SettingsActivity)

        init()

    }

    private fun init() {

        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(this@SettingsActivity)


        tbackpress = findViewById(R.id.backpress)
        binding?.termConditionTxt?.setOnClickListener(this)
        binding?.privacyPolicyTxt?.setOnClickListener(this)
        binding?.aboutUsTxt?.setOnClickListener(this)
        tbackpress.setOnClickListener(this)
        binding?.languageTxt?.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.termConditionTxt -> {
                binding?.termConditionTxt?.isEnabled = false
                val intent = Intent(this@SettingsActivity, TermPrivacyHelpActivity::class.java)
                Constants.IS_TERM_AND_CONDITION = true
                Constants.IS_PRIVACY_POLICY = false
                Constants.IS_ABOUT_US = false
                startActivity(intent)

                binding?.termConditionTxt?.postDelayed( {
                    binding?.termConditionTxt?.isEnabled = true
                },2000)

            }
            R.id.privacyPolicyTxt -> {
                binding?.privacyPolicyTxt?.isEnabled = false
                val intent = Intent(this@SettingsActivity, TermPrivacyHelpActivity::class.java)
                Constants.IS_TERM_AND_CONDITION = false
                Constants.IS_PRIVACY_POLICY = true
                Constants.IS_ABOUT_US = false
                startActivity(intent)
                binding?.privacyPolicyTxt?.postDelayed( {
                    binding?.privacyPolicyTxt?.isEnabled = true
                },2000)

            }
            R.id.aboutUsTxt -> {
                binding?.aboutUsTxt?.isEnabled = false
                val intent = Intent(this@SettingsActivity, TermPrivacyHelpActivity::class.java)
                Constants.IS_TERM_AND_CONDITION = false
                Constants.IS_PRIVACY_POLICY = false
                Constants.IS_ABOUT_US = true
                startActivity(intent)
                binding?.aboutUsTxt?.postDelayed( {
                    binding?.aboutUsTxt?.isEnabled = true
                },2000)

            }
            R.id.backpress -> { finish() }
            R.id.languageTxt -> {

//                popupLanguageDisable()
//                 change language api call....
                val intent = Intent(this@SettingsActivity, ChooseLanguageActivity::class.java)
                intent.putExtra("setting","setting")
                startActivity(intent)
            }
        }
    }

    // popup will display to select currency.
    private fun popupLanguageDisable() {

        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.popup_language_coming_soon_layout, loginContainer,false)
        languageWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        languageWindow?.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            languageWindow?.elevation = 10f
        }
        languageWindow?.isTouchable = false
        languageWindow?.isOutsideTouchable = false
        val goBackTxt = popUp.findViewById<TextView>(R.id.goBackTxt)
        goBackTxt.setOnClickListener {
            languageWindow?.dismiss()
        }




    }


    override fun onBackPressed() {
            finish()
    }



}