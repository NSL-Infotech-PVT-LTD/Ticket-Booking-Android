package com.surpriseme.user.fragments.chatFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.util.PrefrenceShared
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(val context: Context, val receiverList: List<ChatDataModel>) :RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    val shared: PrefrenceShared = PrefrenceShared(context)

    class ChatViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val recLayout: ConstraintLayout = itemview.findViewById(R.id.recLayout)
        val senderLayout: ConstraintLayout = itemview.findViewById(R.id.senderLayout)
        //        val recImg: CircleImageView = itemview.findViewById(R.id.recImg)
        val recMessage: MaterialTextView = itemview.findViewById(R.id.recMessage)
        //        val senderImg: CircleImageView = itemview.findViewById(R.id.senderImg)
        val senderMsg: MaterialTextView = itemview.findViewById(R.id.senderMsg)
        val recTimeTxt: MaterialTextView = itemview.findViewById(R.id.recTimeTxt)
        val senderTimeTxt: MaterialTextView = itemview.findViewById(R.id.senderTimeTxt)
        val dateMtv: MaterialTextView = itemview.findViewById(R.id.dateMtv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.chat_conversation_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val model: ChatDataModel = receiverList.get(position)
//        val inpuFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//        val outputFormat = SimpleDateFormat("dd-MMM-yyyy")
//        val date = inpuFormat.parse(model.created_at)
        val date = model.created_at
        holder.dateMtv.setText(uTCToLocal("yyyy-MM-dd hh:mm:ss","dd-MMM-yyyy",date))
    }

    override fun getItemCount(): Int {
        return receiverList.size
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