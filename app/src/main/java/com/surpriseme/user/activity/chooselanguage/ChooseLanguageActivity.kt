package com.surpriseme.user.activity.chooselanguage

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.settings.UpdateLanguageModel
import com.surpriseme.user.databinding.ActivityChooseLanguageBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefManger
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class ChooseLanguageActivity : AppCompatActivity(), View.OnClickListener, ChooseLanguageAdapter.ChangeLocale {

    private var binding:ActivityChooseLanguageBinding?=null
    private var shared:PrefrenceShared?=null
    private var layoutManager:RecyclerView.LayoutManager?=null
    private var languageList:ArrayList<LanguageModel> = ArrayList()
    private var prefManager:PrefManger?=null
    private var language=""
    private var languageWindow:PopupWindow?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choose_language)
        binding = DataBindingUtil.setContentView(
            this@ChooseLanguageActivity,
            R.layout.activity_choose_language
        )
        shared = PrefrenceShared(this@ChooseLanguageActivity)
        prefManager = PrefManger(this@ChooseLanguageActivity)

        init()

    }

    private fun init() {

        // initialize listener.....

        initializeRecycler()

    }

    private fun initializeRecycler() {

        layoutManager = LinearLayoutManager(this@ChooseLanguageActivity)
        binding?.languageRecycler?.layoutManager = layoutManager
        binding?.languageRecycler?.setHasFixedSize(true)
        binding?.goBackTxt?.setOnClickListener { finish() }


        binding?.saveButton!!.setOnClickListener {

            if (prefManager?.getInt("position") ==-1) {
                Utility.alertErrorMessage(this@ChooseLanguageActivity, getString(R.string.please_choose_language))
            } else {
                if (intent.hasExtra("splash")) {
                    val intent = Intent(this@ChooseLanguageActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (intent.hasExtra("setting")) {
                    val intent = Intent(this@ChooseLanguageActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
//                    changeLanguageApi(prefManager?.getString1("language")!!)
                } else {
                    val intent = Intent(this@ChooseLanguageActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

//            if (intent.getStringExtra("setting")!!.equals("setting")) {
//
//            }
//            else if(intent.getStringExtra("register")!!.equals("register")) {
//                val intent = Intent(this@ChooseLanguageActivity, SignUpActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//
//            else {
//                val intent = Intent(this@ChooseLanguageActivity, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//                finishAffinity()
//            }
        }
            displayLanguage()

    }
    override fun onClick(v: View?) {

        when(v?.id) {

            R.id.goBackTxt -> {
                finish()
            }
        }
    }

    private fun displayLanguage() {
        languageList.add(LanguageModel("English", "en"))
        languageList.add(LanguageModel("Dutch", "nl"))
        languageList.add(LanguageModel("German", "de"))
        languageList.add(LanguageModel("Spanish", "es"))

        val adapter = ChooseLanguageAdapter(prefManager!!,this@ChooseLanguageActivity, languageList,this)
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
        prefManager?.setString1("language",language)
        setLocale(language)
    }

    // Change language api call....
    private fun changeLanguageApi(language: String){
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.updateLanguageApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,language)
            .enqueue(object : Callback<UpdateLanguageModel> {
                override fun onResponse(
                    call: Call<UpdateLanguageModel>,
                    response: Response<UpdateLanguageModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() !=null){
                            val intent = Intent(this@ChooseLanguageActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else{
                        val jsonobject: JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonobject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonobject.getString(Constants.ERROR)
                                Toast.makeText(this@ChooseLanguageActivity,"" + errorMessage, Toast.LENGTH_SHORT).show()
                            }catch (e: JSONException) {
                                Toast.makeText(this@ChooseLanguageActivity,"" + e.message.toString(),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<UpdateLanguageModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(this@ChooseLanguageActivity,"" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
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
            finish()
        }

    }



}