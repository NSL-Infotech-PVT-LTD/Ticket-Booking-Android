package com.surpriseme.user.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.payment.AddCardActivity
import com.surpriseme.user.activity.payment.BookingCancelModel
import com.surpriseme.user.data.model.CardGetDetailModel
import com.surpriseme.user.data.model.CardGetModel
import com.surpriseme.user.data.model.DeleteCard
import com.surpriseme.user.data.model.PaymentModel
import com.surpriseme.user.databinding.ActivitySeleckBankBinding
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

class SelectBank : AppCompatActivity(), CardAdapter.ChangeLocale, View.OnClickListener {

    private var binding: ActivitySeleckBankBinding? = null
    private var shared: PrefrenceShared? = null
    private var cardId = ""
    private var cardList: ArrayList<CardGetDetailModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seleck_bank)
        shared = PrefrenceShared(this)
        binding?.selectBankContainer?.visibility = View.GONE

        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text = Utility.randomString(this@SelectBank)

//        bookingid = intent.getStringExtra("bookingid")!!
        binding?.cardrecycler?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        binding?.cardrecycler?.addItemDecoration(itemDecorator)

        //initializing onclick listener....
        binding?.backpress?.setOnClickListener(this@SelectBank)
        binding?.addcardbtn?.setOnClickListener(this@SelectBank)
        binding?.paynowbtn?.setOnClickListener(this@SelectBank)
        cardget()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                if (Constants.IDEAL_PAYMENT) {
                    finish()
                } else {
                    popupBookingCancel()
                    Constants.IDEAL_PAYMENT = false
                }

            }
            R.id.addcardbtn -> {
                val intent = Intent(this, AddCardActivity::class.java)
                startActivity(intent)
            }
            R.id.paynowbtn -> {
                if (binding?.paynowbtn?.text == getString(R.string.pay_now)) {
                    if (Constants.IS_CARD_SELECTED)
                        paynow(cardId)
                    else {
                        Utility.alertErrorMessage(this@SelectBank, getString(R.string.PLEASE_SELECT_ANY_CARD))
                    }
                } else {
                    val intent = Intent(this, AddCardActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun cardget() {

        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.cardlist(shared?.getString(Constants.DataKey.AUTH_VALUE)!!)
            .enqueue(object : Callback<CardGetModel> {
                override fun onResponse(
                    call: Call<CardGetModel>,
                    response: Response<CardGetModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            cardList.clear()
                            cardList = response.body()?.data?.data!!
                            Constants.cardList = cardList
                            if (cardList.isNotEmpty()) {

                                binding?.selectBankContainer?.visibility = View.VISIBLE
                                val adapter = CardAdapter(this@SelectBank, response.body()!!.data.data, this@SelectBank)
                                binding?.cardrecycler?.adapter = adapter
                                adapter.notifyDataSetChanged()
                                binding?.noCardAvailableTxt?.visibility = View.GONE
                                binding?.paynowbtn?.text = getString(R.string.pay_now)
                                binding?.cardRecyclerLayout?.visibility = View.VISIBLE
                            } else {
                                binding?.cardRecyclerLayout?.visibility = View.GONE
                                if (Constants.IS_CARD_DELETED){
                                    binding?.noCardAvailableTxt?.visibility = View.VISIBLE
                                    binding?.paynowbtn?.text = getString(R.string.addcardd)
                                }else {
                                    val intent = Intent(this@SelectBank, AddCardActivity::class.java)
                                    startActivity(intent)
                                    Constants.IS_CARD_DELETED = false
                                }

                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@SelectBank,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SelectBank,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<CardGetModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(
                        this@SelectBank,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun language(language: String) {
        this.cardId = language
    }

    override fun deletcard(id: String) {
        popupDeleteCard(id)
    }

    // popup will display to delete Card ...
    private fun popupDeleteCard(id: String) {
        val layoutInflater: LayoutInflater = this@SelectBank.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View = layoutInflater.inflate(R.layout.delete_card_popup, binding?.selectBankContainer,false)
        val popUpWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popUpWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popUpWindow.elevation = 10f
        }
        popUpWindow.isTouchable = false
        popUpWindow.isOutsideTouchable = false
        val yes: MaterialTextView = popUp.findViewById(R.id.yes)
        val cancel: MaterialTextView = popUp.findViewById(R.id.cancel)
        yes.setOnClickListener {

            Constants.IS_CARD_DELETED = true
            popUpWindow.dismiss()
            deletecard(id)
            cardget()
        }
        cancel.setOnClickListener {
            popUpWindow.dismiss()
        }
    }

    private fun paynow(id: String) {
        binding?.pleaseWaitLayout?.visibility = View.VISIBLE
        RetrofitClient.api.paynow(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            id,
            Constants.BOOKING_ID,
            "confirmed",
            "card"
        )
            .enqueue(object : Callback<PaymentModel> {
                override fun onResponse(
                    call: Call<PaymentModel>,
                    response: Response<PaymentModel>
                ) {
                    binding?.pleaseWaitLayout?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            Constants.IS_BOOKING_DONE = true
                            Constants.BOOKING = false
                            Constants.NOTIFICATION = false
                            binding?.paymentDoneLayout?.visibility = View.VISIBLE
                            binding?.paymentDoneLayout?.postDelayed({
                                Toast.makeText(
                                    this@SelectBank,
                                    "" + response.body()!!.data.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent =
                                    Intent(this@SelectBank, BookingDetailFragment::class.java)
//                                intent.putExtra("bookingId", bookingid)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                                binding?.paymentDoneLayout?.visibility = View.GONE
                            }, 3000)
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@SelectBank,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SelectBank,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                    binding?.pleaseWaitLayout?.visibility = View.GONE
                    Toast.makeText(
                        this@SelectBank,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun deletecard(id: String) {
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.deletecard(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            id
        )
            .enqueue(object : Callback<DeleteCard> {
                override fun onResponse(
                    call: Call<DeleteCard>,
                    response: Response<DeleteCard>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@SelectBank,
                                "" + response.body()!!.data.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@SelectBank,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SelectBank,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<DeleteCard>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(this@SelectBank, "" + t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun popupBookingCancel() {
        val layoutInflater: LayoutInflater =
            this@SelectBank.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View = layoutInflater.inflate(
            R.layout.booking_cancel_layout,
            binding?.selectBankContainer,
            false
        )
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
                                this@SelectBank,
                                "" + response.body()?.data?.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@SelectBank, MainActivity::class.java)
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
                                    this@SelectBank,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SelectBank,
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

    override fun onBackPressed() {
        if (Constants.IDEAL_PAYMENT) {
            finish()
        } else {
            popupBookingCancel()
            Constants.IDEAL_PAYMENT = false
        }
    }

}