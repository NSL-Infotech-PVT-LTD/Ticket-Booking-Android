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
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentSelectDateBinding
import com.surpriseme.user.fragments.bookingslotfragment.BookSlotFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.InvalidAuth
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SelectDateFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSelectDateBinding
    private var shared:PrefrenceShared?=null
    private var sdate = ""
    private lateinit var ctx: Context
    private lateinit var date: Date
    private var backpress:MaterialTextView?=null
    private var calendarDateList:ArrayList<CalendarDateList> = ArrayList()
    private var calenderList:ArrayList<Calendar> = ArrayList()
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private var artistID = ""
    private var showTypeTv:MaterialTextView?=null


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
        shared = PrefrenceShared(ctx)

        showTypeTv = view.findViewById(R.id.showTypeTv)
        showTypeTv?.visibility = View.VISIBLE
        if (Constants.SHOW_TYPE == ctx.resources.getString(R.string.digital)) {
            showTypeTv?.text = ctx.resources.getString(R.string.virtual)
        } else {
            showTypeTv?.text = ctx.resources.getString(R.string.in_person)
        }

        init(view)

        return view

    }

    private fun init(view: View) {

        artistID = shared?.getString(Constants.ARTIST_ID)!!

        // initializing views here....
        backpress = view.findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)

        // calling booking list date api....
        customerBookingListDateApi()

        binding.calendarView.setMinimumDate(Calendar.getInstance())
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {

            override fun onDayClick(eventDay: EventDay) {

                if(calenderList.contains(eventDay.calendar)) {


                    val cal: Calendar = eventDay.calendar
                    val year = cal[Calendar.YEAR]
                    var month = cal[Calendar.MONTH]
                    month += 1
                    val dayOfMonth = cal[Calendar.DAY_OF_MONTH]
                    var newDateStr = ""
                    var weekDay = ""
//Toast.makeText(ctx,"" + year.toString() +"\n" + month.toString() +"" + dayOfMonth.toString(),Toast.LENGTH_SHORT).show()
                    val sDate = " $year-$month-$dayOfMonth"
                    val sdf = SimpleDateFormat("yyyy-mm-dd")
                    val dateToFormat = sdf.parse(sDate)
                    val finalDate = sdf.format(dateToFormat)

                    try {
                        val dateStr = sDate

                        val curFormater = SimpleDateFormat("yyyy-MM-dd")
                        val dateObj = curFormater.parse(dateStr)
                        val postFormater = SimpleDateFormat("dd MMMM , yyyy")
                        newDateStr = postFormater.format(dateObj!!)
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
//                val sdfParse = SimpleDateFormat("yyyy-MM-dd")
//                val sdf = SimpleDateFormat("dd MMMM yyyy, EEEE")
//                date = sdfParse.parse(finalDate)
//
//                Toast.makeText(ctx,"" + date.toString(),Toast.LENGTH_SHORT).show()
//                strDate = sdf.format(date!!)
//                val model = DateModel()
//                model.date = strDate
//                model.isnew =true
//
//
//                dateList.add(model)
//                haskey[finalDate] = arrayListOf()


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

    // Customer booking list date api....

    private fun customerBookingListDateApi() {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.calendarDateList(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,artistID)
            .enqueue(object :Callback<CalendarDateListModel> {
                override fun onResponse(
                    call: Call<CalendarDateListModel>,
                    response: Response<CalendarDateListModel>
                ) {

                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {

                            calendarDateList.clear()
                            calendarDateList .addAll(response.body()?.data!!)
                            if (calendarDateList.isNotEmpty()) {


                                for (i in 0 until calendarDateList.size) {
                                    val c = Calendar.getInstance()
                                    var date =   sdf.parse(calendarDateList[i].date)
                                    c.time =date!!


                                    calenderList.add(c)


                                }
                                binding.calendarView.selectedDates = calenderList

                            } else {

                            }
                        }
                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx,"" + errorMessage,Toast.LENGTH_SHORT).show()

                            }catch (e:JSONException) {
                                Toast.makeText(ctx,"" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CalendarDateListModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx,"" + t.message.toString(),Toast.LENGTH_SHORT).show()

                }


            })

    }
}