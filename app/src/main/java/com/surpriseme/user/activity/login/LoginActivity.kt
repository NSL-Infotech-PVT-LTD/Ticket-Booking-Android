package com.surpriseme.user.activity.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.chooselanguage.ChooseLanguageActivity
import com.surpriseme.user.activity.forgotpassword.ForgotPasswordActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.signuptype.SignUpTypeActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var email = ""
    private var password = ""
    private lateinit var checkValidEmail: CheckValidEmail
    private lateinit var shared: PrefrenceShared
    private var fbtoken: String = ""
    private var loaderLayout: ConstraintLayout? = null
    private var prefManager: PrefManger? = null
    private var languageWindow:PopupWindow?=null


    override fun onStart() {
        super.onStart()

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                fbtoken = instanceIdResult.token

            }

        languageWindow?.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        shared = PrefrenceShared(this@LoginActivity)
        prefManager = PrefManger(this@LoginActivity)

        inIt()
    }

    private fun inIt() {

        Picasso.get().load(R.drawable.login_2)
            .resize(500,500)
            .onlyScaleDown()
            .into(img_sign)


//        signUpTxtAtLogin.setOnClickListener(this)
        loginButton.setOnClickListener(this)
        forgetTxt.setOnClickListener(this)
        accountYetTxt.setOnClickListener(this)
        changetext.setOnClickListener(this)
        eye_disable.setOnClickListener(this)
        eye_enable.setOnClickListener(this)
        translate_icon.setOnClickListener(this)

        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(this@LoginActivity)

        checkValidEmail = CheckValidEmail()

        shared()
        loaderLayout = findViewById(R.id.loaderLayout)

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            prefManager!!.setBoolean1(Constants.ISREMEMBER, isChecked)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginButton -> {
                validation()
            }
            R.id.forgetTxt -> {
                val forgotIntent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(forgotIntent)
            }
            R.id.accountYetTxt -> {
                val intent = Intent(this@LoginActivity, SignUpTypeActivity::class.java)
                startActivity(intent)
            }
            R.id.changetext -> {
//                popupLanguageDisable()
                val intent = Intent(this@LoginActivity, ChooseLanguageActivity::class.java)
                intent.putExtra("login","login")
                startActivity(intent)
            }
            R.id.translate_icon -> {
                val intent = Intent(this@LoginActivity, ChooseLanguageActivity::class.java)
                intent.putExtra("login","login")
                startActivity(intent)
            }
            R.id.eye_disable -> {
                passwordEdt!!.transformationMethod = null
                eye_enable!!.visibility = View.VISIBLE
                eye_disable!!.visibility = View.GONE

            }
            R.id.eye_enable -> {

                passwordEdt!!.transformationMethod = PasswordTransformationMethod()
                eye_enable!!.visibility = View.GONE
                eye_disable!!.visibility = View.VISIBLE
            }
        }
    }

    private fun validation() {

        email = emailedt.text.toString().trim()
        password = passwordEdt.text.toString().trim()

        when {
            email.isEmpty() -> {
                Utility.alertErrorMessage(this@LoginActivity, getString(R.string.enter_your_mail))
            }
            //        else if (!checkValidEmail.isValidEmail(email)) {       // Checking for Valid Email Entered or not....
            //            Utility.alertErrorMessage(this@LoginActivity, getString(R.string.please_enter_valid_email))
            //        }
            password.isEmpty() -> {
                Utility.alertErrorMessage(this@LoginActivity, getString(R.string.ENTER_YOUR_PASSWRD))
            }

            else -> {
                // Hit login api here....
                loginApi()
            }
        }
    }

    private fun shared() {
        if (prefManager!!.getBoolean1(Constants.ISREMEMBER)) {
            emailedt?.setText(prefManager!!.getString1("username"))
            passwordEdt?.setText(prefManager!!.getString1("password"))
            checkbox.isChecked = true
        } else {
            checkbox.isChecked = false
        }
    }


    private fun loginApi() {

        shared.setString(Constants.FB_TOKEN, fbtoken)
        loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.loginApi(
            email,
            password,
            fbtoken,
            Constants.DataKey.DEVICE_TYPE_VALUE
        )
            .enqueue(object : Callback<Loginmodel> {
                override fun onResponse(call: Call<Loginmodel>, response: Response<Loginmodel>) {
                    loaderLayout?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            shared.setString(
                                Constants.DataKey.AUTH_VALUE,
                                Constants.BEARER + response.body()?.data?.token!!
                            )   // To save Auth token
                            shared.setString(
                                Constants.DataKey.USER_ID,
                                response.body()?.data?.user?.id.toString()
                            )    // To Save User ID
                            shared.setString(
                                Constants.DataKey.OLD_PASS_VALUE,
                                password
                            )
                            // To save User Password
                            if (response.body()?.data?.user?.image != null)
                                shared.setString(
                                    Constants.DataKey.USER_IMAGE,  // To Save User Image
                                    Constants.ImageUrl.BASE_URL +
                                            Constants.ImageUrl.USER_IMAGE_URL + response.body()?.data?.user?.image)

                            prefManager?.setString1(Constants.DataKey.CURRENCY, response.body()?.data?.user?.currency) // to save currency....


                            if (prefManager!!.getBoolean1(Constants.ISREMEMBER)) {
                                prefManager!!.setString1(
                                    "username",
                                    email
                                )
                                prefManager!!.setString1(
                                    "password",
                                    password
                                )
                            } else {
                                prefManager!!.setString1("username", "")
                                prefManager!!.setString1("password", "")
                            }
                            val mainActIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(mainActIntent)
                            finishAffinity()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Utility.alertErrorMessage(this@LoginActivity, errorMessage)
                            } catch (e: JSONException) {
                                Snackbar.make(
                                    loginContainer,
                                    "" + e.message.toString(),
                                    BaseTransientBottomBar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Loginmodel>, t: Throwable) {
                    loaderLayout?.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
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
        }




    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


}