package com.surpriseme.user.activity.settings

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.chooselanguage.ChooseLanguageAdapter
import com.surpriseme.user.activity.chooselanguage.LanguageModel
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.ActivitySettingsBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.retrofit.RetrofitInterface
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefManger
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SettingsActivity : AppCompatActivity(), View.OnClickListener,
    ChooseLanguageAdapter.ChangeLocale {

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
    private var languageList:ArrayList<LanguageModel> = ArrayList()
    private var mGetLanguage = ""
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
        loadingText.text  = Utility.randomString()

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
                category()
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
                category()
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
                category()
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
                mSettings = false
                mSettingsLayout = false
                binding?.chooseLangLayout?.visibility = View.VISIBLE
                initializeRecycler()

            }
            R.id.changeLanguageBtn -> {

                // change language api call....
                binding?.chooseLangLayout?.visibility = View.GONE
                changeLanguageBtn?.isEnabled = false
                changeLanguageApi(mGetLanguage)
                Handler().postDelayed({
                    changeLanguageBtn?.isEnabled = true
                },2000)

            }
            R.id.languageBackpress -> {
                binding?.chooseLangLayout?.visibility = View.GONE
            }
        }
    }

    // Change language api call....
    private fun changeLanguageApi(language: String){

        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.updateLanguageApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,language)
            .enqueue(object :Callback<UpdateLanguageModel> {
                override fun onResponse(
                    call: Call<UpdateLanguageModel>,
                    response: Response<UpdateLanguageModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() !=null){
                            val intent = Intent(this@SettingsActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else{
                        val jsonobject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonobject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonobject.getString(Constants.ERROR)
                                Toast.makeText(this@SettingsActivity,"" + errorMessage,Toast.LENGTH_SHORT).show()
                            }catch (e:JSONException) {
                                Toast.makeText(this@SettingsActivity,"" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<UpdateLanguageModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(this@SettingsActivity,"" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun initializeRecycler() {

        val layoutManager = LinearLayoutManager(this@SettingsActivity)
        languageRecycler?.layoutManager = layoutManager
        languageRecycler?.setHasFixedSize(true)
        languageList.clear()
        displayLanguage()
    }

    private fun displayLanguage() {
        languageList.add(LanguageModel("English", "en"))
        languageList.add(LanguageModel("Dutch", "nl"))
        languageList.add(LanguageModel("German", "de"))
        languageList.add(LanguageModel("Spanish", "es"))

        val adapter = ChooseLanguageAdapter(this@SettingsActivity, languageList,this)
        languageRecycler?.adapter = adapter
        languageRecycler?.setHasFixedSize(true)
    }

//    fun setLocale(lang: String?) {
//
//        val config = resources.configuration
//        val locale = Locale(lang!!)
//        Locale.setDefault(locale)
//        config.locale = locale
//        resources.updateConfiguration(config, resources.displayMetrics)
////        val myLocale = Locale(lang)
////        val res: Resources = resources
////        val dm: DisplayMetrics = res.getDisplayMetrics()
////        val conf: Configuration = res.getConfiguration()
////        conf.locale = myLocale
////        res.updateConfiguration(conf, dm)
////        val refresh = Intent(this, LoginActivity::class.java)
////        finish()
////        startActivity(refresh)
//    }

    // choose Language adapter override function.....
    override fun language(language: String) {
        mGetLanguage = language
//        setLocale(language)
    }

    // Calling Terms and Conditions api....
    private fun category() {

        binding!!.loaderLayout.visibility=View.VISIBLE

        val call: Call<TermAndConditionsModel>

        if(type == getString(R.string.about_us)) {
            val apiInterface1 =
                RetrofitClient.getRetrofitInstance()!!.create(RetrofitInterface::class.java)
            call = apiInterface1.aboutus()
        }
        else if(type == getString(R.string.privacy_policy)){
            val apiInterface1 =
                RetrofitClient.getRetrofitInstance()!!.create(RetrofitInterface::class.java)
            call = apiInterface1.privacypolicy()
        }
        else{
            val apiInterface1 =
                RetrofitClient.getRetrofitInstance()!!.create(RetrofitInterface::class.java)
            call = apiInterface1.term()
        }
        call.enqueue(object : Callback<TermAndConditionsModel> {

            override fun onResponse(call: Call<TermAndConditionsModel>, response: Response<TermAndConditionsModel>) {
                binding!!.loaderLayout.visibility=View.GONE

                if (response.body() != null) {
                    if (response.isSuccessful) {

                        if(type == getString(R.string.about_us)) {
                            detailTxt.text = response.body()!!.data.config
                        }
                        else if(type == getString(R.string.privacy_policy)){
                            detailTxt.text = Html.fromHtml( response.body()!!.data.config).toString()
                        }
                        else{
                            detailTxt.text = Html.fromHtml( response.body()!!.data.config).toString()
                        }


                    }
                }
            }

            override fun onFailure(call: Call<TermAndConditionsModel>, t: Throwable) {
                binding!!.loaderLayout.visibility=View.GONE

                Toast.makeText(this@SettingsActivity, "" + t.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }

        })
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