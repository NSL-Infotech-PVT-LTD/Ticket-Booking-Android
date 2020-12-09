package com.surpriseme.user.fragments.chatFragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.FragmentChatBinding
import com.surpriseme.user.fragments.chatListfragment.ChatListFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.ConnectionManger
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

class ChatFragment : AppCompatActivity(), View.OnClickListener, IOnMessageReceived {

    private var binding: FragmentChatBinding? = null
    private var shared: PrefrenceShared? = null
  //  private var ctx: Context? = null
    private var mReceiverId = ""
    private var mReceiverImage = ""
    private var mReceiverName = ""
    private var adapter: MessageAdapter? = null
    private var messageList: ListView? = null
    private var chatBackpress: ImageView? = null
    private var mReceiverImageView: ImageView? = null
    private var mReceiverNameMtv: MaterialTextView? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_payment)
        binding = DataBindingUtil.setContentView(this,R.layout.fragment_chat)
        shared = PrefrenceShared(this)
        ConnectionManger.listener = this
//        activity!!.window.statusBarColor = ContextCompat.getColor(activity!!,R.color.chatPurple)
//        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        init()

    }



    private fun init() {

        messageList = findViewById(R.id.listchat)
        chatBackpress = findViewById(R.id.chatBackpress)
        mReceiverImageView =findViewById(R.id.chatImage)
        mReceiverNameMtv =findViewById(R.id.chatName)
        chatBackpress?.setOnClickListener(this)
       // ((ctx as MainActivity)).hideBottomNavigation()

        binding?.sendBtn?.setOnClickListener(this)
        // getting chatId through bundle from chatListFragment....
        mReceiverId = intent.getStringExtra("chatId")!!.toString()
        if(intent.getStringExtra("receiverImage")!=null ) {
            mReceiverImage = intent.getStringExtra("receiverImage").toString()
        }
        if(intent.getStringExtra("receiverName")!=null) {
            mReceiverName = intent.getStringExtra("receiverName").toString()
        }
        adapter = MessageAdapter(mReceiverId, shared, ArrayList(), this)
        if (mReceiverImage != "" || mReceiverImage != null) {

            Picasso.get()
                .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + mReceiverImage)
                .resize(4000, 1500)
                .placeholder(R.drawable.profile_pholder)
                .onlyScaleDown()
                .into(mReceiverImageView)
        }
        if ((mReceiverName != "") || (mReceiverName != "null")) {
            if (mReceiverName == "null")
                mReceiverNameMtv?.text = ""
            else {
//                val prefix = mReceiverName.substring(0,1)
//                val suffix = mReceiverName.substring(1)
//                pre
                mReceiverNameMtv?.text = mReceiverName
            }
        } else {
            mReceiverNameMtv?.text = ""
        }
        messageList?.adapter = adapter
        adapter?.notifyDataSetChanged()
        chatApi(mReceiverId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sendBtn -> {

                val message = binding?.msgEdt?.text.toString().trim()
                if (message != "")
//                ConnectionManger.sendMessage(message,shared?.getString(Constants.DataKey.USER_ID)!!,mReceiverId)
                    if (binding?.msgEdt!!.text.isEmpty()) {
//                    onetoonelayout?.mySnack("write something..") {}
                    } else {
                        //   if(senderrole!=null || requestId!=null) {
                        ConnectionManger.sendMessage(
                            message,
                            shared?.getString(Constants.DataKey.USER_ID)!!,
                            mReceiverId
                        )
                        binding?.msgEdt!!.setText("")
                    }
            }
            R.id.chatBackpress -> {
//                fragmentManager?.popBackStack()
                finish()
            }
        }
    }


    private fun chatApi(receiverId: String) {
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.chatApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!, receiverId)
            .enqueue(object : Callback<ChatByIdModel> {
                override fun onResponse(
                    call: Call<ChatByIdModel>,
                    response: Response<ChatByIdModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            mReceiverNameMtv?.text = ""
                            val list = response.body()?.data?.chat?.data
                            val receiverModel = response.body()?.data?.receiver_detail
                            list?.reverse()
                            adapter?.addItemList(list)
                            Picasso.get()
                                .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + receiverModel?.image)
                                .placeholder(R.drawable.profile_pholder)
                                .resize(4000, 1500)
                                .onlyScaleDown()
                                .into(mReceiverImageView)
                            mReceiverNameMtv?.text = receiverModel?.name

                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMesssage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@ChatFragment, "" + errorMesssage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(this@ChatFragment, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ChatByIdModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(this@ChatFragment, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onMessage(text: String) {
    runOnUiThread {
            val jsonObject = JSONObject()
            val model = ChatDataModel()
            try {
                val messageText = JSONObject(text)
                val myMessgae = messageText.getString("message")
                jsonObject.put("message", myMessgae)
                model.message = myMessgae
                model.sender_id = messageText.getInt("sender_id")
                model.attachment = messageText.getString("attachment")
                model.receiver_id = messageText.getInt("receiver_id")
                model.type = messageText.getString("type")
                model.local_message_id = System.currentTimeMillis().toString()

                adapter?.addItem(model)


            } catch (e: JSONException) {
                e.printStackTrace()
            }
       }
    }

    class MessageAdapter(
        val id: String, val shared: PrefrenceShared?,
        val list: ArrayList<ChatDataModel>, var ctx: Context
    ) : BaseAdapter() {

        var messagesList = list

        override fun getCount(): Int {
            return messagesList.size
        }

        override fun getItem(i: Int): ChatDataModel {
            return messagesList[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getViewTypeCount(): Int {
            return super.getViewTypeCount()
        }

        override fun getView(i: Int,itemview: View?, viewGroup: ViewGroup): View {
            var itemview: View? = itemview
            val inflater = LayoutInflater.from(viewGroup.context)

            if (itemview == null) itemview =
                inflater.inflate(R.layout.chat_conversation_layout, viewGroup, false)
            //Initializing views inside chat_conversation_layout....
            val recLayout = itemview?.findViewById<ConstraintLayout>(R.id.recLayout)
            val senderLayout = itemview?.findViewById<ConstraintLayout>(R.id.senderLayout)
//        val recImg: CircleImageView = itemview.findViewById(R.id.recImg)
            val recMessage = itemview?.findViewById<MaterialTextView>(R.id.recMessage)
//        val senderImg: CircleImageView = itemview.findViewById(R.id.senderImg)
            val senderMsg = itemview?.findViewById<MaterialTextView>(R.id.senderMsg)
            val recTimeTxt = itemview?.findViewById<MaterialTextView>(R.id.recTimeTxt)
            val senderTimeTxt = itemview?.findViewById<MaterialTextView>(R.id.senderTimeTxt)
            val dateMtv = itemview?.findViewById<MaterialTextView>(R.id.dateMtv)
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("hh:mm a ") //or use getDateInstance()
        val formatedDate = formatter.format(date)
            val item = messagesList[i]
            try {
                if (item.sender_id.toString() == shared?.getString(Constants.DataKey.USER_ID)) {

                    recLayout?.visibility = View.GONE
                    senderLayout?.visibility = View.VISIBLE
                    senderMsg?.text = item.message
                try {
                    var myDate = ""
                  if (item.created_at.isEmpty()) {
                      myDate =   formatedDate
                      senderTimeTxt?.text = myDate
                    } else {
                      myDate =   item.created_at
                      senderTimeTxt?.text =uTCToLocal("yyyy-MM-dd hh:mm:ss", "hh:mm a", myDate)
                    }

               //     val time = item.created_at


                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                } else {
                    recLayout?.visibility = View.VISIBLE
                    senderLayout?.visibility = View.GONE
                    recMessage?.text = item.message
                try {
                    var myDate = ""
                    myDate = if (item.created_at.isEmpty()) {
                        formatedDate
                    } else {
                        item.created_at
                    }
                  //  val time = item.created_at
                    recTimeTxt?.text =uTCToLocal("yyyy-MM-dd hh:mm:ss", "hh:mm a", myDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                }
            } catch (e: JSONException) {
                recLayout?.visibility = View.GONE
                senderLayout?.visibility = View.GONE
                e.printStackTrace()
            }
            return itemview!!
        }

        fun addItem(item: ChatDataModel) {
            messagesList.add(item)
            notifyDataSetChanged()
        }

        fun addItemList(item: ArrayList<ChatDataModel>?) {
            messagesList.addAll(item as ArrayList<ChatDataModel>)
            notifyDataSetChanged()
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
}

interface IOnMessageReceived {
    fun onMessage(text: String)
}
