package com.surpriseme.user.activity.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import butterknife.OnClick
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

class PaymentActivity : AppCompatActivity(), View.OnClickListener {

    private var binding:ActivityPaymentBinding?=null
    private var shared:PrefrenceShared?=null
    var bookingId=""
    private var list:ArrayList<DataX> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_payment)
        binding = DataBindingUtil.setContentView(this@PaymentActivity,R.layout.activity_payment)

        bookingId = intent.getStringExtra("bookingid")
        init()
        binding?.idealPaymentTxt!!.setOnClickListener {
            val intent = Intent(this@PaymentActivity, IdealPayment ::class.java)
            intent.putExtra("bookingid",bookingId)
            startActivity(intent)

        }

    }

    private fun init() {
        shared= PrefrenceShared(this)
        cardget()
        // initialization of views....
        binding?.cardPaymentTxt?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cardPaymentTxt -> {
                if(list.isEmpty()){
                val intent = Intent(this@PaymentActivity, AddCardActivity ::class.java)
                    intent.putExtra("paycard","paycard")
                    intent.putExtra("bookingid",bookingId)
                startActivity(intent)
                }
                else{
                    val intent = Intent(this@PaymentActivity, SeleckBank ::class.java)
                    intent.putExtra("bookingid",bookingId)
                    startActivity(intent)
                }
            }

        }
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
                            if(response.body()!!.data.data.isNotEmpty()){
                                list= response.body()!!.data.data
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

}