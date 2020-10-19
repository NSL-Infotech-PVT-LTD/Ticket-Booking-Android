package com.surpriseme.user.fragments.bookingdetailfragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentBookingDetailBinding
import com.surpriseme.user.fragments.bookingfragment.BookingFragment
import com.surpriseme.user.fragments.notificationfragment.NotificationFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class BookingDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentBookingDetailBinding
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var fromTime = ""
    private var toTime = ""
    private var bookingId = ""
    private var bookingModel: BookingDataModel? = null

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
        shared = PrefrenceShared(ctx)





        init()

        return view
    }

    private fun init() {
        bookingId = arguments?.getString("bookingId")!!
        bookingDetailApi()
        binding.backArrow.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
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
                            if (bookingModel != null) {
                                displayDetail(bookingModel!!)
                            } else {
                                Toast.makeText(ctx, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
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

    private fun addChip(pItem: String, pChipGroup: ChipGroup) {
        val lChip = Chip(context)
        lChip.text = pItem
        lChip.isClickable = false
        lChip.textSize = 13F
// lChip.chipStrokeColor = resources.getColorStateList(R.color.colorAccent)
// lChip.chipStrokeWidth = 1F
        lChip.setEnsureMinTouchTargetSize(false)
        lChip.setTextColor(ContextCompat.getColor(ctx, R.color.white))
        lChip.chipBackgroundColor = ContextCompat.getColorStateList(ctx, R.color.grey_color)
// following lines are for the demo
        pChipGroup.addView(lChip as View)

    }

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
                binding.statusMtv.text = "Status:- ${bookingModel.status}"
                binding.showTypeMtv.text = "Show type:- ${bookingModel?.type}"
                binding.addressMtv.text = bookingModel?.address
                if (bookingModel.rate_detail != null) {

                    // Display date at top of card....
                    var date = bookingModel.date
                    var spf = SimpleDateFormat("yyyy-MM-dd")
                    val newDate: Date = spf.parse(date)
                    spf = SimpleDateFormat("dd-MMM-yyyy")
                    date = spf.format(newDate)


                }
                binding.dateTxt.text = bookingModel.date

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
                Toast.makeText(ctx, Constants.SOMETHING_WENT_WRONG,Toast.LENGTH_SHORT).show()
                val fragment = NotificationFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameContainer,fragment)
                transaction?.commit()
            }
        }

    }

}