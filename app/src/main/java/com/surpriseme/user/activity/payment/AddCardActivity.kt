package com.surpriseme.user.activity.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import butterknife.OnClick
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.ActivityAddCardBinding

class AddCardActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddCardBinding? = null
    private var cardHolderName = ""
    private var accountNumber = ""
    private var month = ""
    private var year = ""
    private var cvv = ""
    private var backpress: MaterialTextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_card)

        binding = DataBindingUtil.setContentView(this@AddCardActivity, R.layout.activity_add_card)

        init()

    }

    private fun init() {
        // initialization of views....
        backpress = findViewById(R.id.backpress)

        //validations....
        validations()

    }

    private fun validations() {

        binding?.cardHolderEdt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                cardHolderName = s.toString()
                Handler().postDelayed(Runnable {
                    binding?.accountNumberEdt?.requestFocus()
                }, 2000)
            }


        })

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

                cvv  = s.toString()

            }


        })


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                finish()
            }

        }
    }
}