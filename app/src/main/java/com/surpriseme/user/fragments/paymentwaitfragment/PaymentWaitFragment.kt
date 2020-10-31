package com.surpriseme.user.fragments.paymentwaitfragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.surpriseme.user.R
import com.surpriseme.user.fragments.addcardfragment.AddCardFragment


class PaymentWaitFragment : Fragment() {

    private val screenTime: Long = 5000
    private var ctx: Context?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val view =  inflater.inflate(R.layout.fragment_payment_wait, container, false)

        init()

        return view
    }
    private fun init() {
        Handler().postDelayed({

            val fragment = AddCardFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameContainer,fragment)
            transaction?.commit()

        },screenTime)
    }

}