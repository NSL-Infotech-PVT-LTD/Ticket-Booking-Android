package com.surpriseme.user.fragments.paymentfragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentPaymentBinding
import com.surpriseme.user.fragments.bookingfragment.BookingFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentFragment : Fragment(), View.OnClickListener {

    private var binding:FragmentPaymentBinding?= null
    private var ctx:Context?=null
    private val screenTime: Long = 3000
    private var shared:PrefrenceShared?=null
    private var bookingID = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_payment,container,false)
        val view = binding?.root
        shared = PrefrenceShared(ctx!!)

        init()

        return view
    }

    private fun init() {
        binding?.checkoutBtn?.setOnClickListener(this)
        bookingID = arguments?.getString("bookingId")!!
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.checkoutBtn -> {
//                val fragment = PaymentWaitFragment()
//                val transaction = fragmentManager?.beginTransaction()
//                transaction?.replace(R.id.frameContainer,fragment)
//                transaction?.commit()
                paymentStatusApi()
            }

        }
    }

    private fun paymentStatusApi() {
        val status = "confirmed"
        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.bookingStatusApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,bookingID,status)
            .enqueue(object :Callback<BookingStatusModel> {
                override fun onResponse(
                    call: Call<BookingStatusModel>,
                    response: Response<BookingStatusModel>
                ) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {
                            Toast.makeText(ctx,"" + response.body()?.data?.message,Toast.LENGTH_SHORT).show()
                            val fragment = BookingFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.frameContainer,fragment)
                            transaction?.commit()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx,"" + errorMessage,Toast.LENGTH_SHORT).show()
                            }catch (e:JSONException){
                                Toast.makeText(ctx,"" + e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<BookingStatusModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(ctx,"" + t.message.toString(),Toast.LENGTH_SHORT).show()

                }

            })
    }


}