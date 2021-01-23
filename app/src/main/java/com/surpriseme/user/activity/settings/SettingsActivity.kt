package com.surpriseme.user.activity.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.chooselanguage.ChooseLanguageActivity
import com.surpriseme.user.databinding.ActivitySettingsBinding
import com.surpriseme.user.util.PrefManger
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility

class SettingsActivity : AppCompatActivity(), View.OnClickListener{

    private var headerTxt = ""
    private lateinit var headerTv:MaterialTextView
    private lateinit var detailTxt:MaterialTextView
    private lateinit var headerTxtIcon:ImageView
    private lateinit var tbackpress:MaterialTextView
    private var mSettings = false
    private var mSettingsLayout = false
    private var type = ""
    private var languageRecycler:RecyclerView?=null
    private var changeLanguageBtn:MaterialButton?=null
    private var languageBackpress:ImageView?=null
    private var prefManager:PrefManger?=null
    private var shared:PrefrenceShared?=null

    override fun onStart() {
        super.onStart()
        mSettings = true
        mSettingsLayout = false
    }

    private var binding:ActivitySettingsBinding?=null
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

        headerTv = findViewById(R.id.headerTxt)
        detailTxt = findViewById(R.id.detailTxt)
        tbackpress = findViewById(R.id.backpress)
        binding?.termConditionTxt?.setOnClickListener(this)
        binding?.privacyPolicyTxt?.setOnClickListener(this)
        binding?.aboutUsTxt?.setOnClickListener(this)
        tbackpress.setOnClickListener(this)
        headerTxtIcon = findViewById(R.id.headerTxtIcon)
        binding?.languageTxt?.setOnClickListener(this)
        detailTxt.movementMethod = LinkMovementMethod.getInstance()
        languageRecycler = findViewById(R.id.languageRecycler)
        changeLanguageBtn = findViewById(R.id.changeLanguageBtn)
        changeLanguageBtn?.setOnClickListener(this)
        languageBackpress = findViewById(R.id.languageBackpress)
        languageBackpress?.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.termConditionTxt -> {
                binding?.termConditionTxt?.isEnabled = false
                binding?.settingLayout?.visibility = View.VISIBLE
                headerTxt = getString(R.string.terms_condition)
                headerTxtIcon.setImageResource(R.drawable.term_condition_icon)
                headerTv.text = headerTxt
                mSettings = false
                mSettingsLayout = true
                type = getString(R.string.terms_condition)

                binding?.termConditionTxt?.postDelayed( {
                    binding?.termConditionTxt?.isEnabled = true
                },2000)

            }
            R.id.privacyPolicyTxt -> {
                binding?.privacyPolicyTxt?.isEnabled = false
                binding?.settingLayout?.visibility = View.VISIBLE
                headerTxt = getString(R.string.privacy_policy)
                headerTxtIcon.setImageResource(R.drawable.privacy_policy_icon)
                headerTv.text = headerTxt
                mSettings = false
                mSettingsLayout = true
                type = getString(R.string.privacy_policy)

                binding?.privacyPolicyTxt?.postDelayed( {
                    binding?.privacyPolicyTxt?.isEnabled = true
                },2000)

            }
            R.id.aboutUsTxt -> {
                binding?.aboutUsTxt?.isEnabled = false
                binding?.settingLayout?.visibility = View.VISIBLE
                headerTxt = getString(R.string.about_us)
                headerTxtIcon.setImageResource(R.drawable.about_us_icon)
                headerTv.text = headerTxt
                mSettings = false
                mSettingsLayout = true
                type = getString(R.string.about_us)

                binding?.aboutUsTxt?.postDelayed( {
                    binding?.aboutUsTxt?.isEnabled = true
                },2000)

            }
            R.id.backpress -> {
                if (mSettings) {
                    finish()
                }else {
                    binding?.settingLayout?.visibility = View.GONE
                    mSettings = true
                    mSettingsLayout = false
                }
            }
            R.id.languageTxt -> {
                // change language api call....
                val intent = Intent(this@SettingsActivity, ChooseLanguageActivity::class.java)
                intent.putExtra("setting","setting")
                startActivity(intent)

            }
            R.id.changeLanguageBtn -> {


            }
            R.id.languageBackpress -> {

            }
        }
    }

    override fun onBackPressed() {
        if (mSettings) {
            finish()
        } else {
            binding?.settingLayout?.visibility = View.GONE
            mSettings = true
            mSettingsLayout = false
        }
    }


}