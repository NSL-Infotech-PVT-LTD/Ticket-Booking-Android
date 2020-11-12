package com.surpriseme.user.activity.signuptype

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.databinding.ActivitySignUpTypeBinding
import com.surpriseme.user.activity.signup.SignUpActivity
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.activity_sign_up_type.*
import org.json.JSONException
import java.util.*


class SignUpTypeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpTypeBinding
    private val EMAIL = "email"
    private var callbackManager: CallbackManager?=null
    private var accessTokenTracker:AccessTokenTracker?=null
    private var shared:PrefrenceShared?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up_type)

        binding = DataBindingUtil.setContentView(
            this@SignUpTypeActivity,
            R.layout.activity_sign_up_type
        )
        shared = PrefrenceShared(this@SignUpTypeActivity)
        inIt()
    }

    private fun inIt() {
        signUpEmailBtn.setOnClickListener(this)
        signUpFbBtn.setOnClickListener(this)
        backToLoginBtn.setOnClickListener(this)
        initSecond()


    }

//    // Facebook
    fun initSecond() {
        binding.signUpFbBtn.setReadPermissions(
            Arrays.asList("email", "public_profile")
        )

        accessTokenTracker = object : AccessTokenTracker() {
            // This method is invoked everytime access token changes
            override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken?, currentAccessToken: AccessToken
            ) {
// currentAccessToken is null if the user is logged out
                useLoginInformation(currentAccessToken)

            }
        }
    }

//    //Facebook
    private fun OnClickHandle() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))

// Registering CallbackManager with the LoginButton
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
// Retrieving access token using the LoginResult
                    val accessToken = loginResult.accessToken
                }

                override fun onCancel() {}
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
                    val name = `object`.getString("name")
                    val email = `object`.getString("email")
                    val id = `object`.getString("id")
                    val image = `object`.getString("public_picture")
                    shared?.setString("name", name)
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
            R.id.signUpFbBtn -> {

                // sign up with facebok....
                OnClickHandle()

            }
            R.id.backToLoginBtn -> {
                finish()
            }



        }
    }

}