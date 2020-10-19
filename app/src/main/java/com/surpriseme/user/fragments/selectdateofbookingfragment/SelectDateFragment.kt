package com.surpriseme.user.fragments.selectdateofbookingfragment

import android.app.DatePickerDialog
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

        val calendar = Calendar.getInstance()
        binding.calenderView.minDate = calendar.getTimeInMillis()
        binding.calenderView.setOnDateChangeListener(object : CalendarView.OnDateChangeListener {
            override fun onSelectedDayChange(
                view: CalendarView,
                year: Int,
                month: Int,
                dayOfMonth: Int
            ) {

                val checkMonth = month+1
                val sDate = "$year-$checkMonth-$dayOfMonth"
                val sdf = SimpleDateFormat("yyyy-mm-DD")
                val date = sdf.parse(sDate)
                val finalDate = sdf.format(date)


                if (finalDate.isEmpty()) {

                } else {
                    val bundle = Bundle()
                    bundle.putString("selectedDate", finalDate)
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

        }
    }

    private fun openCalender() {

        sdate = ""
        // open date picker code below....
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]
        val picker = DatePickerDialog(
            activity!!,
            { view, year, month, dayOfMonth -> //                        binding.dob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                sdate = year.toString() + "/" + (month + 1) + "/" + dayOfMonth

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                date = sdf.parse(sdate)
                var cDate = sdf.format(date)

                if (sdate.isEmpty()) {

                } else {
                    val bundle = Bundle()
                    bundle.putString("selectedDate", cDate.toString())
                    val fragment = BookSlotFragment()
                    fragment.arguments = bundle
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameContainer, fragment)
                    transaction?.addToBackStack("selectDateFragmement")
                    transaction?.commit()
                }
                Toast.makeText(ctx, "" + cDate, Toast.LENGTH_SHORT).show()
            }, year, month, day
        )
        picker.getDatePicker().minDate = System.currentTimeMillis()
        picker.show()

    }
}