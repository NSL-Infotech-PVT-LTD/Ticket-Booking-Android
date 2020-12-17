package com.surpriseme.user.fragments.viewprofile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
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
import com.surpriseme.user.activity.SettingsActivity
import com.surpriseme.user.data.model.UpdateProfileModel
import com.surpriseme.user.databinding.FragmentProfileBinding
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.fragments.changepasswordfragment.ChangePasswordFragment
import com.surpriseme.user.fragments.homefragment.HomeFragment
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
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

class ProfileFragment : Fragment(), View.OnClickListener,Permission.GalleryCameraCallBack,MainActivity.SendImageBitmap {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var shared: PrefrenceShared
    private lateinit var ctx: Context
    private var username = ""
    lateinit var thumbnail: Bitmap
    private lateinit var file: File
    private var userImage: MultipartBody.Part? = null
    private var toolProfileTxt:MaterialTextView?=null
    private var toolBackpress:MaterialTextView?=null
    private var currency = ""


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

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)

        val view = binding.root
        shared = PrefrenceShared(ctx)
        Constants.PROFILE_FRAGMENT = true

        ((ctx as MainActivity)).hideBottomNavigation()

        inIt(view)

        return view
    }

    private fun inIt(view:View) {

//        loaderLayout = view.findViewById(R.id.loaderLayoutprofile)
        toolBackpress = view.findViewById(R.id.backpress)
        toolProfileTxt = view.findViewById(R.id.toolProfileTxt)
        toolProfileTxt?.visibility =View.VISIBLE
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
        if (shared.getString(Constants.DataKey.USER_IMAGE) !="") {
            Picasso.get().load(shared.getString(Constants.DataKey.USER_IMAGE)).resize(4000,1500)
                .onlyScaleDown()
                .into(binding.profileImage)
        }
        if (shared.getString(Constants.DataKey.USER_NAME) !="") {
            binding.usernameEdt.setText(shared.getString(Constants.DataKey.USER_NAME))
        }
        if (shared.getString(Constants.DataKey.USER_EMAIL) !="") {
            binding.emailEdt.setText(shared.getString(Constants.DataKey.USER_EMAIL))
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.backpress -> {

                updateProfilePopup()
            }
            R.id.logoutTxt -> {

                logoutPop()
            }
            R.id.editProfileBtn -> {
                binding.editProfileBtn.visibility = View.GONE
                binding.cameraIcon.visibility = View.VISIBLE
                binding.usernameEdt.isEnabled = true
                binding.profileImage.isEnabled = true
                binding.usernameEdt.requestFocus()
                binding.updateprofilebtn.visibility = View.VISIBLE
            }
            R.id.updateprofilebtn -> {

                username = binding.usernameEdt.text.toString()

                if (username.isEmpty()) {
                    binding.usernameEdt.error = getString(R.string.please_fill_require_field)
                    binding.usernameEdt.requestFocus()
                } else {
                    // update profile api
                    updateProfileApi(currency)
                }
            }
            R.id.cameraIcon -> {

                Permission.checkPermissionForImageCamera(requireActivity(),this@ProfileFragment)

            }
            R.id.changePasswordText -> {
                loadFragment(ChangePasswordFragment())
            }
            R.id.settingsTxt -> {
                binding.settingsTxt.isEnabled =false
                val settingsIntent = Intent(ctx, SettingsActivity ::class.java)
                startActivity(settingsIntent)
                binding.settingsTxt.postDelayed(Runnable {
                    binding.settingsTxt.isEnabled = true
                },2000)
            }

        }
    }


    private fun updateProfileApi(currency:String) {

        binding.loaderLayout.visibility = View.VISIBLE

        val requestBodyMap: HashMap<String, RequestBody> = HashMap()
        requestBodyMap[Constants.ApiKey.NAME] =
            RequestBody.create(MediaType.parse("multipart/form-data"), username)
        requestBodyMap[Constants.ApiKey.CURRENCY] = RequestBody.create(MediaType.parse("multipart/form-data"),currency)

        RetrofitClient.api.updateProfileApi(shared.getString(Constants.DataKey.AUTH_VALUE),requestBodyMap,userImage)
            .enqueue(object :Callback<UpdateProfileModel> {
                override fun onResponse(
                    call: Call<UpdateProfileModel>,
                    response: Response<UpdateProfileModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {

                            shared.setString(Constants.DataKey.USER_IMAGE,
                                Constants.ImageUrl.BASE_URL + Constants.ImageUrl.USER_IMAGE_URL +
                                        response.body()?.data?.user?.image)
                            val intent = Intent(ctx, MainActivity ::class.java)
                            startActivity(intent)
                            activity?.finish()

                        }
                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(ctx, "" + errorMessage,Toast.LENGTH_SHORT).show()

                            }catch (e:JSONException) {
                                Toast.makeText(ctx,"" + Constants.SOMETHING_WENT_WRONG,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<UpdateProfileModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                }
            })
    }

    private fun loadFragment(fragment: Fragment) {

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.addToBackStack("profileFragment")
        transaction?.commit()

    }


    fun updateProfilePopup() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.update_profile_popup_layout, null)
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

        val yes: MaterialTextView = popUp.findViewById(R.id.yes)
        val no: MaterialTextView = popUp.findViewById(R.id.no)
        val displayMessage: TextView = popUp.findViewById(R.id.dialogtext11)
        displayMessage.text = ctx.getString(R.string.do_you_want_to_save_your_profile)

        yes.setOnClickListener {
            popUpWindowReport.dismiss()
        }

        no.setOnClickListener {
            popUpWindowReport.dismiss()
            fragmentManager?.popBackStack()
            Constants.PROFILE_FRAGMENT = false
        }
    }
    private fun logoutPop() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.item_custom_logout, null)
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

        val yes: MaterialTextView = popUp.findViewById(R.id.yes)
        val cancelTv: MaterialTextView = popUp.findViewById(R.id.cancel)

        yes.setOnClickListener {
            shared.clearShared()
            Constants.SHOW_TYPE = ""
            val loginActivity = Intent(ctx, LoginActivity ::class.java)
            startActivity(loginActivity)
            activity?.finishAffinity()
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
        val imageFilePath: String = mPaths!!.get(0).toString()
        val bmOption: BitmapFactory.Options = BitmapFactory.Options()
        thumbnail = BitmapFactory.decodeFile(imageFilePath, bmOption)
        file = File(imageFilePath)
        binding.profileImage.setImageBitmap(thumbnail)

        userImage = MultipartBody.Part.createFormData(
            Constants.ApiKey.IMAGE, file.name, RequestBody.create(
                MediaType.parse("image/*"), file
            )
        )

    }

}