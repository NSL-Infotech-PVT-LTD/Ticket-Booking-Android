package com.surpriseme.user.activity.signuptype

import android.content.Intent
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.signup.SignUpActivity
import com.surpriseme.user.databinding.ActivitySignUpTypeBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.activity_sign_up_type.*
import kotlinx.android.synthetic.main.chat_list_layout.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*


class SignUpTypeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpTypeBinding
    private var callbackManager: CallbackManager?=null
    private var accessTokenTracker:AccessTokenTracker?=null
    private var shared:PrefrenceShared?=null
    private var fbName = ""
    private var fbEmail = ""
    private var fbImage = ""
    private var fbID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up_type)

        binding = DataBindingUtil.setContentView(this@SignUpTypeActivity, R.layout.activity_sign_up_type)
        shared = PrefrenceShared(this@SignUpTypeActivity)
        inIt()
    }

    private fun inIt() {


        signUpEmailBtn.setOnClickListener(this)
        binding.fbLogin.setOnClickListener(this)
        backToLoginBtn.setOnClickListener(this)
        initSecond()
    }

//    // Facebook
    fun initSecond() {

//        binding.fbLogin.setReadPermissions(Arrays.asList("email", "public_profile"))

        accessTokenTracker = object : AccessTokenTracker() {
            // This method is invoked everytime access token changes
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?, currentAccessToken: AccessToken?
            ) {
// currentAccessToken is null if the user is logged out
                if (currentAccessToken !=null) {
                    useLoginInformation(currentAccessToken)
                }

            }
        }
    }

    //Facebook
    private fun OnClickHandle() {

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))

// Registering CallbackManager with the LoginButton
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
// Retrieving access token using the LoginResult
                    val accessToken = loginResult.accessToken
//                    val isLoggedIn = accessToken != null && !accessToken.isExpired

                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {}
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
                    Picasso.get().load(fbImage).resize(4000,1500)
                        .onlyScaleDown()
                        .into(binding.fbImage)

                    shared?.setString(Constants.FB_ID,fbID)
                    shared?.setString(Constants.FB_NAME,fbName)
                    shared?.setString(Constants.FB_EMAIL,fbEmail)
                    shared?.setString(Constants.FB_IMAGE,fbImage)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
// We set parameters to the GraphRequest using a Bundle.

        if (fbID !="") {

            // register with FB api....
            registerWithFbApi()

        } else {
            val parameters = Bundle()
            parameters.putString("fields", "id,name,email,picture.width(200)")
            request.parameters = parameters
// Initiate the GraphRequest
            request.executeAsync()
        }
    }

    // Facebook
    override fun onDestroy() {
        super.onDestroy()
// We stop the tracking before destroying the activity
        accessTokenTracker?.stopTracking()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (callbackManager !=null)
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
            R.id.loginButton -> {
                // sign up with facebok....
                OnClickHandle()
            }
            R.id.backToLoginBtn -> {
                finish()
            }
            R.id.fbLogin -> {
                OnClickHandle()
            }

        }
    }

    // Register api with facebook
    private fun registerWithFbApi() {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.registerWithFB(fbName,fbEmail,fbID,Constants.DataKey.DEVICE_TYPE_VALUE,Constants.DataKey.DEVICE_TOKEN_VALUE,"en"
        ,fbImage)
            .enqueue(object :Callback<RegisterWithFbModel> {
                override fun onResponse(
                    call: Call<RegisterWithFbModel>,
                    response: Response<RegisterWithFbModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() !=null) {

                            shared?.setString(
                                Constants.DataKey.AUTH_VALUE,
                                Constants.BEARER + response.body()?.data?.token!!
                            )   // To save Auth token
                            shared?.setString(Constants.DataKey.USER_ID,response.body()?.data?.user?.id.toString())    // To Save User ID
//                            shared.setString(Constants.DataKey.OLD_PASS_VALUE, password)                                       // To save User Password
                            shared?.setString(
                                Constants.DataKey.USER_IMAGE,  // To Save User Image
                                Constants.ImageUrl.BASE_URL + Constants.ImageUrl.USER_IMAGE_URL + response.body()?.data?.user?.image)
                            val mainActIntent = Intent(this@SignUpTypeActivity, MainActivity::class.java)
                            startActivity(mainActIntent)
                            finishAffinity()
                        }
                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@SignUpTypeActivity,"" + errorMessage,Toast.LENGTH_SHORT).show()
                            } catch (e:JSONException) {
                                Toast.makeText(this@SignUpTypeActivity,"" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<RegisterWithFbModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(this@SignUpTypeActivity,"" + t.message.toString(),Toast.LENGTH_SHORT).show()

                }

            })
    }
}