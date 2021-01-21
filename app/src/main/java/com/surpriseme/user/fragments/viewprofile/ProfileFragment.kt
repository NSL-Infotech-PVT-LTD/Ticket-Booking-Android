package com.surpriseme.user.fragments.viewprofile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.settings.SettingsActivity
import com.surpriseme.user.data.model.UpdateProfileModel
import com.surpriseme.user.databinding.FragmentProfileBinding
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.login.Loginmodel
import com.surpriseme.user.fragments.changepasswordfragment.ChangePasswordFragment
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import com.tournie.Util.Permission
import net.alhazmy13.mediapicker.Image.ImagePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment(), View.OnClickListener, Permission.GalleryCameraCallBack,
    MainActivity.SendImageBitmap {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var shared: PrefrenceShared
    private lateinit var ctx: Context
    private var username = ""
    private lateinit var thumbnail: Bitmap
    private lateinit var file: File
    private var userImage: MultipartBody.Part? = null
    private var toolProfileTxt: MaterialTextView? = null
    private var toolBackpress: MaterialTextView? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        ((ctx as MainActivity)).profileFragmentContext(this@ProfileFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)
        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text = Utility.randomString(ctx)
        // Hide Bottom Navigation
        ((ctx as MainActivity)).hideBottomNavigation()
        inIt(view)

        return view
    }

    private fun inIt(view: View) {

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text = Utility.randomString(ctx)
//        loaderLayout = view.findViewById(R.id.loaderLayoutprofile)
        toolBackpress = view.findViewById(R.id.backpress)
        toolProfileTxt = view.findViewById(R.id.toolProfileTxt)
        toolProfileTxt?.visibility = View.VISIBLE
        binding.logoutTxt.setOnClickListener(this)
        binding.editProfileBtn.setOnClickListener(this)
        binding.updateprofilebtn.setOnClickListener(this)
        binding.cameraIcon.setOnClickListener(this)
        binding.changePasswordText.setOnClickListener(this)
        binding.settingsTxt.setOnClickListener(this)
        toolBackpress?.setOnClickListener(this)

        binding.usernameEdt.isEnabled = false
        binding.emailEdt.isEnabled = false
        binding.profileImage.isEnabled = false

        // display value to the views ....
        if (shared.getString(Constants.DataKey.USER_IMAGE) != "") {
            Picasso.get().load(shared.getString(Constants.DataKey.USER_IMAGE))
                .placeholder(R.drawable.profile_pholder).into(binding.profileImage)
        }
        if (shared.getString(Constants.DataKey.USER_NAME) != "") {
            binding.usernameEdt.setText(shared.getString(Constants.DataKey.USER_NAME))
        }
        if (shared.getString(Constants.DataKey.USER_EMAIL) != "") {
            binding.emailEdt.setText(shared.getString(Constants.DataKey.USER_EMAIL))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.backpress -> {
                if (Constants.PROFILE_FRAGMENT) {
                    updateProfilePopup()
                } else {
                    requireActivity().supportFragmentManager.popBackStack()
                    Constants.PROFILE_FRAGMENT = false
                }
            }
            R.id.logoutTxt -> {
                logoutPop()
            }
            R.id.editProfileBtn -> {
             //   isTryToUpdateProfile = true
                Constants.PROFILE_FRAGMENT = true
                binding.editProfileBtn.visibility = View.GONE
                binding.cameraIcon.visibility = View.VISIBLE
                binding.usernameEdt.isEnabled = true
                binding.profileImage.isEnabled = true
                binding.usernameEdt.requestFocus()
                binding.updateprofilebtn.visibility = View.VISIBLE
                binding.passwordprofileLayout.visibility = View.GONE
                binding.aboutprofileLayout.visibility = View.GONE
                binding.logoutLayout.visibility = View.GONE
            }
            R.id.updateprofilebtn -> {
             //   isTryToUpdateProfile = false
                Constants.PROFILE_FRAGMENT = false
                username = binding.usernameEdt.text.toString()
                if (username.isEmpty()) {
                    Utility.alertErrorMessage(ctx,ctx.resources.getString(R.string.enter_your_user_name))
                } else {
                    // update profile api
                    updateProfileApi()
                }
                Handler().postDelayed({
                    binding.passwordprofileLayout.visibility = View.VISIBLE
                    binding.aboutprofileLayout.visibility = View.VISIBLE
                    binding.logoutLayout.visibility = View.VISIBLE
                }, 3000)
            }
            R.id.cameraIcon -> {
                Permission.checkPermissionForImageCamera(requireActivity(), this@ProfileFragment)
            }
            R.id.changePasswordText -> {
                loadFragment(ChangePasswordFragment())
            }
            R.id.settingsTxt -> {
                binding.settingsTxt.isEnabled = false
                val settingsIntent = Intent(ctx, SettingsActivity::class.java)
                startActivity(settingsIntent)
                binding.settingsTxt.postDelayed({
                    binding.settingsTxt.isEnabled = true
                }, 2000)
            }
        }
    }
    private fun updateProfileApi() {
        binding.loaderLayout.visibility = View.VISIBLE
        val requestBodyMap: HashMap<String, RequestBody> = HashMap()
        requestBodyMap[Constants.ApiKey.NAME] =
            RequestBody.create(MediaType.parse("multipart/form-data"), username)
        RetrofitClient.api.updateProfileApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            requestBodyMap,
            userImage
        )
            .enqueue(object : Callback<UpdateProfileModel> {
                override fun onResponse(
                    call: Call<UpdateProfileModel>,
                    response: Response<UpdateProfileModel>
                ) {
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            binding.loaderLayout.visibility = View.GONE
                            shared.setString(
                                Constants.DataKey.USER_IMAGE,
                                Constants.ImageUrl.BASE_URL + Constants.ImageUrl.USER_IMAGE_URL + response.body()?.data?.user?.image)
                            //isTryToUpdateProfile = false
                            binding.editProfileBtn.visibility = View.VISIBLE
                            binding.cameraIcon.visibility = View.GONE
                            binding.usernameEdt.isEnabled = false
                            binding.profileImage.isEnabled = false
                            binding.updateprofilebtn.visibility = View.GONE
                            binding.passwordprofileLayout.visibility = View.VISIBLE
                            binding.aboutprofileLayout.visibility = View.VISIBLE
                            binding.logoutLayout.visibility = View.VISIBLE
                            val message = response.body()?.data?.message
                            popupUpdatedProfile(message!!)
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    ctx,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<UpdateProfileModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameContainer, fragment)
        transaction.addToBackStack("profileFragment")
        transaction.commit()
    }

    // popup to display want to update profile or not....
    fun updateProfilePopup() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View = layoutInflater.inflate(R.layout.update_profile_popup_layout,binding.profileContainer,false)
        val popUpWindowReport = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popUpWindowReport.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popUpWindowReport.elevation = 10f
        }
        popUpWindowReport.isTouchable = false
        popUpWindowReport.isOutsideTouchable = false

        val yes: MaterialTextView = popUp.findViewById(R.id.yes)
        val no: MaterialTextView = popUp.findViewById(R.id.no)
        val displayMessage: TextView = popUp.findViewById(R.id.dialogtext11)
        displayMessage.text = ctx.getString(R.string.do_you_want_to_save_your_profile)

        yes.setOnClickListener {
            popUpWindowReport.dismiss()
            username = binding.usernameEdt.text.toString()

            if (username.isEmpty()) {
                Utility.alertErrorMessage(ctx, getString(R.string.enter_your_user_name))
            } else {
                // update profile api
                updateProfileApi()
            }
            Handler().postDelayed({
                binding.passwordprofileLayout.visibility = View.VISIBLE
                binding.aboutprofileLayout.visibility = View.VISIBLE
                binding.logoutLayout.visibility = View.VISIBLE
            }, 3000)
            Constants.PROFILE_FRAGMENT = false

        }
        no.setOnClickListener {
            popUpWindowReport.dismiss()
            requireActivity().supportFragmentManager.popBackStack()
            Constants.PROFILE_FRAGMENT = false
        }
    }
    // when profile will be updated, then success message to display, "Profile has been updated successfully"
    fun popupUpdatedProfile(message:String?) {
        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View = layoutInflater.inflate(R.layout.updated_profile_alert_layout, binding.profileContainer,false)
        val popUpWindowReport = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindowReport.showAtLocation(popUp, Gravity.CENTER, 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popUpWindowReport.elevation = 10f
        }
        popUpWindowReport.isTouchable = false
        popUpWindowReport.isOutsideTouchable = false
        val done: MaterialTextView = popUp.findViewById(R.id.done)
        val displayMessage: TextView = popUp.findViewById(R.id.messageTxt)
        displayMessage.text = message

        done.setOnClickListener {
            popUpWindowReport.dismiss()

        }
    }

    private fun logoutPop() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View = layoutInflater.inflate(R.layout.item_custom_logout, binding.profileContainer,false)
        val popUpWindowReport = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindowReport.showAtLocation(popUp, Gravity.CENTER, 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popUpWindowReport.elevation = 10f
        }
        popUpWindowReport.isTouchable = false
        popUpWindowReport.isOutsideTouchable = false
        val yes: TextView = popUp.findViewById(R.id.yes)
        val cancelTv: TextView = popUp.findViewById(R.id.cancel)
        yes.setOnClickListener {
            logout()
            popUpWindowReport.dismiss()
        }
        cancelTv.setOnClickListener {
            popUpWindowReport.dismiss()
        }
    }

    override fun onImagePermissionSuccess() {
        ImagePicker.Builder(requireActivity())
            .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
            .compressLevel(ImagePicker.ComperesLevel.HARD)
            .directory(ImagePicker.Directory.DEFAULT)
            .extension(ImagePicker.Extension.PNG)
            .scale(400, 300)
            .allowMultipleImages(false)
            .enableDebuggingMode(true)
            .build()
    }

    override fun getImageBitmap(mPaths: List<String?>?) {
        val imageFilePath: String = mPaths!![0].toString()
        val bmOption: BitmapFactory.Options = BitmapFactory.Options()
        thumbnail = BitmapFactory.decodeFile(imageFilePath, bmOption)
        file = File(imageFilePath)
        binding.profileImage.setImageBitmap(thumbnail)
        userImage = MultipartBody.Part.createFormData(
            Constants.ApiKey.IMAGE,
            file.name,
            RequestBody.create(MediaType.parse("image/*"), file))
    }

    private fun logout() {

        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.logout(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            shared.getString(Constants.FB_TOKEN),
            Constants.DataKey.DEVICE_TYPE_VALUE
        )
            .enqueue(object : Callback<Loginmodel> {
                override fun onResponse(call: Call<Loginmodel>, response: Response<Loginmodel>) {
                    binding.loaderLayout.visibility = View.GONE

                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                Toast.makeText(
                                    ctx,
                                    "" + response.body()!!.data.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                shared.clearShared()
                                Constants.SHOW_TYPE = ""
                                val intent = Intent(ctx, LoginActivity::class.java)
                                ctx.startActivity(intent)
                                activity?.finishAffinity()
                            }
                        } else {
                            val jsonobject: JSONObject
                            if (response.errorBody() != null) {
                                try {
                                    jsonobject = JSONObject(response.errorBody()?.string()!!)
                                    val errorMessage = jsonobject.getString(Constants.ERROR)
                                    Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT)
                                        .show()
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        ctx,
                                        "" + e.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Loginmodel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

}