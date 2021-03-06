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
import com.surpriseme.user.util.BaseViewHolder
import com.surpriseme.user.util.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingListAdapter(
    val context: Context, val bookingList: ArrayList<BookingArtistDetailModel>,
    val seeFullDetailClick: SeeFullDetailClick
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var fromTime = ""
    private var toTime = ""
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var mType =""
    private var mStatus =""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

//        val view = LayoutInflater.from(context).inflate(R.layout.artist_recycler_layout,parent,false)
//        return ArtistViewHolder(view)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(

                LayoutInflater.from(context).inflate(R.layout.booking_list_layout, parent, false)
            )
            VIEW_TYPE_LOADING -> ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
            )
            else -> null!!
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        if (bookingList.size == 1) {
            return VIEW_TYPE_NORMAL
        }
        return if (isLoaderVisible) {
            if (position == bookingList.size - 1)
                VIEW_TYPE_LOADING
            else
                VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size ?: 0
    }

    fun addItems(postItems: ArrayList<BookingArtistDetailModel>?) {
        bookingList.addAll(postItems!!)
        val myList = bookingList.distinctBy { it.id } as ArrayList
        bookingList.clear()
        bookingList.addAll(myList)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        bookingList.add(BookingArtistDetailModel())
        notifyItemInserted(bookingList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = bookingList.size - 1
        val item: BookingArtistDetailModel = getItem(position)
        if (item != null) {
            bookingList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        bookingList.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): BookingArtistDetailModel {
        return bookingList[position]
    }

    inner class ViewHolder(itemview: View) : BaseViewHolder(itemview) {

        val status = itemview.findViewById<MaterialTextView>(R.id.statusMtv)
        val type = itemview.findViewById<MaterialTextView>(R.id.typeMtv)
        val image = itemview.findViewById<ImageView>(R.id.image)
        val name = itemview.findViewById<MaterialTextView>(R.id.name)
        val dateTxt = itemview.findViewById<MaterialTextView>(R.id.dateTxt)
        val timeTxt = itemview.findViewById<MaterialTextView>(R.id.timeTxt)
        val seeFullDetail = itemview.findViewById<MaterialTextView>(R.id.seeFullDetailMtv)
        val dateMtv = itemview.findViewById<MaterialTextView>(R.id.dateMtv)
        val bookingId = itemview.findViewById<MaterialTextView>(R.id.bookingIdTv)

        override fun clear() {}

        override fun onBind(position: Int) {
            super.onBind(position)

            val bookingModel = bookingList[position]

            mType = bookingModel.type

            bookingId.text = "#" + " " + bookingModel.id.toString()

            if (mType == "digital") {
                mType = context.resources.getString(R.string.virtual)
            } else {
                mType = context.resources.getString(R.string.in_person)
            }
            type.text = mType        // Display type of booking

            mStatus = bookingModel.status

            if (mStatus == "pending") {
                mStatus = context.resources.getString(R.string.Pending)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_yellow_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "rejected") {
                mStatus = context.resources.getString(R.string.Rejected)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_red_bg)
                status.text = mStatus    // Display Status of booking

            } else if (mStatus == "cancel") {
                mStatus = context.resources.getString(R.string.Cancel)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_red_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "accepted") {
                mStatus = context.resources.getString(R.string.Accepted)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_green_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "confirmed") {
                mStatus = context.resources.getString(R.string.Confirmed)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_green_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "processing") {
                mStatus = context.resources.getString(R.string.Processing)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_yellow_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "completed_review") {
                mStatus = context.resources.getString(R.string.Completed)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_green_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "payment_failed") {
                mStatus = context.resources.getString(R.string.Failed)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_red_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "report") {
                mStatus = context.resources.getString(R.string.Report)
                status.background = ContextCompat.getDrawable(context,R.drawable.status_red_bg)
                status.text = mStatus    // Display Status of booking
            } else if (mStatus == "completed") {
                status.background = ContextCompat.getDrawable(context,R.drawable.status_green_bg)
                status.text = context.resources.getString(R.string.Completed)    // Display Status of booking
            }


            // Display date at top of card....
            var date = bookingModel.date
            if (date.isNotEmpty()) {
                var spf = SimpleDateFormat("yyyy-MM-dd")
                val newDate: Date = spf.parse(date)
                spf = SimpleDateFormat("dd-MMM-yyyy")
                date = spf.format(newDate)
                dateMtv.setText(date)

                // Dispaly Image of Artist....
                Picasso.get()
                    .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + bookingModel.artist_detail?.image)
                    .placeholder(R.drawable.user_pholder_updated)
                    .into(image)

                // Display Name of Artist....
                name.text = bookingModel.artist_detail?.name

                // Display date inside card....
                val outSdf = SimpleDateFormat("dd MMMM, yyyy")
                val cardDate = outSdf.format(newDate)
                dateTxt.text = cardDate.toString()

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
                timeTxt.text = "$fromTime to $toTime"

//                if (bookingModel.artist_detail?.category_id_details?.size!! > 0) {
//                    for (i in 0 until bookingModel.artist_detail.category_id_details.size)
//                        addChip(
//                            bookingModel.artist_detail.category_id_details[i].toString(),
//                            categoriesChips
//                        )
//                }
                // See Full Detail button Click....
                seeFullDetail.setOnClickListener {
                    // See Full Detail button click....
                    seeFullDetailClick.fullDetail(bookingModel.id.toString())
                }
                itemView.setOnClickListener {
                    seeFullDetailClick.fullDetail(bookingModel.id.toString())
                }

            }
        }
    }

    class ProgressHolder internal constructor(itemView: View?) :
        BaseViewHolder(itemView) {
        override fun clear() {}
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

    interface SeeFullDetailClick {
        fun fullDetail(bookingID: String)
    }
}