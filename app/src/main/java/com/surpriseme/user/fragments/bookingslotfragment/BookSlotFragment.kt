package com.surpriseme.user.fragments.bookingslotfragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.SelectBank
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.payment.PaymentActivity
import com.surpriseme.user.databinding.FragmentBookSlotBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefManger
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class BookSlotFragment : Fragment(), View.OnClickListener, BookSlotAdapter.SelectDate,
    BookSlotAdapter.BookSlot {

    private lateinit var binding: FragmentBookSlotBinding
    private lateinit var ctx: Context
    private var date = ""
    private var dispDate = ""
    private var weekday = ""
    private lateinit var shared: PrefrenceShared
    private var prefManager: PrefManger? = null
    private var mFromTime = ""
    private var mToTime = ""
    private var artistID = ""
    private var adapter: BookSlotAdapter? = null
    private val list = ArrayList<Int>()
    private var slotList: ArrayList<SlotDataModel> = ArrayList()
    private val fakeList: ArrayList<SlotDataModel> = ArrayList()
    private var selectedSlotList: ArrayList<SlotDataModel> = ArrayList()
    private var backpress: MaterialTextView? = null
    private var showType = ""
    private var bookingId = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_slot, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)
        prefManager = PrefManger(ctx)

        init(view)

        return view
    }

    private fun init(view: View) {

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text = Utility.randomString(ctx)

        // initialization of views....
        backpress = view.findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        artistID = shared.getString(Constants.ARTIST_ID)

        ((ctx as MainActivity)).hideBottomNavigation()

        binding.proceedToCheckoutBtn.setOnClickListener(this)
        binding.clearAllBtn.setOnClickListener(this)

        // getting date for api parameter....
        if (arguments?.getString("selectedDate") != null) {
            date = arguments?.getString("selectedDate")!!
//            binding.dateDispText.text = date
        }
        // getting full date to display....
        if (arguments?.getString("displayDate") != null) {
            dispDate = arguments?.getString("displayDate")!!
            binding.dateDispText.text = dispDate
        }
        // getting weekday ....
        if (arguments?.getString("weekday") != null) {
            weekday = arguments?.getString("weekday")!!
            binding.weekDayText.text = weekday
        }

        showType = if (Constants.SHOW_TYPE == "digital") {
            "digital"
        } else {
            "live"
        }
        initializeRecycler()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.proceedToCheckoutBtn -> {
                binding.proceedToCheckoutBtn.isEnabled = false
                popupBookingConfirm()
                Handler().postDelayed({
                    binding.proceedToCheckoutBtn.isEnabled = true
                }, 2000)

            }
            R.id.clearAllBtn -> {
                adapter?.settSlotClear(true)
                list.clear()
                selectedSlotList.clear()
                binding.proceedToCheckoutBtn.background =
                    ContextCompat.getDrawable(ctx, R.drawable.proceed_to_check_unselected)
            }
            R.id.backpress -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun initializeRecycler() {

        val layoutManager = GridLayoutManager(ctx, 2)
        binding.timeSlotRecycler.layoutManager = layoutManager
//        val timeArray:ArrayList<SelectTimeModel> = ArrayList()

        fakeList.add(SlotDataModel("0", "", "12:00 am - 01:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "01:00 am - 02:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "02:00 am - 03:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "03:00 am - 04:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "04:00 am - 05:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "05:00 am - 06:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "06:00 am - 07:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "07:00 am - 08:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "08:00 am - 09:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "09:00 am - 10:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "10:00 am - 11:00 am", 0, false))
        fakeList.add(SlotDataModel("0", "", "11:00 am - 12:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "12:00 pm - 01:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "01:00 pm - 02:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "02:00 pm - 03:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "03:00 pm - 04:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "04:00 pm - 05:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "05:00 pm - 06:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "06:00 pm - 07:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "07:00 pm - 08:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "08:00 pm - 09:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "09:00 pm - 10:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "10:00 pm - 11:00 pm", 0, false))
        fakeList.add(SlotDataModel("0", "", "11:00 pm - 12:00 am", 0, false))

        bookingSlotApi()
    }

    // booking Slot Api....
    private fun bookingSlotApi() {

        binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.bookingSlotApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            date,
            artistID
        )
            .enqueue(object : Callback<SlotModel> {
                override fun onResponse(call: Call<SlotModel>, response: Response<SlotModel>) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            slotList.clear()
                            slotList = response.body()?.data!!

                            if (slotList.isNotEmpty()) {
                                for (element in response.body()?.data!!) {
                                    val sdf = SimpleDateFormat("HH:mm:ss")
                                    val fromSdf = SimpleDateFormat("hh:mm aa")
                                    val fromConvert = sdf.parse(element.hour)
                                    val fromTime = fromSdf.format(fromConvert!!)

                                    for (i in 0 until fakeList.size) {
                                        val split = fakeList[i].hour.split(" - ")
                                        if (split[0] == fromTime.toLowerCase()) {
                                            fakeList[i].booked = element.booked
                                            fakeList[i].date = element.date
                                            fakeList[i].hour = fakeList[i].hour
                                            fakeList[i].id = element.id
                                        }
                                    }

                                }
                                adapter = BookSlotAdapter(
                                    ctx,
                                    fakeList,
                                    this@BookSlotFragment,
                                    this@BookSlotFragment
                                )
                                binding.timeSlotRecycler.adapter = adapter

                                binding.slotDetailLayout.visibility = View.VISIBLE
                                binding.noArtistFound.visibility = View.GONE
                            } else {
                                binding.slotDetailLayout.visibility = View.GONE
                                binding.noArtistFound.visibility = View.VISIBLE


                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<SlotModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })

    }

    override fun slotPosition(position: Int) {

        list.add(position)
        if (list.size > 1) {
            val maxValue = list.maxBy { it }
            val minValue = list.minBy { it }
            adapter?.getHighestSlot(minValue!!, maxValue!!)
        }
    }

    override fun date(slotList: ArrayList<SlotDataModel>) {

        selectedSlotList = slotList
        if (selectedSlotList.isNotEmpty()) {
            binding.proceedToCheckoutBtn.background =
                ContextCompat.getDrawable(ctx, R.drawable.proceed_to_check_selected)
        } else {
            binding.proceedToCheckoutBtn.background =
                ContextCompat.getDrawable(ctx, R.drawable.proceed_to_check_unselected)
        }

    }

    // Booking Create Api....
    private fun bookingCreateApi() {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.bookingCreateApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE,
            showType,
            date,
            mFromTime,
            mToTime,
            artistID,
            shared.getString(Constants.ADDRESS)
        )
            .enqueue(object : Callback<BookingCreateModel> {
                override fun onResponse(
                    call: Call<BookingCreateModel>,
                    response: Response<BookingCreateModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            // Display Popup Message when Api Successfully created booking....
                            bookingId = response.body()?.data?.address?.id.toString()

                            if (prefManager?.getString1(Constants.DataKey.CURRENCY) == "EUR") {
                                Constants.IDEAL_PAYMENT = true
                                val intent = Intent(ctx, PaymentActivity::class.java)
                                Constants.BOOKING_ID = bookingId
//                                intent.putExtra("bookingid", bookingId)
                                startActivity(intent)
                                requireActivity().finish()
                                list.clear()
                            } else {
                                val intent = Intent(ctx, SelectBank::class.java)
                                Constants.BOOKING_ID = bookingId
//                                intent.putExtra("bookingid", bookingId)
                                startActivity(intent)
                                requireActivity().finish()
                                list.clear()
                            }

                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorObject = jsonObject.getJSONObject(Constants.ERROR)
                                val errorMessage = errorObject.getString("message")
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BookingCreateModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun popupBookingConfirm() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.booking_confirm_layout, binding.bookSlotContainer,false)
        val windowBookingConfirm = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        windowBookingConfirm.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            windowBookingConfirm.elevation = 10f
        }
        windowBookingConfirm.isTouchable = false
        windowBookingConfirm.isOutsideTouchable = false

        val confirmTv: MaterialTextView = popUp.findViewById(R.id.confirmTv)
        val cancelTv: MaterialTextView = popUp.findViewById(R.id.cancelTv)

        confirmTv.setOnClickListener {
            // calling Booking Create Api....
            windowBookingConfirm.dismiss()
            if (shared.getString(Constants.DataKey.USER_ID) == artistID) {

                Toast.makeText(ctx, "" + Constants.DataKey.USER_ID, Toast.LENGTH_LONG).show()
            } else {
                val list: ArrayList<SlotDataModel> = ArrayList()

                for (i in 0 until selectedSlotList.size) {

                    if (selectedSlotList[i].isBooked) {
                        list.add(selectedSlotList[i])
                        list[0].hour

                        val fromTime = list[0].hour
                        val toTime = list[list.size - 1].hour
                        val sdfParse1 = SimpleDateFormat("HH:mm")
                        val sdf1 = SimpleDateFormat("hh:mm a")

                        mFromTime = fromTime.split(" - ")[0]   // gettimg fromTime from List
                        mToTime = toTime.split(" - ")[1]     // // gettimg toTime from List

                        val date = sdf1.parse(mFromTime)
                        val date1 = sdf1.parse(mToTime)
                        if (date != null && date1 !=null) {
                            mFromTime = sdfParse1.format(date)
                            mToTime = sdfParse1.format(date1)
                        }
                    }
                }
                if (selectedSlotList.isEmpty()) {
                    Utility.alertErrorMessage(ctx, ctx.resources.getString(R.string.please_select_any_slot))
                } else {
                    bookingCreateApi()
                }
            }
        }
        cancelTv.setOnClickListener {
            windowBookingConfirm.dismiss()
        }
    }

}