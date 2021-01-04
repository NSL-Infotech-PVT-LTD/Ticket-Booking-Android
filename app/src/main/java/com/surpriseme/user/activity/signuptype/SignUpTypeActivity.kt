package com.surpriseme.user.activity.signuptype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.signup.SignUpActivity
import com.surpriseme.user.databinding.ActivitySignUpTypeBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefManger
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.activity_sign_up_type.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpTypeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpTypeBinding
    private var callbackManager: CallbackManager? = null
    private var accessTokenTracker: AccessTokenTracker? = null
    private var shared: PrefrenceShared? = null
    private var fbName = ""
    private var fbEmail = ""
    private var fbImage = ""
    private var fbID:String? = null
    private var fbDataModel: FbDataModel? = null
    private var prefManager: PrefManger?=null
    private var currency = ""



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
//        setContentView(R.layout.activity_sign_up_type)

        binding =
            DataBindingUtil.setContentView(this@SignUpTypeActivity, R.layout.activity_sign_up_type)
        shared = PrefrenceShared(this@SignUpTypeActivity)
        prefManager = PrefManger(this@SignUpTypeActivity)
        inIt()
    }

    private fun inIt() {
        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString()

        signUpEmailBtn.setOnClickListener(this)
        binding.fbLogin.setOnClickListener(this)
        backToLoginBtn.setOnClickListener(this)
        initSecond()
    }

    //    // Facebook
    private fun initSecond() {

//        binding.fbLogin.setReadPermissions(Arrays.asList("email", "public_profile"))

        accessTokenTracker = object : AccessTokenTracker() {
            // This method is invoked everytime access token changes
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?, currentAccessToken: AccessToken?
            ) {
// currentAccessToken is null if the user is logged out
                if (currentAccessToken != null) {
                    useLoginInformation(currentAccessToken)
                }
            }
        }
    }

    //Facebook
    private fun onClickHandle() {

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))

// Registering CallbackManager with the LoginButton
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("login", "onSuccess: " + loginResult.toString())
// Retrieving access token using the LoginResult
//                    val accessToken = loginResult.accessToken
//                    val isLoggedIn = accessToken != null && !accessToken.isExpired
                }

                override fun onCancel() {
                    Toast.makeText(this@SignUpTypeActivity,"Cancel",Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(
                        this@SignUpTypeActivity,
                        "" + error.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Facebook
    fun useLoginInformation(accessToken: AccessToken) {
        /**
         * Creating the GraphRequest to fetch user details
         * 1st Param - AccessToken
         * 2nd Param - Callback (which will be invoked once the request is successful)
         */
        val request =
            GraphRequest.newMeRequest(
                accessToken
            ) { `object`, response ->

//OnCompleted is invoked once the GraphRequest is successful
                try {
                    fbName = `object`.getString("name")
                    fbEmail = `object`.getString("email")
                    fbID = `object`.getString("id")
                    fbImage = `object`.getJSONObject("picture").getJSONObject("data").getString("url")

                    shared?.setString(Constants.FB_ID, fbID)
                    shared?.setString(Constants.FB_NAME, fbName)
                    shared?.setString(Constants.FB_EMAIL, fbEmail)
                    shared?.setString(Constants.FB_IMAGE, fbImage)

                    if (fbID !=null) {
                        registerWithFbApi()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
// We set parameters to the GraphRequest using a Bundle.


            val parameters = Bundle()
            parameters.putString("fields", "id,name,email,picture.width(200)")
            request.parameters = parameters
// Initiate the GraphRequest
            request.executeAsync()

    }

    // Facebook
    override fun onDestroy() {
        super.onDestroy()
// We stop the tracking before destroying the activity
        accessTokenTracker?.stopTracking()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (callbackManager != null)
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signUpEmailBtn -> {
                // sign up with email...
                val intent = Intent(this@SignUpTypeActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
            R.id.backToLoginBtn -> {
                finish()
            }
            R.id.fbLogin -> {
                onClickHandle()
            }
        }
    }

    // Register api with facebook
    private fun registerWithFbApi() {
        shared?.setString(Constants.FB_TOKEN, fbtoken)
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.registerWithFB(
            fbName,
            fbEmail,
            fbID!!,
            Constants.DataKey.DEVICE_TYPE_VALUE,
            fbtoken,
            "en",
            fbImage
        )
            .enqueue(object : Callback<RegisterWithFbModel> {
                override fun onResponse(
                    call: Call<RegisterWithFbModel>,
                    response: Response<RegisterWithFbModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            fbDataModel = response.body()?.data
                            shared?.setString(
                                Constants.DataKey.AUTH_VALUE,
                                Constants.BEARER + response.body()?.data?.token!!
                            )   // To save Auth token
                            shared?.setString(
                                Constants.DataKey.USER_ID,
                                response.body()?.data?.user?.id.toString()
                            )    // To Save User ID
//                            shared.setString(Constants.DataKey.OLD_PASS_VALUE, password)                                       // To save User Password
                            shared?.setString(
                                Constants.DataKey.USER_IMAGE,  // To Save User Image
                                Constants.ImageUrl.BASE_URL + Constants.ImageUrl.USER_IMAGE_URL + response.body()?.data?.user?.image
                            )

                            currency = response.body()?.data?.user?.currency!!
                            if (currency.isEmpty())
                            prefManager?.setString1(Constants.DataKey.CURRENCY,"")
                            else prefManager?.setString1(Constants.DataKey.CURRENCY,currency)

//                            isFbRegistered = true
                            val mainActIntent =
                                Intent(this@SignUpTypeActivity, MainActivity::class.java)
                            startActivity(mainActIntent)
                            finishAffinity()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@SignUpTypeActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SignUpTypeActivity,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<RegisterWithFbModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@SignUpTypeActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
    }
}