package com.surpriseme.user.activity.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.signuptype.SignUpTypeActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.CheckValidEmail
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
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
    private var backpress:MaterialTextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        shared = PrefrenceShared(this@SignUpActivity)

        inIt()
        checkValidEmail = CheckValidEmail()
    }

    private fun inIt() {

        signupBtn.setOnClickListener(this)
        backToLoginBtn.setOnClickListener(this)
        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)

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

        }
    }

    private fun validation() {

        username = useredt.text.toString().trim()
        email = emailEdt.text.toString().trim()
        password = passwordEdt.text.toString().trim()
        confirmPassword = confirmPassEdt.text.toString().trim()

        if (username.isEmpty()) {
            useredt.error = getString(R.string.please_fill_require_field)
            useredt.requestFocus()
        } else if (email.isEmpty()) {
            emailEdt.error = getString(R.string.please_fill_require_field)
            emailEdt.requestFocus()
        } else if (!checkValidEmail.isValidEmail(email)) {
            emailEdt.error = getString(R.string.please_enter_valid_email)
            emailEdt.requestFocus()
        } else if (password.isEmpty()) {
            passwordEdt.error = getString(R.string.please_fill_require_field)
            passwordEdt.requestFocus()
        } else if (confirmPassword.isEmpty()) {
            confirmPassEdt.error = getString(R.string.please_fill_require_field)
            confirmPassEdt.requestFocus()
        } else if (password != confirmPassword) {
            passwordEdt.error = getString(R.string.password_not_match)
            passwordEdt.requestFocus()
        } else {
            // hit Signup api here....
            registerApi()
        }
    }

    private fun registerApi() {

        loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.registerApi(
            username,
            email,
            password,
            Constants.DataKey.DEVICE_TYPE_VALUE,
            Constants.DataKey.DEVICE_TOKEN_VALUE,
            "en"
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
                            shared.setString(Constants.DataKey.OLD_PASS_VALUE, password)                                       // To save User Password
                            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
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