package com.surpriseme.user.fragments.bookingdetailfragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.FragmentBookingDetailBinding
import com.surpriseme.user.fragments.paymentfragment.PaymentFragment
import com.surpriseme.user.fragments.bookingfragment.BookingFragment
import com.surpriseme.user.fragments.chatFragment.ChatFragment
import com.surpriseme.user.fragments.notificationfragment.NotificationFragment
import com.surpriseme.user.fragments.paymentfragment.BookingStatusModel
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import kotlinx.android.synthetic.main.fragment_booking_detail.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookingDetailFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentBookingDetailBinding
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var fromTime = ""
    private var toTime = ""
    private var bookingId = ""
    private var artistId = ""
    private var bookingModel: BookingDataModel? = null
    private var spinnerValue = ""
    private var reasonList: ArrayList<String> = ArrayList()
    var descriptionTv: EditText? = null

    // boolean vars to take action according to status....
    private var wantToCancelBooking = false
    private var isArtistReach = false
    private var messageToDisplay = ""



    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_booking_detail, container, false)
        val view = binding.root
        activity!!.window.statusBarColor = ContextCompat.getColor(activity!!, R.color.colorPrimary)
        shared = PrefrenceShared(ctx)
        init(view)



        return view
    }

    private fun init(view: View) {

        // Hide Bottom navigation view....
        ((ctx as MainActivity)).hideBottomNavigation()

        // Views initialization....
        binding.backpress.setOnClickListener(this)
        binding.chatBtn.setOnClickListener(this)
        binding.actionBtn.setOnClickListener(this)
        bookingId = arguments?.getString("bookingId")!!

        bookingDetailApi()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                if (Constants.NOTIFICATION) {
                    Constants.COMING_FROM_DETAIL = false
//                    val fragment = NotificationFragment()
//                    val transaction = fragmentManager?.beginTransaction()
//                    transaction?.replace(R.id.frameContainer, fragment)
//                    transaction?.commit()
                    fragmentManager?.popBackStack()
                } else {
                    Constants.COMING_FROM_DETAIL = true
                    fragmentManager?.popBackStack()
//                    val fragment = BookingFragment()
//                    val transaction = fragmentManager?.beginTransaction()
//                    transaction?.replace(R.id.frameContainer, fragment)
//                    transaction?.commit()
                }
            }
            R.id.chatBtn -> {
                Constants.CHAT_ID = artistId
                val bundle = Bundle()
                val fragment = ChatFragment()
                bundle.putString("chatId", artistId)
                fragment.arguments = bundle
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameContainer, fragment)
                transaction?.addToBackStack("bookingDetailFragment")
                transaction?.commit()
