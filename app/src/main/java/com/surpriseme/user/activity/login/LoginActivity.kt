package com.surpriseme.user.activity.login

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.surpriseme.user.R
import com.surpriseme.user.activity.chooselanguage.ChooseLanguageActivity
import com.surpriseme.user.activity.forgotpassword.ForgotPasswordActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.signuptype.SignUpTypeActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var email = ""
    private var password = ""
    private lateinit var checkValidEmail: CheckValidEmail
    private lateinit var shared: PrefrenceShared
    private var fbtoken: String = ""
    private var loaderLayout: ConstraintLayout? = null
    private var prefManager: PrefManger? = null


    override fun onStart() {
        super.onStart()

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this,
                OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                    fbtoken = instanceIdResult.token

                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        shared = PrefrenceShared(this@LoginActivity)
        prefManager = PrefManger(this@LoginActivity)

        inIt()
    }

    private fun inIt() {


//        signUpTxtAtLogin.setOnClickListener(this)
        loginButton.setOnClickListener(this)
        forgetTxt.setOnClickListener(this)
        accountYetTxt.setOnClickListener(this)
        changetext.setOnClickListener(this)
        eye_disable.setOnClickListener(this)
        eye_enable.setOnClickListener(this)

        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(this@LoginActivity)

        checkValidEmail = CheckValidEmail()

        shared()
        loaderLayout = findViewById(R.id.loaderLayout)

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
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

        if (email.isEmpty()) {
            emailedt.error = getString(R.string.please_fill_require_field)
            emailedt.requestFocus()
        } else if (!checkValidEmail.isValidEmail(email)) {       // Checking for Valid Email Entered or not....
            emailedt.error = getString(R.string.please_enter_valid_email)
            emailedt.requestFocus()
        } else if (password.isEmpty()) {
            passwordEdt.error = getString(R.string.please_fill_require_field)
            passwordEdt.requestFocus()
        } else if (password.length < 8) {
            passwordEdt.error = getString(R.string.password_should_atleast_eight_character)
            passwordEdt.requestFocus()
        } else {
            // Hit login api here....
            loginApi()
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
                            )                                       // To save User Password
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
                                Snackbar.make(
                                    loginContainer,
                                    "" + errorMessage,
                                    BaseTransientBottomBar.LENGTH_SHORT
                                ).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

//    private fun getHashCode() {
//        try {
//            val info: PackageInfo = this@LoginActivity.getPackageManager().getPackageInfo(
//                "com.surpriseme.user",
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d(
//                    "KeyHash", "KeyHash:" + Base64.encodeToString(
//                        md.digest(),
//                        Base64.DEFAULT
//                    )
//                )
//                Toast.makeText(
//                    getApplicationContext(), Base64.encodeToString(
//                        md.digest(),
//                        Base64.DEFAULT
//                    ), Toast.LENGTH_LONG
//                ).show()
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//        } catch (e: NoSuchAlgorithmException) {
//        }
//    }


}