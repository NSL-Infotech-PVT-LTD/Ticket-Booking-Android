package com.surpriseme.user.fragments.selectdateofbookingfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentSelectDateBinding
import com.surpriseme.user.fragments.bookingslotfragment.BookSlotFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SelectDateFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSelectDateBinding
    private var sdate = ""
    private lateinit var ctx: Context
    private lateinit var date: Date

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_date, container, false)
        val view = binding.root

        init()

        return view

    }

    private fun init() {

        // initializing views here....
        binding.backpress.setOnClickListener(this)
        // Open calender with tomorrow as minimum date
        openCalendar()
    }

    private fun openCalendar() {
        val calendar = Calendar.getInstance()

        val twoDaysAgo = calendar.clone() as Calendar
        twoDaysAgo.add(Calendar.DATE, 1)
        binding.calenderView.minDate = twoDaysAgo.timeInMillis
        binding.calenderView.setOnDateChangeListener(object : CalendarView.OnDateChangeListener {
            override fun onSelectedDayChange(
                view: CalendarView,
                year: Int,
                month: Int,
                dayOfMonth: Int
            ) {

                val checkMonth = month + 1
                val day = dayOfMonth
                val sDate = "$year-$checkMonth-$day"
                var newDateStr = ""
                var weekDay = ""

                val sdf = SimpleDateFormat("yyyy-mm-dd")
                val dateToFormat = sdf.parse(sDate)
                val finalDate = sdf.format(dateToFormat)

                try {
                    val dateStr = sDate

                    val curFormater = SimpleDateFormat("yyyy-MM-dd")
                    val dateObj = curFormater.parse(dateStr)
                    val postFormater = SimpleDateFormat("dd MMMM , yyyy")
                    newDateStr = postFormater.format(dateObj)
                    weekDay = SimpleDateFormat("EEEE", Locale.ENGLISH).format(dateObj)

                } catch (e: ParseException) {
                    Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT).show()
                }

                if (finalDate.isEmpty()) {

                } else {
                    val bundle = Bundle()
                    bundle.putString("selectedDate", finalDate)
                    bundle.putString("displayDate", newDateStr)
                    bundle.putString("weekday", weekDay)
                    val fragment = BookSlotFragment()
                    fragment.arguments = bundle
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameContainer, fragment)
                    transaction?.addToBackStack("selectDateFragmement")
                    transaction?.commit()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                fragmentManager?.popBackStack()
            }

        }
    }
}