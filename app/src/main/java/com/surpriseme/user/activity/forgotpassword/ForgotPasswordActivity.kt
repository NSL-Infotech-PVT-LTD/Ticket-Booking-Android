package com.surpriseme.user.activity.forgotpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.CheckValidEmail
import com.surpriseme.user.util.Constants
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private var email =""
    private lateinit var checkValidEmail: CheckValidEmail
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        inIt()
        checkValidEmail = CheckValidEmail()

    }

    private fun inIt() {

        continueButton.setOnClickListener(this)
        tbackpress.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.continueButton -> {
                email = emailedt.text.toString().trim()
                if (email.isEmpty()) {
                    emailedt.error = getString(R.string.please_fill_require_field)
                    emailedt.requestFocus()
                } else if (!checkValidEmail.isValidEmail(email)){
                    emailedt.error = getString(R.string.please_enter_valid_email)
                    emailedt.requestFocus()
                } else{
                    registerApi()
                }
            }
            R.id.tbackpress -> {
                finish()
            }
        }
    }

    private fun registerApi() {
        loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.resetPasswordApi(email)
            .enqueue(object :Callback<ResetPasswordModel> {
                override fun onResponse(
                    call: Call<ResetPasswordModel>,
                    response: Response<ResetPasswordModel>
                ) {
                    loaderLayout.visibility = View.GONE
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
                    loaderLayout.visibility = View.GONE
                    Toast.makeText(this@ForgotPasswordActivity, "" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })

    }

}