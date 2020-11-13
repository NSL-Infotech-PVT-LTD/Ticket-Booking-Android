package com.surpriseme.user.activity.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import butterknife.OnClick
import com.surpriseme.user.R
import com.surpriseme.user.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity(), View.OnClickListener {

    private var binding:ActivityPaymentBinding?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_payment)
        binding = DataBindingUtil.setContentView(this@PaymentActivity,R.layout.activity_payment)

        init()

    }

    private fun init() {
        // initialization of views....
        binding?.cardPaymentTxt?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cardPaymentTxt -> {
                val intent = Intent(this@PaymentActivity, AddCardActivity ::class.java)
                startActivity(intent)
            }

        }
    }


}