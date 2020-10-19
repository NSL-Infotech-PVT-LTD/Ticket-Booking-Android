package com.surpriseme.user.fragments.bookingslotfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookSlotAdapter(
    private val context: Context,
    private val list: Array<String>,
    private val selectDate: SelectDate,
    private val bookSlot: BookSlot
) :
    RecyclerView.Adapter<BookSlotAdapter.BookSlotViewHolder>() {

    private var rowPosition: Int = -1
    private var time = ""
    private var fromTime = ""
    private var toTime = ""
    var setFromTime: String? = null
    var setToTime: String? = null
    var slotPosition: Int? = null
    var lowPosition: Int? = null
    var isClear = false
    private var selectedSlot: ArrayList<Int> = ArrayList()

    inner class BookSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val timeTxt = itemView.findViewById<MaterialTextView>(R.id.timeTxt)!!

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getHighestSlot(lowPosition: Int, slotPosition: Int) {
        this.slotPosition = slotPosition
        this.lowPosition = lowPosition
        notifyDataSetChanged()
    }

    fun settSlotClear(isClear: Boolean) {
        this.isClear = isClear
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSlotViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.book_slot_layout, parent, false)
        return BookSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookSlotViewHolder, position: Int) {

        holder.timeTxt.text = list[position]
        if (isClear) {
            slotPosition = null
            holder.timeTxt.background = ContextCompat.getDrawable(context, R.drawable.boxcardview)
        } else if (slotPosition != null && position in lowPosition!!..slotPosition!!) {
            holder.timeTxt.background = ContextCompat.getDrawable(context, R.drawable.time_slot_bg)
        }
        holder.itemView.setOnClickListener {
            bookSlot.slotPosition(position)
            rowPosition = position
            selectedSlot.add(rowPosition)

            if (slotPosition == null) {
                isClear = false
                holder.timeTxt.background =
                    ContextCompat.getDrawable(context, R.drawable.time_slot_bg)
            }
            time = list[position]           // gettimg time from List
            fromTime = time.split("-")[0]   // gettimg fromTime from List
            toTime = time.split("-")[1]     // // gettimg toTime from List

            val fromDateFormat = SimpleDateFormat("hh:mm aa")

            val fromConvert = SimpleDateFormat("HH:mm")
            try {
                val getDate: Date = fromDateFormat.parse(fromTime)
                if (getDate != null) {
                    setFromTime = fromConvert.format(getDate)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val toDateFormat = SimpleDateFormat("hh:mm aa")

            val toConvert = SimpleDateFormat("HH:mm")
            try {
                val getDate: Date = toDateFormat.parse(toTime)
                if (getDate != null) {
                    setToTime = toConvert.format(getDate)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            selectDate.date(setFromTime!!, setToTime!!)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    interface SelectDate {
        fun date(fromTime: String, toTime: String)
    }

    interface BookSlot {
        fun slotPosition(position: Int)
    }
}