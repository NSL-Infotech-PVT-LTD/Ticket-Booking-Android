package com.surpriseme.user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.surpriseme.user.R
import com.surpriseme.user.databinding.ActivitySettingsBinding
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var headerTxt = ""
    private lateinit var headerTv:TextView
    private lateinit var tbackpress:ImageView
    private var mSettings = false
    private var mSettingsLayout = false

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

        init()

    }

    private fun init() {
        headerTv = findViewById(R.id.headerTxt)
        tbackpress = findViewById(R.id.backpress)
        binding?.termConditionTxt?.setOnClickListener(this)
        binding?.privacyPolicyTxt?.setOnClickListener(this)
        binding?.aboutUsTxt?.setOnClickListener(this)
        tbackpress.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.termConditionTxt -> {
                binding?.termConditionTxt?.isEnabled = false
                binding?.termConditionTxt?.postDelayed( {
                    binding?.termConditionTxt?.isEnabled = true
                },2000)

                binding?.settingLayout?.visibility = View.VISIBLE
                headerTxt = getString(R.string.terms_condition)
                headerTv.text = headerTxt
                mSettings = false
                mSettingsLayout = true


            }
            R.id.privacyPolicyTxt -> {
                binding?.privacyPolicyTxt?.isEnabled = false
                binding?.privacyPolicyTxt?.postDelayed( {
                    binding?.privacyPolicyTxt?.isEnabled = true
                },2000)
                binding?.settingLayout?.visibility = View.VISIBLE
                headerTxt = getString(R.string.privacy_policy)
                headerTv.text = headerTxt
                mSettings = false
                mSettingsLayout = true
            }
            R.id.aboutUsTxt -> {
                binding?.aboutUsTxt?.isEnabled = false
                binding?.aboutUsTxt?.postDelayed( {
                    binding?.aboutUsTxt?.isEnabled = true
                },2000)
                binding?.settingLayout?.visibility = View.VISIBLE
                headerTxt = getString(R.string.about_us)
                headerTv.text = headerTxt
                mSettings = false
                mSettingsLayout = true
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
        }
    }
}