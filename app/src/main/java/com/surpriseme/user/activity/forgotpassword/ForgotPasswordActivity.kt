package com.surpriseme.user.activity.forgotpassword

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
        loadingText.text  = Utility.randomString(this@ForgotPasswordActivity)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.continueButton -> {
                email = emailedt.text.toString().trim()
                if (email.isEmpty()) {
                    Utility.alertErrorMessage(this@ForgotPasswordActivity, getString(R.string.enter_your_mail))
                }
//                else if (!checkValidEmail.isValidEmail(email)){
//                    Utility.alertErrorMessage(this@ForgotPasswordActivity,getString(R.string.please_enter_valid_email))
//                }
                else{
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
                            popupForgetPasswordAlert(response.body()?.data?.message.toString())
                        }
                    } else {
                        val jsonObject: JSONObject
                        try {
                            if (response.errorBody() !=null) {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Utility.alertErrorMessage(this@ForgotPasswordActivity, errorMessage.toString())
                            }
                        }catch (e:JSONException) {
                            Utility.alertErrorMessage(this@ForgotPasswordActivity, e.message.toString())
                        }
                    }
                }
                override fun onFailure(call: Call<ResetPasswordModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(this@ForgotPasswordActivity, "" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })

    }

    // popup will display to select currency.
    fun popupForgetPasswordAlert(message:String) {

        val layoutInflater: LayoutInflater =
            this@ForgotPasswordActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.popup_forget_password_alert_layout, null)
        val errorAlertWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        errorAlertWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            errorAlertWindow.elevation = 10f
        }
        errorAlertWindow.isTouchable = false
        errorAlertWindow.isOutsideTouchable = false

        val messageDispText = popUp.findViewById<MaterialTextView>(R.id.messageDispText)
        val doneTv = popUp.findViewById<MaterialTextView>(R.id.doneTv)
        messageDispText.text = message
        doneTv.setOnClickListener{
            errorAlertWindow.dismiss()
            finish()
        }

    }

}