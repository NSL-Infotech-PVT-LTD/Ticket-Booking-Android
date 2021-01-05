package com.surpriseme.user.fragments.notificationfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentNotificationBinding
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailFragment
import com.surpriseme.user.fragments.chatListfragment.ChatListFragment
import com.surpriseme.user.fragments.homefragment.HomeFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class NotificationFragment : Fragment(),NotificationListAdapter.NotificationDetail
{

    private var binding:FragmentNotificationBinding?=null
    private var shared: PrefrenceShared? = null
    private var isNotify = "1"
    private var notificationList:ArrayList<NotificationListDataModel> = ArrayList()
    private var ctx:Context?=null
    private var mNotificationID=""
    private var backpress:MaterialTextView?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notification,container,false)
        val view = binding?.root
        shared = PrefrenceShared(ctx!!)
        //Hide Bottom Navigation for Notification Screen.....
        ((ctx as MainActivity)).hideBottomNavigation()

        init(view!!)


        return view
    }
    private fun init(view:View) {

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(ctx!!)
        // calling notification list api....
        notificationListApi()
        backpress = view.findViewById(R.id.backpress)
        backpress?.setOnClickListener {
            // backpress code....
            fragmentManager?.popBackStack()
        }

        // checking Switch is on or off....
        if (binding?.switchBtn?.isChecked!!) {
            binding?.switchBtn?.trackDrawable = ContextCompat.getDrawable(ctx!!,R.drawable.notification_track_active)
            isNotify = "1"
        }

        // on activity start, check for notification on or off....
        if (shared?.getString(Constants.SWITCH_STATUS) =="1") {
            binding?.switchBtn?.isChecked = true
            binding?.switchBtn?.trackDrawable = ContextCompat.getDrawable(ctx!!,R.drawable.notification_track_active)
        }
        else {
            binding?.switchBtn?.isChecked = false
            binding?.switchBtn?.trackDrawable = ContextCompat.getDrawable(ctx!!,R.drawable.notification_track_inactive)
        }

        // Switch button change listener....
        binding?.switchBtn?.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                binding?.switchBtn?.trackDrawable = ContextCompat.getDrawable(ctx!!,R.drawable.notification_track_active)
                isNotify = "1"
                notificationStatusApi()
                shared?.setString(Constants.SWITCH_STATUS, isNotify)

            } else {
                binding?.switchBtn?.trackDrawable = ContextCompat.getDrawable(ctx!!,R.drawable.notification_track_inactive)
                isNotify = "0"
                notificationStatusApi()
                shared?.setString(Constants.SWITCH_STATUS, isNotify)
            }
        }

    }

    // Notification Status Api....
    private fun notificationStatusApi() {

        binding?.loaderLayout?.visibility = View.VISIBLE

        RetrofitClient.api.notificationStatusApi(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            isNotify
        )
            .enqueue(object : Callback<NotificationStatusModel> {
                override fun onResponse(
                    call: Call<NotificationStatusModel>,
                    response: Response<NotificationStatusModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
//                            Toast.makeText(
//                                applicationContext,
//                                "" + response.body()!!.data.message,
//                                Toast.LENGTH_LONG
//                            ).show()
                            Snackbar.make(binding?.notificationContainer!!,"" + response.body()?.data?.message,
                                BaseTransientBottomBar.LENGTH_SHORT).show()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage: String = jsonObject.getString(Constants.ERROR)
                                Snackbar.make(binding?.notificationContainer!!,"" + errorMessage,
                                    BaseTransientBottomBar.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Snackbar.make(binding?.notificationContainer!!,"" + e.message.toString(),
                                    BaseTransientBottomBar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationStatusModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (t is SocketTimeoutException) {
                        Toast.makeText(
                            ctx,
                            "" + Constants.TIME_OUT,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            ctx,
                            "" + t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    // Notification List Api....
    private fun notificationListApi() {

        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.notificationListApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,"")
            .enqueue(object :Callback<NotificationListModel> {
                override fun onResponse(
                    call: Call<NotificationListModel>,
                    response: Response<NotificationListModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {
                            notificationList.clear()
                            notificationList = response.body()?.data?.data!!

                            if (notificationList.isNotEmpty()) {
                                val notificationListAdp = NotificationListAdapter(ctx!!,notificationList,this@NotificationFragment)
                                binding?.notiListRecycler?.adapter = notificationListAdp

                                binding?.noDataFound?.visibility = View.GONE
                                binding?.refresh?.visibility = View.GONE

                            } else {
                                binding?.noDataFound?.visibility = View.VISIBLE
                                binding?.noDataFound?.text = "No Notifications"
                                binding?.refresh?.visibility = View.VISIBLE

                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage: String = jsonObject.getString(Constants.ERROR)
                                Snackbar.make(binding?.notificationContainer!!,"" + errorMessage,BaseTransientBottomBar.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Snackbar.make(binding?.notificationContainer!!,"" + e.message.toString(),BaseTransientBottomBar.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<NotificationListModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Snackbar.make(binding?.notificationContainer!!,"" + t.message.toString(),BaseTransientBottomBar.LENGTH_SHORT).show()
                }

            })

    }
    // override method from Notification List Adapter to send detail to Detail Fragment
    override fun detail(id:String, notificationId:String,targetModel:String) {

        this.mNotificationID = notificationId
        notificationReadApi(mNotificationID)
        Constants.NOTIFICATION = true
        Constants.BOOKING = false
        Constants.IS_BOOKING_DONE = false
        if (targetModel == Constants.TARGET_MODEL_MESSAGE) {
            val bundle = Bundle()
            val fragment = ChatListFragment()
            bundle.putString("chatId",id)
            fragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameContainer,fragment)
            transaction?.addToBackStack("notiFragment")
            transaction?.commit()
        }else {
          val intent = Intent(ctx,BookingDetailFragment::class.java)
            intent.putExtra("bookingId",id)
            startActivity(intent)
        }

    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameContainer, fragment)
        transaction?.commit()

    }

    // NotificationReadApi
    private fun notificationReadApi(notificationId: String) {

        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.notificationReadApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,notificationId)
            .enqueue(object :Callback<NotificationReadModel> {
                override fun onResponse(
                    call: Call<NotificationReadModel>,
                    response: Response<NotificationReadModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful){
//                            Snackbar.make(binding?.notificationContainer!!,response.body()?.data?.message!!,BaseTransientBottomBar.LENGTH_SHORT).show()

                        }
                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
//                                Snackbar.make(binding?.notificationContainer!!,"" + errorMessage,BaseTransientBottomBar.LENGTH_SHORT).show()
                                Toast.makeText(ctx,"" + errorMessage,Toast.LENGTH_SHORT).show()
                            }catch (e:JSONException) {
//                                Snackbar.make(binding?.notificationContainer!!,"" + e.message.toString(),BaseTransientBottomBar.LENGTH_SHORT).show()
                                Toast.makeText(ctx,"" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationReadModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
//                    Snackbar.make(binding?.notificationContainer!!,"" + t.message.toString(),BaseTransientBottomBar.LENGTH_SHORT).show()
                    Toast.makeText(ctx,"" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })
    }

}