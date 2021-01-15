package com.surpriseme.user.activity.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.signuptype.SignUpTypeActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private var username = ""
    private var email = ""
    private var password = ""
    private var confirmPassword = ""
    private lateinit var checkValidEmail: CheckValidEmail
    private lateinit var shared: PrefrenceShared
    private var prefManager: PrefManger?=null
    private var backpress:MaterialTextView?=null
    private var fbName = ""
    private var fbEmail = ""
    private var fbtoken = ""

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
        setContentView(R.layout.activity_sign_up)

        shared = PrefrenceShared(this@SignUpActivity)
        prefManager = PrefManger(this@SignUpActivity)

        inIt()
        checkValidEmail = CheckValidEmail()
    }

    private fun inIt() {
        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(this@SignUpActivity)

        signupBtn.setOnClickListener(this)
        backToLoginBtn.setOnClickListener(this)
        passEyeDisable.setOnClickListener(this)
        passEyeEnable.setOnClickListener(this)
        confirmPassEyeEnable.setOnClickListener(this)
        confirmPassEyeDisable.setOnClickListener(this)
        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)

        if (fbName !="") {
            useredt.setText(fbName)
        }
        if (fbEmail !="") {
            emailEdt.setText(fbEmail)
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                val intent = Intent(this@SignUpActivity, SignUpTypeActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.signupBtn -> {
                validation()
            }
            R.id.backToLoginBtn -> {
                finish()
            }
            R.id.confirmPassEyeDisable -> {
                confirmPassEdt.transformationMethod = null
                confirmPassEyeEnable.visibility = View.VISIBLE
                confirmPassEyeDisable.visibility = View.GONE
            }
            R.id.confirmPassEyeEnable -> {
                confirmPassEdt.transformationMethod = PasswordTransformationMethod()
                confirmPassEyeEnable.visibility = View.GONE
                confirmPassEyeDisable.visibility = View.VISIBLE
            }
            R.id.passEyeDisable -> {
                passwordEdt?.transformationMethod = null
                passEyeDisable?.visibility = View.GONE
                passEyeEnable?.visibility = View.VISIBLE
            }
            R.id.passEyeEnable -> {
                passwordEdt?.transformationMethod = PasswordTransformationMethod()
                passEyeEnable?.visibility = View.GONE
                passEyeDisable?.visibility = View.VISIBLE
            }
//            R.id.confirmPassEyeDisable -> {
//                confirmPassEdt.transformationMethod = null
//                confirmPassEyeEnable.visibility = View.VISIBLE
//                confirmPassEyeDisable.visibility = View.GONE
//            }
//            R.id.confirmPassEyeEnable -> {
//                confirmPassEdt.transformationMethod = PasswordTransformationMethod()
//                confirmPassEyeEnable.visibility = View.GONE
//                confirmPassEyeDisable.visibility = View.VISIBLE
//            }



        }
    }

    private fun validation() {

        username = useredt.text.toString().trim()
        email = emailEdt.text.toString().trim()
        password = passwordEdt.text.toString().trim()
        confirmPassword = confirmPassEdt.text.toString().trim()

        if (username.isEmpty()) {
            Utility.alertErrorMessage(this@SignUpActivity,getString(R.string.enter_your_user_name))
        } else if (email.isEmpty()) {
            Utility.alertErrorMessage(this@SignUpActivity, getString(R.string.enter_your_mail))
        } else if (!checkValidEmail.isValidEmail(email)) {
            Utility.alertErrorMessage(this@SignUpActivity,getString(R.string.please_enter_valid_email))
        } else if (password.isEmpty()) {
            Utility.alertErrorMessage(this@SignUpActivity, getString(R.string.enter_your_password))
        } else if (confirmPassword.isEmpty()) {
            Utility.alertErrorMessage(this@SignUpActivity, getString(R.string.enter_your_confirm_password))
        } else if (password != confirmPassword) {
            Utility.alertErrorMessage(this@SignUpActivity,getString(R.string.password_not_match))
        } else {
            // hit Signup api here....
            registerApi()
        }
    }

    private fun registerApi() {
        shared.setString(Constants.FB_TOKEN, fbtoken)   // To save user token
        loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.registerApi(
            username,
            email,
            password,
            Constants.DataKey.DEVICE_TYPE_VALUE,
           fbtoken,
            prefManager?.getString1("language")!!
        )
            .enqueue(object : Callback<RegisterModel> {
                override fun onResponse(
                    call: Call<RegisterModel>,
                    response: Response<RegisterModel>
                ) {
                    loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            shared.setString(Constants.DataKey.AUTH_VALUE, Constants.BEARER + response.body()?.data?.token!!)   // To save Auth token
                            shared.setString(Constants.DataKey.ARTIST_ID_VALUE,response.body()?.data?.user?.id.toString())    // To Save Artist ID
                            shared.setString(Constants.DataKey.OLD_PASS_VALUE, password) // To save User Password
                            prefManager?.setString1(Constants.DataKey.CURRENCY, "") // to save currency....
                            prefManager?.setInt("myCurrencyAdp",-1) // to set initial currency to 0 position....



                            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
//                            intent.putExtra("currency",true)
                            startActivity(intent)
                            finishAffinity()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@SignUpActivity, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(this@SignUpActivity, "" + Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<RegisterModel>, t: Throwable) {
                    loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@SignUpActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


}