//
            }
            R.id.actionBtn -> {
//                // If status button text is Pay Now then redirect to payment screen.

                if (actionBtn.text == ctx.resources.getString(R.string.cancel_booking)) {

                    // display popup , Are you sure want to cancel booking....
                    if (wantToCancelBooking)
                        messageToDisplay =
                            ctx.resources.getString(R.string.are_you_sure_want_to_cancel)
                    statusPopUp(messageToDisplay)
                    // If Booing status cancel....

                } else if (actionBtn.text == ctx.resources.getString(R.string.pay_now)) {
                    val bundle = Bundle()
                    bundle.putString("bookingId", bookingId)
                    val fragment = PaymentFragment()
                    fragment.arguments = bundle
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameContainer, fragment)
                    transaction?.addToBackStack("paymentFragment")
                    transaction?.commit()
                } else if (actionBtn.text == ctx.resources.getString(R.string.did_artist_reach_at_yout_location)) {
                    // display pop up..

                    if (isArtistReach)
                        messageToDisplay = ctx.resources.getString(R.string.are_yout_sure)
                    statusPopUp(messageToDisplay)
                } else if (actionBtn.text == ctx.resources.getString(R.string.go_to_Home)) {
                    Constants.COMING_FROM_DETAIL = true
                    fragmentManager?.popBackStack()
                }
            }
        }


    }

    private fun bookingDetailApi() {

        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.bookingDetailApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, bookingId
        )
            .enqueue(object : Callback<BookingDetailModel> {
                override fun onResponse(
                    call: Call<BookingDetailModel>,
                    response: Response<BookingDetailModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            bookingModel = response.body()?.data
                            artistId = bookingModel?.artist_detail?.id.toString()

                            if (bookingModel != null) {
                                binding.cardLayout.visibility = View.VISIBLE
                                Constants.OTP = bookingModel?.otp!!
                                displayDetail(bookingModel!!)
                            } else {

                                binding.cardLayout.visibility = View.GONE
                                Snackbar.make(binding.bookingDetailContainer, "" + Constants.SOMETHING_WENT_WRONG,BaseTransientBottomBar.LENGTH_INDEFINITE).show()
                                if (Constants.NOTIFICATION) {
                                    val fragment = NotificationFragment()
                                    val transaction = fragmentManager?.beginTransaction()
                                    transaction?.replace(R.id.frameContainer, fragment)
                                    transaction?.commit()
                                } else {
                                    val fragment = BookingFragment()
                                    val transaction = fragmentManager?.beginTransaction()
                                    transaction?.replace(R.id.frameContainer, fragment)
                                    transaction?.commit()
                                }
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    ctx,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<BookingDetailModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }


            })
    }

