package com.surpriseme.user.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.surpriseme.user.R
import com.surpriseme.user.activity.payment.AddCardActivity
import com.surpriseme.user.data.model.CardGetModel
import com.surpriseme.user.data.model.DeleteCard
import com.surpriseme.user.data.model.PaymentModel
import com.surpriseme.user.databinding.ActivitySeleckBankBinding
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SeleckBank : AppCompatActivity(), CardAdapter.ChangeLocale {

    lateinit var binding: ActivitySeleckBankBinding
    var shared: PrefrenceShared? = null
    var bookingid=""
    var card_id=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seleck_bank)
        shared = PrefrenceShared(this)
        bookingid = intent.getStringExtra("bookingid")
        binding.cardrecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        binding.cardrecycler.addItemDecoration(itemDecorator)

        binding.addcardbtn.setOnClickListener {
            val intent=Intent(this,AddCardActivity::class.java)
            intent.putExtra("selectcard","selectcard")
            intent.putExtra("bookingid",intent.getStringExtra("bookingid"))
            startActivity(intent)
        }

        binding.paynowbtn.setOnClickListener {
            paynow(card_id)
        }
        cardget()

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
                                val adapter = CardAdapter(
                                    this@SeleckBank,
                                    response.body()!!.data.data,
                                    this@SeleckBank
                                )
                                binding.cardrecycler.adapter = adapter
                                adapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@SeleckBank,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SeleckBank,
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
                        this@SeleckBank,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun language(language: String) {
          this.card_id = language
    }

    override fun deletcard(id: String) {
        deletecard(id)
    }


    private fun paynow(id: String) {

        binding!!.loaderLayout.visibility = View.VISIBLE
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
                                this@SeleckBank,
                                "" + response.body()!!.data.message,
                                Toast.LENGTH_LONG
                            ).show()
                            val intent =
                                Intent(this@SeleckBank, BookingDetailFragment::class.java)
                            intent.putExtra("bookingId", bookingid)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)


                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@SeleckBank,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SeleckBank,
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
                        this@SeleckBank,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun deletecard(id: String) {

        binding!!.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.deletecard(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            id
        )
            .enqueue(object : Callback<DeleteCard> {
                override fun onResponse(
                    call: Call<DeleteCard>,
                    response: Response<DeleteCard>
                ) {
                    binding!!.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@SeleckBank,
                                "" + response.body()!!.data.message,
                                Toast.LENGTH_LONG
                            ).show()



                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    this@SeleckBank,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SeleckBank,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<DeleteCard>, t: Throwable) {
                    binding!!.loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@SeleckBank,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }



}