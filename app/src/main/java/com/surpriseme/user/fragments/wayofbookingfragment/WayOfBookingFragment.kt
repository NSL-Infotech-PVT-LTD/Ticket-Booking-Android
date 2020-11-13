package com.surpriseme.user.fragments.wayofbookingfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentWayOfBookingBinding
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.fragments.selectdateofbookingfragment.SelectDateFragment
import com.surpriseme.user.util.Constants
import kotlinx.android.synthetic.main.fragment_map.*


class WayOfBookingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentWayOfBookingBinding
    private lateinit var ctx: Context
    private var bookingType = ""
    private var backpress:MaterialTextView?=null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_way_of_booking,
            container,
            false
        )
        val view = binding.root

        ((ctx as MainActivity)).hideBottomNavigation()

        init(view)

        return view
    }

    private fun init(view: View) {

        // initialization of views....
        backpress = view.findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        binding.liveBookNowBtn.setOnClickListener(this)
        binding.digitalBookingNowBtn.setOnClickListener(this)

        /*binding.radioGroup.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                val radioid  = group?.findViewById<RadioButton>(checkedId)
                var type = radioid?.text.toString()
                if (type == Constants.LIVE_SHOW_BOOKING )
                    type = Constants.LIVE
                else if (type == Constants.DIGITAL_SHOW_BOOKING)
                    type = Constants.DIGITAL
                Constants.BOOKING_TYPE = type
            }
        })*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.liveBookNowBtn -> {

                bookingType = "live"
                Constants.BOOKING_TYPE = bookingType

                if (Constants.BOOKING_TYPE =="") {
                    Toast.makeText(ctx,"" + ctx.resources.getString(R.string.please_choose_type_of_booking),Toast.LENGTH_SHORT).show()

                }else {
                    val fragment = SelectDateFragment()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameContainer,fragment)
                    transaction?.addToBackStack("wayOfBooking")
                    transaction?.commit()
                }

            }
            R.id.digitalBookingNowBtn -> {

                bookingType = "digital"
                Constants.BOOKING_TYPE = bookingType

                if (Constants.BOOKING_TYPE =="") {
                    Toast.makeText(ctx,"" + ctx.resources.getString(R.string.please_choose_type_of_booking),Toast.LENGTH_SHORT).show()

                }else {
                    val fragment = SelectDateFragment()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameContainer,fragment)
                    transaction?.addToBackStack("wayOfBooking")
                    transaction?.commit()
                }
            }
            R.id.backpress -> {
                fragmentManager?.popBackStack()
            }

        }

    }

}