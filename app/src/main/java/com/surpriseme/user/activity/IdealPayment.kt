
package com.surpriseme.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.textview.MaterialTextView
import com.stripe.android.*
import com.stripe.android.model.*
import com.surpriseme.user.R
import com.surpriseme.user.data.model.PaymentIntent
import com.surpriseme.user.databinding.ActivityIdealPaymentBinding
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

class IdealPayment : AppCompatActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    lateinit var binding:ActivityIdealPaymentBinding

    var shared:PrefrenceShared?=null
    private var bankname=""
    var bankid =ArrayList<String>()
    var bankcode=""
    var stripe: Stripe?=null
    var bookingid=""
    private var backpress:MaterialTextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_ideal_payment)

        shared= PrefrenceShared(this)
//        bookingid = intent.getStringExtra("bookingid")!!

        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        binding.paynowbtn.setOnClickListener(this)

        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(this@IdealPayment)


        binding.holderedt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(binding.holderedt.text.isNotEmpty()){
                    binding.paynowbtn.backgroundTintList = getColorStateList(R.color.colorAccent)
                }else{
                    binding.paynowbtn.backgroundTintList = getColorStateList(R.color.btnback)
                }
            }

        })


        val banklist = ArrayList<String>()
        banklist.add("")
        banklist.add("ABN AMRO")
        banklist.add("ASN Bank")
        banklist.add("Bunq")
        banklist.add("Handlesbanken")
        banklist.add("ING")
        banklist.add("Knab")
        banklist.add("Moneyou")
        banklist.add("Rabobank")
        banklist.add("RegioBank")
        banklist.add("SNS Bank (De Volksbank)")
        banklist.add("Triodos Bank")
        banklist.add("Van Lanschot")

        bankid.add("")
        bankid.add("abn_amro")
        bankid.add("asn_bank")
        bankid.add("bunq")
        bankid.add("handelsbanken")
        bankid.add("ing")
        bankid.add("knab")
        bankid.add("moneyou")
        bankid.add("rabobank")
        bankid.add("regiobank")
        bankid.add("sns_bank")
        bankid.add("triodos_bank")
        bankid.add("van_lanschot")

        val bankadapter = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,banklist)
        binding.bookingSpinner.adapter = bankadapter
        binding.choosebankedt.hint=""
        binding.bookingSpinner.onItemSelectedListener =this
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                finish()
            }
            R.id.paynowbtn -> {
                if(binding.choosebankedt.text.toString().equals(getString(R.string.selectbank))){
                    Utility.alertErrorMessage(this@IdealPayment, getString(R.string.selectbank))
                }
                else if(binding.holderedt.text.toString().isEmpty()){
                    Utility.alertErrorMessage(this@IdealPayment, getString(R.string.ENTER_ACCOUNT_HOLDER_NAME))
                }
                else{
                    paynow(Constants.BOOKING_ID)
                }
            }
        }
    }

    fun pay(secret:String){

        val billingDetails = PaymentMethod.BillingDetails(name = binding.holderedt.text.toString())

        val ideal = PaymentMethodCreateParams.Ideal(bankcode)
        val paymentMethodCreateParams = PaymentMethodCreateParams.create(ideal, billingDetails)

        val confirmParams = ConfirmPaymentIntentParams
            .createWithPaymentMethodCreateParams(
                paymentMethodCreateParams  = paymentMethodCreateParams,
                clientSecret = secret,
                returnUrl = "surpriseme://stripe-redirect",
            )
//        confirmParams.paymentMethodCreateParams = paymentMethodCreateParams
        stripe= Stripe(applicationContext, PaymentConfiguration.getInstance(applicationContext).publishableKey)

        stripe!!.confirmPayment(this,confirmParams)

    }
    private fun paynow(id: String) {
        binding.pleaseWaitLayout.visibility = View.VISIBLE
        RetrofitClient.api.payintent(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            id
        )
            .enqueue(object : Callback<PaymentIntent> {
                override fun onResponse(
                    call: Call<PaymentIntent>,
                    response: Response<PaymentIntent>
                ) {
                    binding.pleaseWaitLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                         pay(response.body()!!.data.client_secret)
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@IdealPayment,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@IdealPayment,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<PaymentIntent>, t: Throwable) {
                    binding.pleaseWaitLayout.visibility = View.GONE
                    Toast.makeText(
                        this@IdealPayment,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle the result of stripe.confirmSetupIntent
        stripe!!.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                val setupIntent = result.intent
                val status = setupIntent.status
                when (status) {
                    StripeIntent.Status.Succeeded -> {
                        // Setup succeeded
                        Constants.IS_BOOKING_DONE = true
                        Constants.BOOKING = false
                        Constants.NOTIFICATION = false
                        binding.paymentDoneLayout.visibility = View.VISIBLE
                        binding.paymentDoneLayout.postDelayed({
                            Toast.makeText(this@IdealPayment,"Success",Toast.LENGTH_LONG).show()
                            val intent = Intent(this@IdealPayment,BookingDetailFragment::class.java)
//                            intent.putExtra("bookingId",bookingid)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                            binding.paymentDoneLayout.visibility = View.GONE
                        },3000)
                    } else -> {
                        Toast.makeText(this@IdealPayment,"Failure",Toast.LENGTH_LONG).show()

                        val intent = Intent(this@IdealPayment,BookingDetailFragment::class.java)
//                        intent.putExtra("bookingId",bookingid)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        // Setup failed/cancelled
                    }
                }
            }

            override fun onError(e: Exception) {
                // Setup failed
            }
        })
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position>0) {
            bankname = parent?.getItemAtPosition(position).toString()
            bankcode = bankid[position]
            Toast.makeText(this, "" + bankcode, Toast.LENGTH_LONG).show()
            binding.choosebankedt.text = bankname
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

}