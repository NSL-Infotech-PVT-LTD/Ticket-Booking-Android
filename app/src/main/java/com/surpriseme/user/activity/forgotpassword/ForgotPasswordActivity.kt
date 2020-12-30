package com.surpriseme.user.activity.forgotpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.databinding.ActivityForgotPasswordBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.CheckValidEmail
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.item_custom_logout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private var binding:ActivityForgotPasswordBinding?=null
    private var email =""
    private lateinit var checkValidEmail: CheckValidEmail
    private var backpress:MaterialTextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forgot_password)
        binding = DataBindingUtil.setContentView(this@ForgotPasswordActivity,R.layout.activity_forgot_password)

        inIt()
        checkValidEmail = CheckValidEmail()

    }

    private fun inIt() {

        binding?.continueButton?.setOnClickListener(this)
        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.continueButton -> {
                email = emailedt.text.toString().trim()
                if (email.isEmpty()) {
                    binding?.emailedt?.error = getString(R.string.please_fill_require_field)
                    binding?.emailedt?.requestFocus()
                } else if (!checkValidEmail.isValidEmail(email)){
                    binding?.emailedt?.error = getString(R.string.please_enter_valid_email)
                    binding?.emailedt?.requestFocus()
                } else{
                    registerApi()
                }
            }
            R.id.backpress -> {
                finish()
            }
        }
    }

    private fun registerApi() {
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.resetPasswordApi(email)
            .enqueue(object :Callback<ResetPasswordModel> {
                override fun onResponse(
                    call: Call<ResetPasswordModel>,
                    response: Response<ResetPasswordModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {
                            val intent = Intent(this@ForgotPasswordActivity, LoginActivity ::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val jsonObject: JSONObject
                        try {
                            if (response.errorBody() !=null) {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@ForgotPasswordActivity, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }catch (e:JSONException) {
                            Toast.makeText(this@ForgotPasswordActivity, "" + Constants.SOMETHING_WENT_WRONG,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResetPasswordModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(this@ForgotPasswordActivity, "" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })

    }

}