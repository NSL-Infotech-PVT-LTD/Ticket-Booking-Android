package com.surpriseme.user.fragments.reviewfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants
import kotlinx.android.synthetic.main.fragment_payment_wait.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewAdapter(val context: Context, val reviewList:ArrayList<ReviewDataModel>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView) {

        val artistImage = itemView.findViewById<ImageView>(R.id.reviewImage)
        val artistName = itemView.findViewById<MaterialTextView>(R.id.reviewNameTxt)
        val rating = itemView.findViewById<RatingBar>(R.id.ratingbar)
        val reviewDesc = itemView.findViewById<MaterialTextView>(R.id.reviewDescTxt)
        val reviewDate = itemView.findViewById<MaterialTextView>(R.id.reviewDateTxt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.review_layout,parent,false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {

        val model = reviewList[position]
        if (model !=null) {
            Picasso.get()
                .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + model.customer_detail.image)
                .placeholder(R.drawable.profile_pholder)
                .into(holder.artistImage)
            holder.artistName.text = model.customer_detail.name
            if (model.rate != null) {
                holder.rating.rating = model.rate.toFloat()
            }

            holder.reviewDesc.text = "  " + model.review + "  "
            val date = model.created_at
            holder.reviewDate.text = uTCToLocal("yyyy-MM-dd hh:mm:ss", "dd MMMM, yyyy", date)

        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

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