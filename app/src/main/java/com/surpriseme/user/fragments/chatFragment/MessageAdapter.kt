package com.surpriseme.user.fragments.chatFragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.BaseViewHolder
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(
    val id: String, var shared: PrefrenceShared?,
    val list: ArrayList<ChatDataModel>, var ctx: Context, var recycle: RecyclerView

) : RecyclerView.Adapter<BaseViewHolder>() {
    var messagesList = list
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var mDate = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_conversation_layout, parent, false)
//        shared= PrefrenceShared(ctx)
//        return ViewHolder(v)
        shared = PrefrenceShared(ctx)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(

                LayoutInflater.from(ctx).inflate(R.layout.chat_conversation_layout, parent, false)
            )
            VIEW_TYPE_LOADING -> ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
            )
            else -> null!!
        }

    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (messagesList.size == 1) {
            return VIEW_TYPE_NORMAL
        }
        return if (isLoaderVisible) {
            if (position == messagesList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun addItem(item: ChatDataModel) {
        messagesList.add(0, item)
        notifyDataSetChanged()
        recycle.scrollToPosition(0)
    }

    fun addItemList(item: ArrayList<ChatDataModel>?) {
        messagesList.addAll(item as ArrayList<ChatDataModel>)
        notifyDataSetChanged()
    }


    fun addLoading() {
        isLoaderVisible = true
        messagesList.add(ChatDataModel())
        notifyItemInserted(messagesList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = messagesList.size - 1
        val item: ChatDataModel = getItem(position)
        if (item != null) {
            messagesList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        messagesList.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): ChatDataModel {
        return messagesList[position]
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

    inner class ViewHolder(v: View) : BaseViewHolder(v) {
        var recLayout: ConstraintLayout? = null
        var senderLayout: ConstraintLayout? = null
        var recImg: CircleImageView? = null
        var recMessage: MaterialTextView? = null
        var senderMsg: MaterialTextView? = null
        var senderTimeTxt: MaterialTextView? = null
        var recTimeTxt: MaterialTextView? = null
        var dateMtv: MaterialTextView? = null
        var scurve: ImageView? = null
        var rcurve: ImageView? = null

        init {
            recLayout = v.findViewById(R.id.recLayout)
            senderLayout = v.findViewById(R.id.senderLayout)
            recImg = v.findViewById(R.id.recImg)
            recMessage = v.findViewById(R.id.recMessage)
            senderMsg = v.findViewById(R.id.senderMsg)
            recTimeTxt = v.findViewById(R.id.recTimeTxt)
            senderTimeTxt = v.findViewById(R.id.senderTimeTxt)
            dateMtv = v.findViewById(R.id.dateMtv)
            scurve = v.findViewById(R.id.scurve)
            rcurve = v.findViewById(R.id.rcurve)
        }

        override fun clear() {

        }


        override fun onBind(position: Int) {
            super.onBind(position)
            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("hh:mm a") //or use getDateInstance()
            val formatedDate = formatter.format(date)


            mDate = messagesList[position].created_at

            dateMtv?.setText(uTCToLocal("yyyy-MM-dd hh:mm:ss","dd-MMM-yyyy",mDate))

            if(position >= 1  && position <  messagesList.size -1 ){

                val senTime = messagesList.get(position).created_at.split(" ")
                val store = messagesList.get(position + 1).created_at.split(" ")
                if (senTime[0].equals(store[0])) {
                    dateMtv?.visibility = View.GONE
                }else {
                    dateMtv?.visibility = View.VISIBLE
                }
            }else if (  messagesList.size == 1){
                dateMtv?.visibility = View.VISIBLE
            }else if (position ==  messagesList.size -1 ){
                dateMtv?.visibility = View.VISIBLE
            }




//            else {
//                dateMtv?.visibility = View.VISIBLE
//            }


//            dateMtv?.text = mDate



            recMessage?.movementMethod =LinkMovementMethod.getInstance()
            senderMsg?.movementMethod =LinkMovementMethod.getInstance()
            senderMsg?.setOnClickListener {
                val clipboard: ClipboardManager =
                    ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("shared", senderMsg?.getText().toString())
                if (clipboard != null) {
                    Toast.makeText(ctx, "Text Copied", Toast.LENGTH_SHORT).show()
                    clipboard.setPrimaryClip(clip)
                }
            }
            recMessage?.setOnClickListener {
                val clipboard: ClipboardManager =
                    ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("shared", recMessage?.getText().toString())
                if (clipboard != null) {
                    Toast.makeText(ctx, "Text Copied", Toast.LENGTH_SHORT).show()
                    clipboard.setPrimaryClip(clip)
                }
            }
            val item = messagesList[position]
            try {

//                 var setDateAMPm: String? = null
//                 val getDateFormat1 = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//                 val convert1 = SimpleDateFormat("EEE, MMM dd, yyyy")
//                 try {
//                     val getDate = getDateFormat1.parse(messagesList[position].created_at.toString() + "")
//                     //  String setDateAMPm = null;
//                     if (getDate != null) {
//                         setDateAMPm = convert1.format(getDate)
//                     }
//                     dateMtv?.text = setDateAMPm
//                 } catch (e: ParseException) {
//                     e.printStackTrace()
//                 }
//
//                 if (position > 0) {
//                     val current: String = messagesList[position].created_at.split(" ")[0]
//                     val newone: String = messagesList[position - 1].created_at.split(" ")[0]
//                     if (current == newone) {
//                         dateMtv?.visibility = View.GONE
//                     } else {
//                         dateMtv?.visibility = View.VISIBLE
//                     }
//                 } else {
//                     dateMtv?.visibility = View.VISIBLE
//                 }


//                 val date = messagesList[0].created_at
//
//                if (i > 0) {
//                    val senTime = messagesList.get(i).created_at!!.split(" ")
//                    val store = messagesList.get(i - 1).created_at!!.split(" ")
//
//                    if (senTime[0].equals(store[0])) {
//                        dateMtv?.visibility = View.GONE
//                    } else {
//                        dateMtv?.visibility = View.VISIBLE
//                    }
//                } else {
//                    dateMtv?.visibility = View.VISIBLE
//                }
                if (item.sender_id.toString() == shared?.getString(Constants.DataKey.USER_ID)) {

                    recLayout?.visibility = View.GONE
                    recTimeTxt?.visibility = View.GONE
                    rcurve?.visibility = View.GONE
                    recImg?.visibility = View.GONE
                    senderLayout?.visibility = View.VISIBLE
                    senderTimeTxt?.visibility = View.VISIBLE
                    scurve?.visibility = View.VISIBLE
                    senderMsg?.text = item.message

                    try {
                        var myDate = ""
                        if (item.created_at.isEmpty()) {
                            myDate = formatedDate
                            senderTimeTxt?.text = myDate
                        } else {
                            myDate = item.created_at
                            senderTimeTxt?.text =
                                uTCToLocal("yyyy-MM-dd hh:mm:ss", "hh:mm a", myDate)
                        }

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                } else {
                    recLayout?.visibility = View.VISIBLE
                    recTimeTxt?.visibility = View.VISIBLE
                    rcurve?.visibility = View.VISIBLE
                    recImg?.visibility = View.VISIBLE
                    senderLayout?.visibility = View.GONE
                    senderTimeTxt?.visibility = View.GONE
                    scurve?.visibility = View.GONE
                    recMessage?.text = item.message
                    Picasso.get()
                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + item.sender_image)
                        .placeholder(R.drawable.profile_pholder).into(recImg)

                    try {
                        var myDate = " "
                        myDate = if (item.created_at.isEmpty()) {
                            formatedDate
                        } else {
                            item.created_at
                        }

                        recTimeTxt?.text = uTCToLocal("yyyy-MM-dd hh:mm:ss", "hh:mm a", myDate)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: JSONException) {
                recLayout?.visibility = View.GONE
                recImg?.visibility = View.GONE
                rcurve?.visibility = View.GONE
                recTimeTxt?.visibility = View.GONE

                senderLayout?.visibility = View.GONE
                senderTimeTxt?.visibility = View.GONE
                scurve?.visibility = View.GONE
                e.printStackTrace()
            }

        }
    }

    class ProgressHolder internal constructor(itemView: View?) :
        BaseViewHolder(itemView) {
        override fun clear() {}
    }


}