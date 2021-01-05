package com.surpriseme.user.activity.payment

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import butterknife.OnClick
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.IdealPayment
import com.surpriseme.user.activity.SeleckBank
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.signup.RegisterModel
import com.surpriseme.user.data.model.CardGetModel
import com.surpriseme.user.data.model.DataX
import com.surpriseme.user.databinding.ActivityPaymentBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PaymentActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityPaymentBinding? = null
    private var shared: PrefrenceShared? = null
    private var bookingId = ""
    private var list: ArrayList<DataX> = ArrayList()
    private var backpress: MaterialTextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_payment)
        binding = DataBindingUtil.setContentView(this@PaymentActivity, R.layout.activity_payment)

        bookingId = intent.getStringExtra("bookingid")!!
        init()
        binding?.idealPaymentTxt!!.setOnClickListener {
            val intent = Intent(this@PaymentActivity, IdealPayment::class.java)
            intent.putExtra("bookingid", bookingId)
            startActivity(intent)
            finish()

        }

    }

    private fun init() {
        shared = PrefrenceShared(this)
        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        cardget()
        // initialization of views....
        binding?.cardPaymentTxt?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardPaymentTxt -> {
                if (list.isEmpty()) {

                    Constants.IS_BOOKING_DONE = true
                    Constants.BOOKING = false
                    Constants.NOTIFICATION = false
                    val intent = Intent(this@PaymentActivity, AddCardActivity::class.java)
                    intent.putExtra("paycard", "paycard")
                    intent.putExtra("bookingid", bookingId)
                    startActivity(intent)
                    finish()

                } else {
                    Constants.IS_BOOKING_DONE = true
                    Constants.BOOKING = false
                    Constants.NOTIFICATION = false
                    val intent = Intent(this@PaymentActivity, SeleckBank::class.java)
                    intent.putExtra("bookingid", bookingId)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.backpress -> {

                popupBookingCancel()
            }

        }
    }

    private fun popupBookingCancel() {

        val layoutInflater: LayoutInflater =
            this@PaymentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.booking_cancel_layout, null)
        val windowBookingCancel = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        windowBookingCancel.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            windowBookingCancel.elevation = 10f
        }
        windowBookingCancel.isTouchable = false
        windowBookingCancel.isOutsideTouchable = false

        val yes: TextView = popUp.findViewById(R.id.yes)
        val no: TextView = popUp.findViewById(R.id.no)

        yes.setOnClickListener {

            windowBookingCancel.dismiss()
            cancelBookingApi(bookingId)

        }

        no.setOnClickListener {
            windowBookingCancel.dismiss()
        }
    }

    // api to cancel booking....
    private fun cancelBookingApi(bookingID: String) {

        binding?.loaderLayout?.visibility = View.VISIBLE
        val status = "cancel"
        RetrofitClient.api.bookingCancelApi(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            bookingID,
            status
        )
            .enqueue(object : Callback<BookingCancelModel> {
                override fun onResponse(
                    call: Call<BookingCancelModel>,
                    response: Response<BookingCancelModel>
                ) {

                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Toast.makeText(
                                this@PaymentActivity,
                                "" + response.body()?.data?.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@PaymentActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@PaymentActivity,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }


                }

                override fun onFailure(call: Call<BookingCancelModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                }
            })


    }

    private fun cardget() {

        binding!!.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.cardlist(shared?.getString(Constants.DataKey.AUTH_VALUE)!!)
            .enqueue(object : Callback<CardGetModel> {
                override fun onResponse(
                    call: Call<CardGetModel>,
                    response: Response<CardGetModel>
                ) {
                    binding!!.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            if (response.body()!!.data.data.isNotEmpty()) {
                                list = response.body()!!.data.data
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@PaymentActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@PaymentActivity,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<CardGetModel>, t: Throwable) {
                    binding!!.loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@PaymentActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onBackPressed() {
        popupBookingCancel()
    }
}