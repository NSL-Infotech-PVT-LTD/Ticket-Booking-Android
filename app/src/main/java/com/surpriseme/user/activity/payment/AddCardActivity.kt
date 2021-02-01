package com.surpriseme.user.activity.payment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.textview.MaterialTextView
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.*
import com.surpriseme.user.R
import com.surpriseme.user.activity.SelectBank
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.data.model.CardAddModel
import com.surpriseme.user.data.model.PaymentModel
import com.surpriseme.user.databinding.ActivityAddCardBinding
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCardActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddCardBinding? = null
    private var cardHolderName = ""
    private var accountNumber = ""
    private var month = ""
    private var year = ""
    private var cvv = ""
    private var shared: PrefrenceShared? = null
    private var backpress: MaterialTextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_card)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)
        shared = PrefrenceShared(this)
        init()

    }
    private fun init() {
        // initialization of views....
        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        binding?.payNowBtn?.setOnClickListener(this)
        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(this@AddCardActivity)
        // Check is Card list.size > 0, then Add card will display on button else Pay now will display...
        if (Constants.cardList.isNotEmpty()) {
            binding?.payNowBtn?.text = getString(R.string.ADD_CARD_TITLE)
        }
        //validations....
        validations()
    }
    private fun validations() {
        binding?.accountNumberEdt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                accountNumber = s.toString()
                if (accountNumber.length == 16)
                    binding?.expiryEdt?.requestFocus()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding?.expiryEdt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                month = s.toString()
                if (month.length == 2)
                    binding?.yearEdt?.requestFocus()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding?.yearEdt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                year = s.toString()
                if (year.length == 4)
                    binding?.cvvEdt?.requestFocus()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding?.cvvEdt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                cvv = s.toString()
            }
        })
    }
    private fun validatecard() {
        if (binding!!.cardHolderEdt.text.toString().isEmpty()) {
            Utility.alertErrorMessage(this@AddCardActivity, getString(R.string.ENTER_ACCOUNT_HOLDER_NAME))
        } else if (binding!!.accountNumberEdt.text.toString().isEmpty()) {
            Utility.alertErrorMessage(this@AddCardActivity, getString(R.string.ENTER_CARD_NUMBER))
        } else if (binding!!.expiryEdt.text.toString().isEmpty()) {
            Utility.alertErrorMessage(this@AddCardActivity, getString(R.string.ENTER_CARD_EXPIRY_MONTH))
        } else if (binding!!.yearEdt.text.toString().isEmpty()) {
            Utility.alertErrorMessage(this@AddCardActivity, getString(R.string.ENTER_EXPIRY_YEAR))
        }else if (binding!!.cvvEdt.text.toString().isEmpty()) {
            Utility.alertErrorMessage(this@AddCardActivity, getString(R.string.ENTER_CVV))
        }  else {
            val expiryedt = binding!!.expiryEdt.text.toString()
            val yearedt = binding!!.yearEdt.text.toString()

            val year = yearedt.toInt()
            val month = expiryedt.toInt()
            val card = CardParams(binding!!.accountNumberEdt.text.toString(), month, year, binding!!.cvvEdt.text.toString(), binding!!.cardHolderEdt.text.toString())
            createToken(card)
        }
    }
    // api to add card...
    private fun createToken(card: CardParams) {
        binding!!.loaderLayout.visibility = View.VISIBLE
        val stripe = Stripe(
            applicationContext,
                Constants.PUBLIC_KEY)
//        pk_test_51HcYaaDVPC7KpoaUBqxarUUagXrI14GRCicyaZt8NztibJ4G9Y7KMtunrcWTg5PDm3PzcuBe1zkFFJiJRt1mXs8s009njabz8l
        stripe.createCardToken(card, null, null, object : ApiResultCallback<Token> {
            override fun onError(e: Exception) {
                binding!!.loaderLayout.visibility = View.GONE
            }
            override fun onSuccess(result: Token) {
                cardget(result.id)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {

                if (!Constants.IDEAL_PAYMENT) {
                    if (Constants.cardList.isEmpty()) {
                        popupBookingCancel()
                    } else {
                        finish()
                    }
                } else {
                    popupBookingCancel()
                }
            }
            R.id.payNowBtn -> {
                    validatecard()
            }

        }
    }

    private fun cardget(id: String) {
         binding!!.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.cardadd(shared?.getString(Constants.DataKey.AUTH_VALUE)!!, id)
            .enqueue(object : Callback<CardAddModel> {
                override fun onResponse(
                    call: Call<CardAddModel>,
                    response: Response<CardAddModel>
                ) {
                    binding!!.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            if (binding?.payNowBtn?.text == getString(R.string.pay_now)) {
                                paynow(response.body()!!.data.data.id)
                            } else {
                                val intent = Intent(this@AddCardActivity, SelectBank::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@AddCardActivity, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(this@AddCardActivity, "" + e.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<CardAddModel>, t: Throwable) {
                    Toast.makeText(
                        this@AddCardActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    private fun paynow(id: String) {
         binding!!.pleaseWaitLayout.visibility = View.VISIBLE
        RetrofitClient.api.paynow(shared?.getString(Constants.DataKey.AUTH_VALUE)!!, id, Constants.BOOKING_ID, "confirmed", "card")
            .enqueue(object : Callback<PaymentModel> {
                override fun onResponse(
                    call: Call<PaymentModel>,
                    response: Response<PaymentModel>
                ) {
                    binding!!.pleaseWaitLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddCardActivity, "" + response.body()!!.data.message, Toast.LENGTH_LONG).show()
                            Constants.IS_BOOKING_DONE = true
                            Constants.BOOKING = false
                            Constants.NOTIFICATION = false
                            binding?.paymentDoneLayout?.visibility = View.VISIBLE
                            Constants.IS_CARD_SELECTED = false
                            binding?.paymentDoneLayout?.postDelayed({
                                Constants.cardList.size
                                val intent = Intent(this@AddCardActivity, BookingDetailFragment::class.java)
                                startActivity(intent)
                                finish()
                                binding?.paymentDoneLayout?.visibility = View.GONE
                            },3000)

                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(this@AddCardActivity, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(this@AddCardActivity, "" + Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                    binding!!.pleaseWaitLayout.visibility = View.GONE
                    Toast.makeText(this@AddCardActivity, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }
    // Displaying The Popup while cancel the booking.....
    private fun popupBookingCancel() {
        val layoutInflater: LayoutInflater = this@AddCardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View = layoutInflater.inflate(R.layout.booking_cancel_layout, binding?.addCardContainer,false)
        val windowBookingCancel = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true)
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
            cancelBookingApi(Constants.BOOKING_ID)
        }
        no.setOnClickListener {
            windowBookingCancel.dismiss()
        }
    }
    // api to cancel booking....
    private fun cancelBookingApi(bookingID: String) {
        binding?.loaderLayout?.visibility = View.VISIBLE
        val status = "cancel"
        RetrofitClient.api.bookingCancelApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!, bookingID, status)
            .enqueue(object : Callback<BookingCancelModel> {
                override fun onResponse(
                    call: Call<BookingCancelModel>,
                    response: Response<BookingCancelModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Toast.makeText(this@AddCardActivity, "" + response.body()?.data?.message.toString(), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AddCardActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(this@AddCardActivity, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(this@AddCardActivity, "" + e.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BookingCancelModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(this@AddCardActivity, "" + t.message.toString(),Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        if (!Constants.IDEAL_PAYMENT) {
            if (Constants.cardList.isEmpty()) {
                popupBookingCancel()
            }
        } else {
            popupBookingCancel()
        }
    }

}

