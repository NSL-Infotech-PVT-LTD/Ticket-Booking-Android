package com.surpriseme.user.fragments.bookingslotfragment

import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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

    private var popWindowNotAvailable:PopupWindow?=null

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

//        holder.itemView.isEnabled = !(list[position].booked == "1" || list[position].date.isEmpty())


        holder.itemView.setOnClickListener {

            if (list[position].booked == "1" || list[position].date.isEmpty()) {
                popupArtistNotAvailable()
                Handler().postDelayed(Runnable {
                    popWindowNotAvailable?.dismiss()
                },1000)

            } else {
                if (!list[position].isBooked) {

                    if (addToList.size > 0) {
                        addToList.clear()
                        slotPosition = position
                        checkSlot(position, holder)
                    } else {
                        lowPosition = position
                        addToList.add(list[position])
                        holder.itemView.background =
                            ContextCompat.getDrawable(context, R.drawable.slot_green)
                        list[position].isBooked = true
                        selectDate.date(addToList)

                    }
                } else if (list[position].isBooked) {
                    holder.itemView.background =
                        ContextCompat.getDrawable(context, R.drawable.slot_bg)
                    list[position].isBooked = false
                    addToList.clear()
                    selectDate.date(addToList)
                    if (slotPosition != null && position in position..slotPosition!! && list[position].date.isNotEmpty() && list[position].booked == "0") {
                        for (i in position until slotPosition!! + 1) {
                            list[i].isBooked = false
                        }
                        notifyDataSetChanged()
                    }
                }
            }

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

            }
        } else if (slotPosition != null && position in lowPosition!!..slotPosition!! && list[position].date.isNotEmpty() && list[position].booked == "0") {
            if (list[position].isBooked){
                addToList.add(list[position])
                selectDate.date(addToList)
                holder.itemView.background =
                    ContextCompat.getDrawable(context, R.drawable.slot_green)
            } else
                holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.slot_bg)

        }

    }

    private fun checkSlot(position: Int, holder: BookSlotViewHolder) {

        slotPosition = position
        if (slotPosition!! < lowPosition!!) {
            slotPosition = slotPosition!! + lowPosition!!
            lowPosition = slotPosition!! - lowPosition!!
            slotPosition = slotPosition!! - lowPosition!!
        }

        if (slotPosition != null && position in lowPosition!!..slotPosition!! && list[position].date.isNotEmpty() && list[position].booked == "0") {
            for (i in lowPosition!! until slotPosition!!+1) {
                list[i].isBooked = true

            }
            notifyDataSetChanged()

        } else {
            Toast.makeText(context, "You cant select this slot", Toast.LENGTH_SHORT).show()
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

    // popup will display to select currency.
    private fun popupArtistNotAvailable() {

        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.artist_not_available_pop_layout, null)
         popWindowNotAvailable = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popWindowNotAvailable?.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popWindowNotAvailable?.elevation = 10f
        }
        popWindowNotAvailable?.isTouchable = false
        popWindowNotAvailable?.isOutsideTouchable = false

    }
}