package com.surpriseme.user.fragments.bookingslotfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookSlotAdapter(
    private val context: Context,
    private val list: ArrayList<SlotDataModel>,
    private val selectDate: SelectDate,
    private val bookSlot: BookSlot
) :
    RecyclerView.Adapter<BookSlotAdapter.BookSlotViewHolder>() {


    private var fromTime = ""
    private var toTime = ""
    var setFromTime: String? = null
    var setToTime: String? = null
    var slotPosition: Int? = null
    var lowPosition: Int? = null
    var isClear = false
    val addToList: ArrayList<SlotDataModel> = ArrayList()

    class BookSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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

        holder.timeTxt.text = list[position].hour
        if (list[position].booked == "1") {
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.slot_grey)
        } else if (list[position].date.isEmpty()) {
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.slot_grey)
        }

        holder.itemView.isEnabled = !(list[position].booked == "1" || list[position].date.isEmpty())

        holder.itemView.setOnClickListener {

            if (!list[position].isBooked) {
                addToList.add(list[position])
                if (addToList.size < 2) {
                    holder.itemView.background =
                        ContextCompat.getDrawable(context, R.drawable.slot_green)
                    list[position].isBooked = true
                    selectDate.date(addToList)

                } else if (addToList.size > 1) {
                    if (list[position - 1].isBooked == false && list[position + 1].isBooked == false) {
                        addToList.remove(list[position])
                        Toast.makeText(context, "You cant select this slot", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        holder.itemView.background =
                            ContextCompat.getDrawable(context, R.drawable.slot_green)
                        list[position].isBooked = true
                        selectDate.date(addToList)
                    }
                }

            } else if (list[position].isBooked) {
                holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.slot_bg)
                list[position].isBooked = false
                addToList.remove(list[position])
                selectDate.date(addToList)
            }
//            bookSlot.slotPosition(position)
//            rowPosition = position
//            selectedSlot.add(rowPosition)
//
//            addToList.add(list[position])
//            Toast.makeText(context,"" + list[position].toString(),Toast.LENGTH_SHORT).show()
//            if (addToList.size <2) {
//                addToList[position].isBooked =true
//                holder.timeTxt.background =
//                    ContextCompat.getDrawable(context, R.drawable.slot_green)
//            } else {
//                if (list[position -1].booked =="0" || list[position -1].date.isEmpty()
//                    || list[position +1].booked =="0" || list[position +1].date.isEmpty() ) {
//                    Toast.makeText(context, "You Cant select this time slot",Toast.LENGTH_SHORT).show()
//                } else {
//                    addToList[position].isBooked = true
//                    holder.timeTxt.background =
//                        ContextCompat.getDrawable(context, R.drawable.slot_green)
//                }
//            }


//            if (slotPosition == null) {
//                isClear = false
//                holder.timeTxt.background =
//                    ContextCompat.getDrawable(context, R.drawable.slot_green)
//            }

        }

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

//        if (setFromTime != null && setToTime != null)
//            selectDate.date(setFromTime!!, setToTime!!)
//


        if (isClear) {
            slotPosition = null
            if (list[position].booked == "1" || list[position].date.isEmpty()) {
                holder.itemView.background =
                    ContextCompat.getDrawable(context, R.drawable.slot_grey)
            } else {
                holder.itemView.background =
                    ContextCompat.getDrawable(context, R.drawable.boxcardview)
                addToList.clear()
                for (i in 0 until list.size) {
                    list[i].isBooked = false
                }

//


            }
        } else if (slotPosition != null && position in lowPosition!!..slotPosition!! && list[position].date.isNotEmpty() && list[position].booked == "0") {
            list[position].isBooked = true
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.slot_green)

        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

    interface SelectDate {
        fun date(slotList: ArrayList<SlotDataModel>)
    }

    interface BookSlot {
        fun slotPosition(position: Int)
    }
}