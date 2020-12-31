package com.surpriseme.user.activity.payment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.FacebookException
import com.google.android.material.textview.MaterialTextView
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.*
import com.stripe.android.view.CardInputWidget
import com.stripe.android.view.CardMultilineWidget

//import com.stripe.android.TokenCallback
import com.surpriseme.user.R
import com.surpriseme.user.data.model.CardAddModel
import com.surpriseme.user.data.model.PaymentModel
import com.surpriseme.user.databinding.ActivityAddCardBinding
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import kotlinx.android.synthetic.main.activity_add_card.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class AddCardActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddCardBinding? = null
    private var cardHolderName = ""
    private var accountNumber = ""
    private var month = ""
    private var year = ""
    private var cvv = ""
    private var shared: PrefrenceShared? = null
    private var backpress: MaterialTextView? = null
    private var bookingid = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_card)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)
        shared = PrefrenceShared(this)

        bookingid = intent.getStringExtra("bookingid")!!

        if (intent.getStringExtra("paycard") == "paycard") {
            binding?.choosePaymentMethodTxt!!.text = getString(R.string.pay_now)
            binding?.payNowBtn!!.text = getString(R.string.pay_now)

        } else if (intent.getStringExtra("selectcard") == "selectcard") {
            binding?.choosePaymentMethodTxt?.text = getString(R.string.addcardd)
            binding?.payNowBtn?.text = getString(R.string.addcardd)

        }

        init()

    }

    private fun init() {
        // initialization of views....
        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString()

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


        binding?.payNowBtn!!.setOnClickListener {
            validatecard()
        }


    }


    private fun validatecard() {
        if (binding!!.cardHolderEdt.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.pleaseenteraccount), Toast.LENGTH_SHORT).show()
        } else if (binding!!.accountNumberEdt.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.pleaseenteraccountnum), Toast.LENGTH_SHORT)
                .show()
        } else if (binding!!.expiryEdt.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.pleaseentermonth), Toast.LENGTH_SHORT).show()
        } else if (binding!!.cvvEdt.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.pleaseentercvv), Toast.LENGTH_SHORT).show()
        } else if (binding!!.yearEdt.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.pleaseenteryear), Toast.LENGTH_SHORT).show()
        } else {


            val expiryedt = binding!!.expiryEdt.text.toString()
            val yearedt = binding!!.yearEdt.text.toString()

            val year = yearedt.toInt()
            val month = expiryedt.toInt()
            val card = CardParams(
                binding!!.accountNumberEdt.text.toString(),
                month,
                year,
                binding!!.cvvEdt.text.toString(),
                binding!!.cardHolderEdt.text.toString()

            )
            CreateToken(card)
        }

    }


    private fun CreateToken(card: CardParams) {

        binding!!.loaderLayout.visibility = View.VISIBLE
        val stripe = Stripe(
            applicationContext,
            "pk_test_51HcYaaDVPC7KpoaUBqxarUUagXrI14GRCicyaZt8NztibJ4G9Y7KMtunrcWTg5PDm3PzcuBe1zkFFJiJRt1mXs8s009njabz8l"
        )
        stripe.createCardToken(card, null, null, object : ApiResultCallback<Token> {
            override fun onError(e: Exception) {
            }


            override fun onSuccess(result: Token) {

                cardget(result.id)
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                val intent = Intent(this@AddCardActivity,PaymentActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }


    private fun cardget(id: String) {

        // binding!!.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.cardadd(shared?.getString(Constants.DataKey.AUTH_VALUE)!!, id)
            .enqueue(object : Callback<CardAddModel> {
                override fun onResponse(
                    call: Call<CardAddModel>,
                    response: Response<CardAddModel>
                ) {

                    if (intent.getStringExtra("selectcard") == "selectcard") {
                        binding!!.loaderLayout.visibility = View.GONE
                    }

                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            if (intent.getStringExtra("paycard") == "paycard") {
                                paynow(response.body()!!.data.data.id)
                            } else if (intent.getStringExtra("selectcard") == "selectcard") {
                                finish()
                            }

                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@AddCardActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@AddCardActivity,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<CardAddModel>, t: Throwable) {
                    binding!!.loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@AddCardActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun paynow(id: String) {

        // binding!!.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.paynow(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            id,
            bookingid,
            "confirmed",
            "card"
        )
            .enqueue(object : Callback<PaymentModel> {
                override fun onResponse(
                    call: Call<PaymentModel>,
                    response: Response<PaymentModel>
                ) {
                    binding!!.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@AddCardActivity,
                                "" + response.body()!!.data.message,
                                Toast.LENGTH_LONG
                            ).show()
                            Constants.IS_BOOKING_DONE = true
                            Constants.BOOKING = false
                            Constants.NOTIFICATION = false
                            val intent = Intent(this@AddCardActivity, BookingDetailFragment::class.java)
                            intent.putExtra("bookingId", bookingid)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()


                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@AddCardActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@AddCardActivity,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                    binding!!.loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@AddCardActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


}