//    private fun addChip(pItem: String, pChipGroup: ChipGroup) {
//        val lChip = Chip(context)
//        lChip.text = pItem
//        lChip.isClickable = false
//        lChip.textSize = 13F
//// lChip.chipStrokeColor = resources.getColorStateList(R.color.colorAccent)
//// lChip.chipStrokeWidth = 1F
//        lChip.setEnsureMinTouchTargetSize(false)
//        lChip.setTextColor(ContextCompat.getColor(ctx, R.color.white))
//        lChip.chipBackgroundColor = ContextCompat.getColorStateList(ctx, R.color.grey_color)
//// following lines are for the demo
//        pChipGroup.addView(lChip as View)
//
//    }

    private fun displayDetail(bookingModel: BookingDataModel) {

        if (bookingModel != null) {
            if (!(bookingModel.id == 0 && bookingModel.address == null && bookingModel.artist_detail == null && bookingModel.date == null)) {

                if (bookingModel.artist_detail != null) {


                    Picasso.get()
                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + bookingModel.artist_detail.image)
                        .resize(4000, 1500)
                        .onlyScaleDown()
                        .placeholder(R.drawable.user_pholder)
                        .into(binding.profileImg)

                    binding.profileName.text = bookingModel.artist_detail.name
                }

                binding.statusTv.text = "${bookingModel.status}"
                if (bookingModel.status == Constants.PENDING)
                    wantToCancelBooking = true
                if (bookingModel.status == Constants.CONFIRMED)
                    isArtistReach = true
                binding.liveDigitalTv.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                binding.liveDigitalTv.text = "${bookingModel?.type}"

                //change satatus of button...
                if (bookingModel.status == Constants.PENDING) {
                    binding.chatBtn.visibility = View.VISIBLE
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_orange_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_grey_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.pending)
                    binding.paymentTv.text = ctx.resources.getString(R.string.not_paid)
                    binding.actionBtn.text = ctx.resources.getString(R.string.cancel_booking)
                } else if (bookingModel.status == Constants.CANCEL) {
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_red_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_grey_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.cancel)
                    binding.paymentTv.text = ctx.resources.getString(R.string.not_paid)
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text = ctx.resources.getString(R.string.go_to_Home)

                } else if (bookingModel.status == Constants.ACCEPTED) {
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_grey_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.accepted)
                    binding.paymentTv.text = ctx.resources.getString(R.string.not_paid)
                    binding.chatBtn.visibility = View.VISIBLE
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text = ctx.resources.getString(R.string.pay_now)
                } else if (bookingModel.status == Constants.REJECTED) {
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_red_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_grey_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.rejected)
                    binding.paymentTv.text = ctx.resources.getString(R.string.not_paid)
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text = ctx.resources.getString(R.string.go_to_Home)

                } else if (bookingModel.status == Constants.CONFIRMED) {
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.confirm)
                    binding.paymentTv.text = ctx.resources.getString(R.string.paid)
                    binding.chatBtn.visibility = View.VISIBLE
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text =
                        ctx.resources.getString(R.string.did_artist_reach_at_yout_location)
                } else if (bookingModel.status == Constants.PROCESSING) {
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_orange_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.processing)
                    binding.paymentTv.text = ctx.resources.getString(R.string.paid)
                    binding.chatBtn.visibility = View.GONE
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text =
                        ctx.resources.getString(R.string.your_artist_start_to_perform)
                } else if (bookingModel.status == Constants.COMPLETE_REVIEW) {
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.complete)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.paymentTv.text = ctx.resources.getString(R.string.paid)
                    binding.chatBtn.visibility = View.GONE
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text =
                        ctx.resources.getString(R.string.your_artist_complete_his_performance)
                } else if (bookingModel.status == Constants.REPORT) {
                    binding.statusTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_red_bg)
                    binding.paymentTv.background =
                        ContextCompat.getDrawable(ctx, R.drawable.status_green_bg)
                    binding.statusTv.text = ctx.resources.getString(R.string.report)
                    binding.paymentTv.text = ctx.resources.getString(R.string.paid)
                    binding.reasonTv.visibility = View.VISIBLE
                    if (bookingModel.params != null)
                        binding.reasonTv.text = bookingModel.params.report
                }

                binding.locationTv.text = bookingModel.address

                // Display date at top of card....
                var date = bookingModel.date
                var spf = SimpleDateFormat("yyyy-MM-dd")
                val newDate: Date = spf.parse(date)
                spf = SimpleDateFormat("dd MMMM, yyyy")
                date = spf.format(newDate)

                binding.dateTxt.text = date

                // converting time to display in card....
                fromTime = bookingModel.from_time
                toTime = bookingModel.to_time
                val fromSdf = SimpleDateFormat("HH:mm")
                val fromConvert = fromSdf.parse(fromTime)
                val fromTime = fromSdf.format(fromConvert!!)

                val toSdf = SimpleDateFormat("HH:mm")
                val toConvert = toSdf.parse(toTime)
                val toTime = toSdf.format(toConvert!!)
                binding.timeTxt.text = "${fromTime} to ${toTime}"

            } else {
                Toast.makeText(ctx, Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                val fragment = NotificationFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameContainer, fragment)
                transaction?.commit()
            }
        }

    }

    // Booking Status Api when status cancel....
    private fun bookingStatusApi() {
        val status = "cancel"
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.bookingStatusApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            bookingId,
            status
        )
            .enqueue(object : Callback<BookingStatusModel> {
                override fun onResponse(
                    call: Call<BookingStatusModel>,
                    response: Response<BookingStatusModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                ctx,
                                "" + response.body()?.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val fragment = BookingFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.frameContainer, fragment)
                            transaction?.commit()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BookingStatusModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }

            })
    }


    private fun reportStatusApi(reason: String) {
        val report = "report"
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.bookingStatusApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            bookingId,
            report,
            reason
        )
            .enqueue(object : Callback<BookingStatusModel> {
                override fun onResponse(
                    call: Call<BookingStatusModel>,
                    response: Response<BookingStatusModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                ctx,
                                "" + response.body()?.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val fragment = BookingFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.frameContainer, fragment)
                            transaction?.commit()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(ctx, "" + errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: JSONException) {
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BookingStatusModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }

            })
    }

    // when artist reach then popup will display
    private fun statusPopUp(message: String) {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.artist_reach_popup, null)
        val popupWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            popupWindow.elevation = 10f
        }
        popupWindow.isTouchable = false
        popupWindow.isOutsideTouchable = false

        val no: MaterialTextView = popUp.findViewById(R.id.no)
        val yes: MaterialTextView = popUp.findViewById(R.id.yes)
        val messageTv: MaterialTextView = popUp.findViewById(R.id.messageTv)
        messageTv.text = message

        yes.setOnClickListener {
            // dispaly otp popup
            if (wantToCancelBooking) {
                bookingStatusApi()
                popupWindow.dismiss()
            }
            if (isArtistReach) {
                otpPopup()
            }
        }

        no.setOnClickListener {
            if (wantToCancelBooking) {
                popupWindow.dismiss()
            } else if (isArtistReach) {
                popupWindow.dismiss()
                reportPopup()
            }

        }
    }


    private fun otpPopup() {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.otp_popup_layout, null)
        val otpWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        otpWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            otpWindow.elevation = 10f
        }
        otpWindow.isTouchable = false
        otpWindow.isOutsideTouchable = false
        val messageDispTxt: MaterialTextView = popUp.findViewById(R.id.messageDispTxt)
        val cross: ImageView = popUp.findViewById(R.id.crossIcon)
        messageDispTxt.text =
            ctx.resources.getString(R.string.your_otp_is) + " " + Constants.OTP + " " + ctx.getString(
                R.string.share_yout_opt_with
            )
        cross.setOnClickListener {
            otpWindow.dismiss()
        }
    }

    private fun reportPopup() {
        var reportSpinner: Spinner? = null

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.report_popup_layout, null)
        val reportPopupWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        reportPopupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            reportPopupWindow.elevation = 10f
        }
        reportPopupWindow.isTouchable = false
        reportPopupWindow.isOutsideTouchable = false
        val cross: ImageView = popUp.findViewById(R.id.crossIcon)
        val submit: MaterialButton = popUp.findViewById(R.id.submitBtn)
        descriptionTv = popUp.findViewById(R.id.descriptionTxt)
        reportSpinner = popUp.findViewById(R.id.reportSpinner)
        reportSpinner?.onItemSelectedListener = this

        reasonList.clear()
        reasonList.add(Constants.SELECT_REASON)
        reasonList.add(Constants.ARTIST_DENIED_DUTY)
        reasonList.add(Constants.ARTIST_IS_UNREACHABLE)
        reasonList.add(Constants.ARTIST_NOT_PICKING_CALL)
        reasonList.add(Constants.OTHER)

        val reasonAdapter = ReasonSpinnerAdapter(ctx, reasonList)
        reportSpinner?.adapter = reasonAdapter


        cross.setOnClickListener {
            if (descriptionTv?.visibility == View.VISIBLE) {
                descriptionTv?.visibility = View.GONE
                spinnerValue = Constants.SELECT_REASON
            } else {
                reportPopupWindow.dismiss()
            }

        }
        submit.setOnClickListener {

            val description = descriptionTv?.text.toString()


            if (spinnerValue == Constants.SELECT_REASON) {
                Toast.makeText(ctx, "Please choose reason", Toast.LENGTH_SHORT).show()
            } else if (spinnerValue == Constants.OTHER) {
                if (description.isEmpty()) {
                    Toast.makeText(ctx, "Please write reason for other", Toast.LENGTH_SHORT).show()
                } else {
                    reportStatusApi(description)
                    reportPopupWindow.dismiss()
                }
            } else {
                reportStatusApi(spinnerValue)
                reportPopupWindow.dismiss()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        spinnerValue = parent?.getItemAtPosition(position).toString()
        if (spinnerValue == Constants.OTHER) {
            descriptionTv?.visibility = View.VISIBLE
        } else {
            descriptionTv?.visibility = View.GONE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}