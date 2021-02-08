package com.surpriseme.user.activity.termprivacyhelp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.ActivityTermPrivacyHelpBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TermPrivacyHelpActivity : AppCompatActivity(), View.OnClickListener {

    private var binding:ActivityTermPrivacyHelpBinding?=null
    private var shared:PrefrenceShared?=null
    private var backpress:MaterialTextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_term_privacy_help)

        binding = DataBindingUtil.setContentView(this@TermPrivacyHelpActivity,R.layout.activity_term_privacy_help)
        shared = PrefrenceShared(this@TermPrivacyHelpActivity)
        init()
    }
    private fun init() {

        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)

        binding?.dataDisplayTxt?.movementMethod = LinkMovementMethod.getInstance()


        when {
            Constants.IS_TERM_AND_CONDITION -> {
                binding?.icon?.setImageResource(R.drawable.term_condition_icon)
                binding?.headerTxt?.text = getString(R.string.terms_condition)
                termAndConditionApi()
            }
            Constants.IS_PRIVACY_POLICY -> {
                binding?.icon?.setImageResource(R.drawable.privacy_policy_icon)
                binding?.headerTxt?.text = getString(R.string.privacy_policy)
                privacyPolicyApi()
            }
            Constants.IS_ABOUT_US -> {
                binding?.icon?.setImageResource(R.drawable.about_us_icon)
                binding?.headerTxt?.text = getString(R.string.about_us)
                aboutUsApi()
            }
        }

    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.backpress -> {finish()}
        }
    }

    // Api for Terms and Conditions...
    private fun termAndConditionApi() {
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.termAndConditionsApi().enqueue(
            object :Callback<TermPrivacyHelpModel> {
                override fun onResponse(
                    call: Call<TermPrivacyHelpModel>,
                    response: Response<TermPrivacyHelpModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {
                            binding?.dataDisplayTxt?.text = Html.fromHtml(response.body()?.data?.config)
                        }
                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@TermPrivacyHelpActivity, errorMessage,Toast.LENGTH_SHORT).show()
                            }catch (e:JSONException) {
                                Toast.makeText(this@TermPrivacyHelpActivity, "" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TermPrivacyHelpModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE

                }


            }
        )
    }

    // Api for Privacy and Policy...
    private fun privacyPolicyApi() {
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.privacyPolicyApi()
            .enqueue(object :Callback<TermPrivacyHelpModel> {
                override fun onResponse(
                    call: Call<TermPrivacyHelpModel>,
                    response: Response<TermPrivacyHelpModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {
                            binding?.dataDisplayTxt?.text = Html.fromHtml(response.body()?.data?.config)
                        }
                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@TermPrivacyHelpActivity, errorMessage,Toast.LENGTH_SHORT).show()
                            }catch (e:JSONException) {
                                Toast.makeText(this@TermPrivacyHelpActivity, "" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TermPrivacyHelpModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                }

            })

    }

    // Api for Privacy and Policy...
    private fun aboutUsApi() {
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.aboutUsApi()
            .enqueue(object :Callback<TermPrivacyHelpModel> {
                override fun onResponse(
                    call: Call<TermPrivacyHelpModel>,
                    response: Response<TermPrivacyHelpModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {
                            binding?.dataDisplayTxt?.text = Html.fromHtml(response.body()?.data?.config)
                        }
                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@TermPrivacyHelpActivity, errorMessage,Toast.LENGTH_SHORT).show()
                            }catch (e:JSONException) {
                                Toast.makeText(this@TermPrivacyHelpActivity, "" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TermPrivacyHelpModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                }

            })

    }



}