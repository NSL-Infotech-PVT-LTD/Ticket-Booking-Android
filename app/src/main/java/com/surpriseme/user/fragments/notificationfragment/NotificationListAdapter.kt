package com.surpriseme.user.fragments.notificationfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import de.hdodenhof.circleimageview.CircleImageView

class NotificationListAdapter(val context: Context, val notiList:ArrayList<NotificationListDataModel>,
val notificationDetail:NotificationDetail) : RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val notiActiveIcon = itemView.findViewById<CircleImageView>(R.id.notiActiveIcon)
        val notiIcon = itemView.findViewById<ImageView>(R.id.notiIcon)
        val notiTitle = itemView.findViewById<MaterialTextView>(R.id.notiTitleMtv)
        val notiBody = itemView.findViewById<MaterialTextView>(R.id.notiBodyMtv)
        val dateTime = itemView.findViewById<MaterialTextView>(R.id.dateTimeMtv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.notification_list_layout,parent,false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        val model = notiList[position]

        if (notiList[position].is_read == "0") {
            holder.notiActiveIcon.visibility = View.VISIBLE
            holder.itemView.background = ContextCompat.getDrawable(context,R.drawable.notification_bg_grey)
        }else {
            holder.notiActiveIcon.visibility = View.GONE
            holder.itemView.background = ContextCompat.getDrawable(context,R.drawable.notification_bg_white)
        }

        holder.notiTitle.text = model.title
        holder.notiBody.text = model.body

        holder.itemView.setOnClickListener {
            notificationDetail.detail(model.booking_detail.target_id.toString(),model.id.toString(),model.booking_detail.target_model)
        }
    }

    override fun getItemCount(): Int {
        return notiList.size
    }

    interface NotificationDetail{
        fun detail(id:String,notificationId:String, targetModel:String)
    }
}