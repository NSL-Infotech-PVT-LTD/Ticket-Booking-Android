package com.surpriseme.user.fragments.bookingfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingListAdapter(val context: Context, val bookingList: ArrayList<BookingArtistDetailModel>,
                         val seeFullDetailClick:SeeFullDetailClick) :RecyclerView.Adapter<BookingListAdapter.BookingListViewHolder>() {

    private var fromTime = ""
    private var toTime = ""

    inner class BookingListViewHolder(itemview: View) :RecyclerView.ViewHolder(itemview) {

        val status = itemview.findViewById<MaterialTextView>(R.id.statusMtv)
        val type = itemview.findViewById<MaterialTextView>(R.id.typeMtv)
        val image = itemview.findViewById<ImageView>(R.id.image)
        val name = itemview.findViewById<MaterialTextView>(R.id.name)
        val dateTxt = itemview.findViewById<MaterialTextView>(R.id.dateTxt)
        val timeTxt = itemview.findViewById<MaterialTextView>(R.id.timeTxt)
        val seeFullDetail = itemview.findViewById<MaterialTextView>(R.id.seeFullDetailMtv)
        val categoriesChips = itemview.findViewById<ChipGroup>(R.id.chipGroup)
        val address = itemview.findViewById<MaterialTextView>(R.id.addressTxt)
        val rating = itemview.findViewById<RatingBar>(R.id.ratingbar)
        val description = itemview.findViewById<MaterialTextView>(R.id.descTxt)
        val dateMtv = itemview.findViewById<MaterialTextView>(R.id.dateMtv)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingListViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.booking_list_layout, parent, false)
        return BookingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingListViewHolder, position: Int) {

        val bookingModel = bookingList[position]
        if (bookingModel.status == Constants.COMPLETE_REVIEW) {
            holder.rating.visibility = View.VISIBLE
            holder.description.visibility = View.VISIBLE
        } else {
            holder.rating.visibility = View.GONE
            holder.description.visibility = View.GONE
        }

        if (bookingModel.rate_detail !=null) {
            holder.rating.rating = bookingModel.rate_detail.rate.toFloat()
            holder.description.text = bookingModel.rate_detail.review
        }

        holder.type.text = bookingModel.type        // Display type of booking
        holder.status.text = bookingModel.status    // Display Status of booking

        // Display date at top of card....
        var date = bookingModel.date
        var spf = SimpleDateFormat("yyyy-MM-dd")
        val newDate: Date = spf.parse(date)
        spf = SimpleDateFormat("dd-MMM-yyyy")
        date = spf.format(newDate)
        holder.dateMtv.setText(date)

        // Dispaly Image of Artist....
        Picasso.get().load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + bookingModel.artist_detail.image)
            .placeholder(R.drawable.user_pholder_updated)
            .resize(4000, 1500)
            .onlyScaleDown()
            .into(holder.image)
        // Display Name of Artist....
        holder.name.text = bookingModel.artist_detail.name

        // Display date inside card....
        val outSdf = SimpleDateFormat("dd-MMMM-yyyy")
        val cardDate = outSdf.format(newDate)
        holder.dateTxt.text = cardDate.toString()

        // converting time to display in card....
        fromTime = bookingModel.from_time
        toTime = bookingModel.to_time
        val fromSdf = SimpleDateFormat("HH:mm")
        val fromConvert = fromSdf.parse(fromTime)
        val fromTime = fromSdf.format(fromConvert!!)

        val toSdf = SimpleDateFormat("HH:mm")
        val toConvert = toSdf.parse(toTime)
        val toTime = toSdf.format(toConvert!!)


//        holder.timeTxt.text = fromTime + " " +  toTime
        holder.timeTxt.text = "$fromTime to $toTime"
        holder.address.text = bookingModel.address

        if (bookingModel.artist_detail.category_id_details .size >0) {
            for (i in 0 until bookingModel.artist_detail.category_id_details.size)
                addChip(
                    bookingModel.artist_detail.category_id_details[i].toString(),
                    holder.categoriesChips
                )
        }
        // See Full Detail button Click....
        holder.seeFullDetail.setOnClickListener{
            // See Full Detail button click....
            seeFullDetailClick.fullDetail(bookingModel.id.toString())


        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    private fun addChip(pItem: String, pChipGroup: ChipGroup) {
        val lChip = Chip(context)
        lChip.text = pItem
        lChip.isClickable = false
        lChip.textSize = 13F
// lChip.chipStrokeColor = resources.getColorStateList(R.color.colorAccent)
// lChip.chipStrokeWidth = 1F
        lChip.setEnsureMinTouchTargetSize(false)
        lChip.setTextColor(ContextCompat.getColor(context, R.color.white))
        lChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.grey_color)
// following lines are for the demo
        pChipGroup.addView(lChip as View)

    }

    interface SeeFullDetailClick{
        fun fullDetail(bookingID:String)
    }
}