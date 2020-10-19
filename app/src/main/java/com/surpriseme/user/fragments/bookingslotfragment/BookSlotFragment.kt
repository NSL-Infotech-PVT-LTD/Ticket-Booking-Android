package com.surpriseme.user.fragments.bookingslotfragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentBookSlotBinding
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookSlotFragment : Fragment(), View.OnClickListener, BookSlotAdapter.SelectDate,
    BookSlotAdapter.BookSlot {

    private lateinit var binding: FragmentBookSlotBinding
    private lateinit var ctx: Context
    private var date = ""
    private lateinit var shared: PrefrenceShared
    private var mFromTime = ""
    private var mToTime = ""
    private var artistID = ""
    private var adapter: BookSlotAdapter? = null
    private val list = ArrayList<Int>()

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

        init()


        return view
    }

    private fun init() {

        artistID = shared.getString(Constants.ARTIST_ID)

        binding.proceedToCheckoutBtn.setOnClickListener(this)
        binding.clearAllBtn.setOnClickListener(this)

        if (arguments?.getString("selectedDate") != null) {
            date = arguments?.getString("selectedDate")!!
            binding.dateDispText.text = date
        }
        initializeRecycler()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.proceedToCheckoutBtn -> {

                // calling Booking Create Api....
                if (shared.getString(Constants.DataKey.USER_ID) == artistID){

                    Toast.makeText(ctx, "" + Constants.DataKey.USER_ID,Toast.LENGTH_LONG).show()
                }else {
                    bookingCreateApi()
                }


            }
            R.id.clearAllBtn -> {

                adapter?.settSlotClear(true)
                list.clear()

            }
        }
    }

    private fun initializeRecycler() {

        val layoutManager = GridLayoutManager(ctx, 2)
        binding.timeSlotRecycler.layoutManager = layoutManager
        val timeArray = SelectTimeModel()
        adapter = BookSlotAdapter(ctx, timeArray.aarayTime, this@BookSlotFragment, this)
        binding.timeSlotRecycler.adapter = adapter
    }

    // Booking Create Api....
    private fun bookingCreateApi() {
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.bookingCreateApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE,
            Constants.BOOKING_TYPE,
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
                            alertPopUp(response.body()?.data?.message!!)
                            list.clear()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
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

    override fun date(fromTime: String, toTime: String) {
        mFromTime = fromTime
        mToTime = toTime
    }

    private fun alertPopUp(message: String) {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.booking_done_popup, null)
        val popUpWindowReport = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindowReport.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popUpWindowReport.elevation = 10f
        }
        popUpWindowReport.isTouchable = false
        popUpWindowReport.isOutsideTouchable = false

        val messageText: MaterialTextView = popUp.findViewById(R.id.messageDispText)
        messageText.text = message
        val done: MaterialTextView = popUp.findViewById(R.id.doneTv)

        done.setOnClickListener {
            popUpWindowReport.dismiss()
            val intent = Intent(ctx, MainActivity::class.java)
            startActivity(intent)

        }
    }

    override fun slotPosition(position: Int) {

        list.add(position)
        if (list.size > 1) {
            val maxValue = list.maxBy { it }
            val minValue = list.minBy { it }
            adapter?.getHighestSlot(minValue!!,maxValue!!)
        }
    }


}