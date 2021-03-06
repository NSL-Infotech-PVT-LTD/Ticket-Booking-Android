package com.surpriseme.user.fragments.bookingdetailfragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.activity.payment.BookingCancelModel
import com.surpriseme.user.activity.payment.PaymentActivity
import com.surpriseme.user.databinding.FragmentBookingDetailBinding
import com.surpriseme.user.fragments.chatFragment.ChatFragment
import com.surpriseme.user.fragments.paymentfragment.BookingStatusModel
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_booking_detail.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookingDetailFragment : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: FragmentBookingDetailBinding

    //private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var fromTime = ""
    private var toTime = ""
    private var bookingId = ""
    private var artistId = ""
    private var artistImage = ""
    private var artistName = ""
    private var bookingModel: BookingDataModel? = null
    private var spinnerValue = ""
    private var reasonList: ArrayList<String> = ArrayList()
    private var descriptionTv: EditText? = null
    private var submitReviewBtn: MaterialButton? = null


    // rate review bottom sheet....
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheet: View
    private var close: ImageView? = null
    private var rateReviewImg: CircleImageView? = null
    private var rateReviewName: TextView? = null
    private var reviewTv: TextView? = null
    private var rateReviewRatingbar: RatingBar? = null
    private var starImg: ImageView? = null
    private var rateReviewTxt: TextView? = null
    private var mShowType = ""

    // boolean vars to take action according to status....
    private var wantToCancelBooking = false
    private var messageToDisplay = ""

    // Rate Review Custom layout views....
    private var ratingbarForSmile: RatingBar? = null
    private var smileIcon: ImageView? = null
    private var laterTv: TextView? = null
    private var rateForSmile = "1"
    private var review = ""
    private var rateReviewEdt: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_payment)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_booking_detail)

        //activity!!.window.statusBarColor = ContextCompat.getColor(activity!!, R.color.colorPrimary)
        shared = PrefrenceShared(this)

        init()

    }

    private fun init() {

        val loadingText = findViewById<TextView>(R.id.loadingtext)
        loadingText.text = Utility.randomString(this@BookingDetailFragment)

        bottomSheet = findViewById(R.id.rateReviewBottomSheet)!!
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        close = findViewById(R.id.close)
        rateReviewImg = findViewById(R.id.rateReviewImg)
        rateReviewName = findViewById(R.id.rateReviewName)
        rateReviewRatingbar = findViewById(R.id.rateReviewRatingbar)
        reviewTv = findViewById(R.id.rateReviewTv)
        starImg = findViewById(R.id.starImg)
        rateReviewTxt = findViewById(R.id.rateReviewTxt)
        ratingbarForSmile = findViewById(R.id.ratingbarForSmile)
        smileIcon = findViewById(R.id.smileIcon)
        laterTv = findViewById(R.id.laterTv)
        laterTv?.setOnClickListener(this)
        submitReviewBtn = findViewById(R.id.submitReviewBtn)
        submitReviewBtn?.setOnClickListener(this)
        rateReviewEdt = findViewById(R.id.rateReviewEdt)
        binding.profileImg.setOnClickListener(this)
        binding.cancelTv.setOnClickListener(this)

        reasonList.clear()
        reasonList.add(Constants.SELECT_REASON)
        reasonList.add(Constants.ARTIST_DENIED_DUTY)
        reasonList.add(Constants.ARTIST_IS_UNREACHABLE)
        reasonList.add(Constants.ARTIST_NOT_PICKING_CALL)
        reasonList.add(Constants.OTHER)


        // get rating to change smile while give rate your experience...


        ratingbarForSmile?.onRatingBarChangeListener =
            object : RatingBar.OnRatingBarChangeListener {
                override fun onRatingChanged(
                    ratingBar: RatingBar?,
                    rating: Float,
                    fromUser: Boolean
                ) {
                    rateForSmile = rating.toString()

                    if (rateForSmile == "1.0") {
                        smileIcon?.setImageResource(R.drawable.smile_one)
                        rateForSmile = "1"
                    } else if (rateForSmile == "2.0") {
                        smileIcon?.setImageResource(R.drawable.smile_two)
                        rateForSmile = "2"
                    } else if (rateForSmile == "3.0") {
                        smileIcon?.setImageResource(R.drawable.smile_three)
                        rateForSmile = "3"
                    } else if (rateForSmile == "4.0") {
                        smileIcon?.setImageResource(R.drawable.smile_fourth)
                        rateForSmile = "4"
                    } else if (rateForSmile == "5.0") {
                        smileIcon?.setImageResource(R.drawable.smile_fourth)
                        rateForSmile = "5"
                    }
                }

            }

        close?.setOnClickListener(this)

        // Views initialization....
        binding.backpress.setOnClickListener(this)
        binding.actionBtn.setOnClickListener(this)
        binding.chatTv.setOnClickListener(this)
        binding.readMoreTv.setOnClickListener(this)
        binding.reasonReadMoreTv.setOnClickListener(this)

//        bookingId = intent.getStringExtra("bookingId")!!

        //Search bottom sheet callback....
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        bookingDetailApi()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backpress -> {
                if (Constants.NOTIFICATION) {
                    Constants.COMING_FROM_DETAIL = false
                    finish()
                } else {
                    Constants.COMING_FROM_DETAIL = true
                    finish()
                }
                if (Constants.IS_BOOKING_DONE) {
                    Constants.BOOKING = false
                    Constants.NOTIFICATION = false
                    val intent = Intent(this@BookingDetailFragment, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
            R.id.chatTv -> {
                Constants.CHAT_ID = artistId
                val intent = Intent(this, ChatFragment::class.java)
                intent.putExtra("chatId", artistId)
                intent.putExtra("receiverImage", artistId)
                intent.putExtra("receiverName", artistId)
                startActivity(intent)
            }
            R.id.actionBtn -> {
//                // If status button text is Pay Now then redirect to payment screen.

                if (actionBtn.text == resources.getString(R.string.cancel_booking)) {

                    // display popup , Are you sure want to cancel booking....
                    if (wantToCancelBooking)
                        messageToDisplay =
                            resources.getString(R.string.are_you_sure_want_to_cancel)
                    statusPopUp()
                    // If Booing status cancel....

                } else if (actionBtn.text == resources.getString(R.string.pay_now)) {
                    val intent = Intent(this, PaymentActivity::class.java)
//                    intent.putExtra("bookingid", bookingId)
                    startActivity(intent)
                } else if (actionBtn.text == resources.getString(R.string.did_artist_reach_at_yout_location)) {
                    // display pop up..

//                    if (isArtistReach)

                    statusPopUp()
                } else if (actionBtn.text == resources.getString(R.string.go_to_Home)) {
                    Constants.COMING_FROM_DETAIL = true
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else if (actionBtn.text == resources.getString(R.string.rate_your_artist)) {
                    binding.rateReviewCustomLayout.visibility = View.VISIBLE
                }
            }
            R.id.readMoreTv -> {
                bottomSheetUpDownRateReview()

                // display values to the rate and review bottom sheet....
                if (bookingModel?.artist_detail != null) {

                    Picasso.get()
                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + bookingModel?.artist_detail?.image)
                        .placeholder(R.drawable.user_pholder_updated)
                        .into(rateReviewImg)
                    rateReviewName?.text = bookingModel?.artist_detail?.name
                }
                if (bookingModel?.rate_detail != null) {
                    if (bookingModel?.rate_detail?.review != null) {
                        reviewTv?.text = bookingModel?.rate_detail?.review
                    }
                    if (bookingModel?.rate_detail?.rate != "") {
                        rateReviewRatingbar?.rating = bookingModel?.rate_detail?.rate?.toFloat()!!
                    }
                }
            }
            R.id.reasonReadMoreTv -> {

                starImg?.setImageResource(R.drawable.error)
                rateReviewTxt?.text = resources.getString(R.string.Report)
                if (bookingModel?.artist_detail != null) {

                    Picasso.get()
                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + bookingModel?.artist_detail?.image)
                        .placeholder(R.drawable.user_pholder_updated)
                        .into(rateReviewImg)
                    rateReviewName?.text = bookingModel?.artist_detail?.name
                }
                if (bookingModel?.params != null) {
                    reviewTv?.text = bookingModel?.params?.report
                }
                rateReviewRatingbar?.visibility = View.GONE
                bottomSheetUpDownRateReview()

            }
            R.id.close -> {
                bottomSheetUpDownRateReview()
            }
            R.id.laterTv -> {
                binding.rateReviewCustomLayout.visibility = View.GONE
            }
                R.id.submitReviewBtn -> {

                review = rateReviewEdt?.text.toString()

                if (review == "") {
                    Utility.alertErrorMessage(
                        this@BookingDetailFragment,
                        getString(R.string.please_give_rating)
                    )
                } else {

                    rateReviewBookingApi(Constants.BOOKING_ID, rateForSmile, review)
                }
            }
            R.id.profileImg -> {
                Constants.IS_COMING_FROM_BOOKING_DETAIL = true
                Constants.IS_CHAT_SESSION = false
                Constants.IS_SEARCH_ACTIVITY = false
                val intent = Intent(this@BookingDetailFragment, MainActivity::class.java)
                intent.putExtra("artistID", artistId)
                startActivity(intent)

            }
            R.id.cancelTv -> {
                popupBookingCancel()
            }
        }

    }

    private fun popupBookingCancel() {

        val layoutInflater: LayoutInflater =
            this@BookingDetailFragment.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(
            R.layout.booking_cancel_layout,
            binding.bookingDetailContainer,
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

        binding.loaderLayout.visibility = View.VISIBLE
        val status = "cancel"
        RetrofitClient.api.bookingCancelApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.BOOKING_ID,
            status
        )
            .enqueue(object : Callback<BookingCancelModel> {
                override fun onResponse(
                    call: Call<BookingCancelModel>,
                    response: Response<BookingCancelModel>
                ) {

                    binding.loaderLayout.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Toast.makeText(
                                this@BookingDetailFragment,
                                "" + response.body()?.data?.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            bookingDetailApi()
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@BookingDetailFragment,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@BookingDetailFragment,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }


                }

                override fun onFailure(call: Call<BookingCancelModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                }
            })


    }

    // Search Bottom Sheet Method....
    private fun bottomSheetUpDownRateReview() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }

    private fun bookingDetailApi() {

        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.bookingDetailApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE,
            Constants.BOOKING_ID
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
                            artistName = bookingModel?.artist_detail?.name.toString()
                            if (bookingModel?.artist_detail?.image != null) {
                                artistImage = bookingModel?.artist_detail?.image.toString()
                            }

                            if (bookingModel != null) {
                                binding.cardLayout.visibility = View.VISIBLE
                                Constants.OTP = bookingModel?.otp!!
                                displayDetail(bookingModel!!)
                            } else {

                                binding.cardLayout.visibility = View.GONE
                                Snackbar.make(
                                    binding.bookingDetailContainer,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    BaseTransientBottomBar.LENGTH_INDEFINITE
                                ).show()
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@BookingDetailFragment,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@BookingDetailFragment,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<BookingDetailModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@BookingDetailFragment,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun displayDetail(bookingModel: BookingDataModel?) {

        if (bookingModel != null) {
            if (!(bookingModel.id == 0 && bookingModel.address == null && bookingModel.artist_detail == null && bookingModel.date == null)) {
                if (bookingModel.artist_detail != null) {
                    Picasso.get()
                        .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + bookingModel.artist_detail.image)
                        .placeholder(R.drawable.user_pholder)
                        .into(binding.profileImg)
                    binding.profileName.text = bookingModel.artist_detail.name
                }

                binding.statusTv.text = "${bookingModel.status}"
                mShowType = bookingModel.type
                if (mShowType == "digital") {
                    binding.virtualTv.visibility = View.VISIBLE
                } else {
                    binding.liveDigitalTv.visibility = View.VISIBLE
                }

                //change satatus of button...
                if (bookingModel.status == Constants.CANCEL) {
                    binding.statusTv.text = resources.getString(R.string.cancel)
                    binding.paymentTv.text = resources.getString(R.string.not_paid)
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text = resources.getString(R.string.go_to_Home)
                    binding.cancelTv.visibility = View.GONE

                } else if (bookingModel.status == Constants.CONFIRMED) {
                    binding.statusTv.text = resources.getString(R.string.Confirmed)
                    binding.paymentTv.text =
                        getString(R.string.paid) + bookingModel.customer_currency + " " + bookingModel.price
//                    binding.paymentTv.text = getString(R.string.paid, bookingModel.artist_currency,bookingModel.price)
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text =
                        resources.getString(R.string.did_artist_reach_at_yout_location)

                } else if (bookingModel.status == Constants.PROCESSING) {
                    binding.statusTv.text = resources.getString(R.string.Processing)
                    binding.paymentTv.text =
                        resources.getString(R.string.paid) + bookingModel.customer_currency + " " + bookingModel.price
                    binding.actionBtn.isEnabled = false
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text =
                        resources.getString(R.string.your_artist_start_to_perform)

                } else if (bookingModel.status == Constants.COMPLETE_REVIEW) {
                    binding.statusTv.text = resources.getString(R.string.completed_review)
                    binding.paymentTv.text = getString(R.string.paid) + bookingModel.customer_currency + " " + bookingModel.price

                    binding.rateReviewLayout.visibility = View.VISIBLE
                    if (bookingModel.rate_detail != null) {
                        if (bookingModel.rate_detail.rate != "") {
                            binding.rateTv.text = bookingModel.rate_detail.rate
                        }
                        if (bookingModel.rate_detail.review != "") {
                            binding.reviewTv.text = bookingModel.rate_detail.review
                        }
                        if (bookingModel.rate_detail.rate.isNotEmpty()) {
                            try {
                                binding.ratingbar.rating = bookingModel.rate_detail.rate.toFloat()
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                            }
                        }

                    }
                } else if (bookingModel.status == Constants.REPORT) {
                    binding.statusTv.text = resources.getString(R.string.report)
                    binding.paymentTv.text =
                        resources.getString(R.string.paid) + bookingModel.customer_currency + " " + bookingModel.price
                    binding.reasonTv.visibility = View.VISIBLE
                    if (bookingModel.params != null)
                        binding.reasonLayout.visibility = View.VISIBLE
                    binding.reasonTv.text =
                        resources.getString(R.string.reason) + " " + bookingModel.params.report
                    if (binding.reasonTv.text.length <= 15) {
                        binding.reasonReadMoreTv.visibility = View.GONE
                    } else {
                        binding.reasonReadMoreTv.visibility = View.VISIBLE
                    }

                } else if (bookingModel.status == Constants.PAYMENT_FAILED) {
                    binding.statusTv.text = "payment_failed"
                    binding.paymentTv.text =
                        resources.getString(R.string.paid) + " " + bookingModel.customer_currency + " " + bookingModel.price
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text = resources.getString(R.string.pay_now)
                    binding.cancelTv.visibility = View.VISIBLE
//                    binding.cancelTxt.visibility = View.VISIBLE
                } else if (bookingModel.status == Constants.COMPLETED) {
                    binding.statusTv.text = resources.getString(R.string.Completed)
                    binding.paymentTv.text =
                        resources.getString(R.string.paid) + " " + bookingModel.customer_currency + " " + bookingModel.price
                    binding.actionBtn.visibility = View.VISIBLE
                    binding.actionBtn.text = resources.getString(R.string.rate_your_artist)
                }

                if (bookingModel.address != null) {
                    binding.locationTv.text = bookingModel.address.toString()
                } else {
                    binding.locationTxt.visibility = View.GONE
                    binding.locationTv.visibility = View.GONE
                }

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
                Toast.makeText(this, Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Rate and Review Api when booking status would be completed....
    private fun rateReviewBookingApi(bookingID: String, rate: String, review: String) {

        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.rateReviewBookingApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            bookingID,
            rate,
            review
        )
            .enqueue(object : Callback<RateReviewModel> {
                override fun onResponse(
                    call: Call<RateReviewModel>,
                    response: Response<RateReviewModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Toast.makeText(
                                this@BookingDetailFragment,
                                "" + response.body()?.data?.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.rateReviewCustomLayout.visibility = View.GONE
                            finish()

                        }
                    } else {
                        val jsonobject: JSONObject
                        if (response.errorBody() != null) {
                            jsonobject = JSONObject(response.errorBody()?.string()!!)
                            val errorMessage = jsonobject.getString(Constants.ERROR)
                            Toast.makeText(
                                this@BookingDetailFragment,
                                "" + errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<RateReviewModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                }

            })
    }

    private fun reportStatusApi(reason: String) {
        val report = "report"
        binding.loaderLayout.visibility = View.VISIBLE
        RetrofitClient.api.bookingStatusApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.BOOKING_ID,
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
                                this@BookingDetailFragment,
                                "" + response.body()?.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(this@BookingDetailFragment, MainActivity::class.java)
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
                                    this@BookingDetailFragment,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@BookingDetailFragment,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BookingStatusModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(
                        this@BookingDetailFragment,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    // when artist reach then popup will display
    private fun statusPopUp() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View = layoutInflater.inflate(
            R.layout.artist_reach_popup,
            binding.bookingDetailContainer,
            false
        )
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

        val messageTv: TextView = popUp.findViewById(R.id.messageTv)
        val yesReachedTv: TextView = popUp.findViewById(R.id.yesReachedTv)
        val noWantToReportTv: TextView = popUp.findViewById(R.id.noWantToReportTv)
        val cancel: TextView = popUp.findViewById(R.id.cancel)

        yesReachedTv.setOnClickListener {
            // dispaly otp popup
            popupWindow.dismiss()
            otpPopup()
//            if (wantToCancelBooking) {
//                bookingStatusApi()
//                popupWindow.dismiss()
//            }
//            if (isArtistReach) {
//                otpPopup()
//            }
        }

        noWantToReportTv.setOnClickListener {
            popupWindow.dismiss()
            reportPopup()

//            if (wantToCancelBooking) {
//                popupWindow.dismiss()
//            } else if (isArtistReach) {
//                popupWindow.dismiss()
//                reportPopup()
//            }

        }
        cancel.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun otpPopup() {

        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUp: View =
            layoutInflater.inflate(R.layout.otp_popup_layout, binding.bookingDetailContainer, false)
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
            resources.getString(R.string.your_otp_is) + " " + Constants.OTP + " " + getString(
                R.string.share_yout_opt_with
            )
        cross.setOnClickListener {
            otpWindow.dismiss()
        }
    }

    private fun reportPopup() {
        val rAdapter = ArrayAdapter<String>(
            this@BookingDetailFragment,
            android.R.layout.simple_dropdown_item_1line,
            reasonList
        )
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(
            R.layout.report_popup_layout,
            binding.bookingDetailContainer,
            false
        )
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
        val cancel: TextView = popUp.findViewById(R.id.cancelReportTv)
        val done: TextView = popUp.findViewById(R.id.submitTv)
        val reportSpinner: Spinner? = popUp.findViewById(R.id.reportSpinner)


        descriptionTv = popUp.findViewById(R.id.descriptionTxt)

//        val reasonAdapter = ReasonSpinnerAdapter(this, reasonList)
//        val reasonAdapter = ReasonSpinnerAdapter(this@BookingDetailFragment, reasonList)

        // rAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        reportSpinner?.adapter = rAdapter

        reportSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

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

        cancel.setOnClickListener {
            if (descriptionTv?.visibility == View.VISIBLE) {
                descriptionTv?.visibility = View.GONE
                spinnerValue = Constants.SELECT_REASON
            } else {
                reportPopupWindow.dismiss()
            }

        }
        done.setOnClickListener {

            val description = descriptionTv?.text.toString()


            if (spinnerValue == Constants.SELECT_REASON) {
                Utility.alertErrorMessage(
                    this@BookingDetailFragment,
                    getString(R.string.please_choose_reason)
                )
            } else if (spinnerValue == Constants.OTHER) {
                if (description.isEmpty()) {
                    Utility.alertErrorMessage(
                        this@BookingDetailFragment,
                        getString(R.string.please_write_reason_for_other)
                    )
                } else {
                    reportPopupWindow.dismiss()
                    reportStatusApi(description)
                }
            } else {
                reportPopupWindow.dismiss()
                reportStatusApi(spinnerValue)
            }
        }
    }

    override fun onBackPressed() {

        if (Constants.NOTIFICATION) {
            Constants.COMING_FROM_DETAIL = false
            finish()
        } else {
            Constants.COMING_FROM_DETAIL = true
            finish()
        }
        if (Constants.IS_BOOKING_DONE) {
            val intent = Intent(this@BookingDetailFragment, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            finish()
        }
    }


}