package com.surpriseme.user.fragments.chatListfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(val context: Context, val chatList: ArrayList<ChatDetailModel>,val goToChat:GoToChat) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    var shared:PrefrenceShared?=null

    inner class ChatListViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.image)
        val name = itemView.findViewById<MaterialTextView>(R.id.name)
        val dateTime = itemView.findViewById<MaterialTextView>(R.id.dateTimeMtv)
        val message = itemView.findViewById<MaterialTextView>(R.id.message)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout, parent, false)
        shared= PrefrenceShared(context)
        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {

        val model = chatList[position]
        if (shared?.getString(Constants.DataKey.USER_ID) == model.sender_id.toString()) {

            Picasso.get().load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + model.receiver_image)
                .resize(4000, 1500)
                .onlyScaleDown()
                .into(holder.image)

            holder.name.text = model.receiver_name
            holder.message.text = model.message
            holder.itemView.setOnClickListener {

                goToChat.chatId(model.receiver_id.toString(),model.receiver_image.toString(),model.receiver_name.toString())
            }
        } else {
            Picasso.get().load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + model.sender_image)
                .resize(4000, 1500)
                .onlyScaleDown()
                .into(holder.image)

            holder.name.text = model.sender_name
            holder.message.text = model.message
            holder.itemView.setOnClickListener {

                goToChat.chatId(model.sender_id.toString(),model.sender_image.toString(),model.sender_name.toString())
            }
        }

        val date = model.created_at
        holder.dateTime.setText(uTCToLocal("yyyy-MM-dd hh:mm:ss", "dd-MMM, hh:mm a", date))


    }

    override fun getItemCount(): Int {
        return chatList.size
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

    interface GoToChat {
        fun chatId(chatid:String, rImage:String,rName:String)
    }

}