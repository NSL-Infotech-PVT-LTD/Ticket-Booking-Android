package com.surpriseme.user.fragments.chatListfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.FragmentListChatBinding
import com.surpriseme.user.fragments.chatFragment.ChatFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatListFragment : Fragment(), ChatListAdapter.GoToChat {

    private var binding:FragmentListChatBinding?=null
    private var shared:PrefrenceShared?=null
    private var mChatId = ""
    private var chatList:ArrayList<ChatDetailModel> = ArrayList()

    private var ctx:Context?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_list_chat,container,false)
        val view = binding!!.root
        shared = PrefrenceShared(ctx!!)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireActivity(),R.color.colorPrimary)
        init()

        return view

    }
    private fun init() {

        ((ctx as MainActivity)).showBottomNavigation()
        // calling Chat list api....
        chatListApi()
    }

    private fun chatListApi() {

        binding?.loaderLayout?.visibility = View.VISIBLE

        RetrofitClient.api.chatListApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,"")
            .enqueue(object :Callback<ChatListModel> {
                override fun onResponse(
                    call: Call<ChatListModel>,
                    response: Response<ChatListModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {
                            chatList.clear()
                            /**
                             * @author pardeep.sharma@netscapelabs.com
                             * @param handle the null check
                             */
                            if (response.body()?.data?.list != null) {
                                chatList = response.body()?.data?.list!!


                                if (chatList.isNotEmpty()) {

                                    binding?.noArtistIcon?.visibility = View.GONE
                                    binding?.noArtistFound?.visibility = View.GONE

                                    val chatListAdapter =
                                        ChatListAdapter(ctx!!, chatList, this@ChatListFragment)
                                    binding?.chatListRecycler?.adapter = chatListAdapter
                                    binding?.chatContainer?.visibility = View.VISIBLE
                                } else {
                                    binding?.chatContainer?.visibility = View.GONE
                                }
                            }else{
                                binding?.noArtistText?.visibility = View.VISIBLE
                            }
                        }else {
                                Toast.makeText(ctx,"Something went Wrong",Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx!!,"" + errorMessage,Toast.LENGTH_SHORT).show()
                            }catch (e:JSONException) {
                                Toast.makeText(ctx!!,"" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ChatListModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(ctx!!,"" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })
    }

    // override method from ChatListAdapter to get receiver_id, then receiver_id will send to chatFragment for one to one chat....
    override fun chatId(chatid: String, rImage:String,rName:String) {
        this.mChatId = chatid
        Constants.IS_CHAT_SESSION = true
        val intent = Intent(ctx , ChatFragment::class.java)
        intent.putExtra("chatId",chatid)
        intent.putExtra("receiverImage",rImage)
        intent.putExtra("receiverName",rName)
        startActivity(intent)
//        val bundle = Bundle()
//        val fragment = ChatFragment()
//        bundle.putString("chatId", mChatId)
//        bundle.putString("receiverImage",rImage)
//        bundle.putString("receiverName",rName)
//        fragment.arguments = bundle
//        val transaction = fragmentManager?.beginTransaction()
//        transaction?.replace(R.id.frameContainer,fragment)
//        transaction?.addToBackStack("chatListFragment")
//        transaction?.commit()
    }

    //Converting Utc Time to Local time
    fun uTCToLocal(
        dateFormatInPut: String?,
        dateFomratOutPut: String?,
        datesToConvert: String?
    ): String? {
        var dateToReturn = datesToConvert
        val sdf = SimpleDateFormat(dateFormatInPut)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var gmt: Date? = null
        val sdfOutPutToSend =
            SimpleDateFormat(dateFomratOutPut)
        sdfOutPutToSend.timeZone = TimeZone.getDefault()
        try {
            gmt = sdf.parse(datesToConvert)
            dateToReturn = sdfOutPutToSend.format(gmt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateToReturn
    }


}