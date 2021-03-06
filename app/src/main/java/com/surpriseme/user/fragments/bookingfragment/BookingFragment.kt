package com.surpriseme.user.fragments.bookingfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.FragmentBookingBinding
import com.surpriseme.user.fragments.bookingdetailfragment.BookingDetailFragment
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PaginationScrollListener
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingFragment : Fragment(), View.OnClickListener, BookingListAdapter.SeeFullDetailClick {

    private lateinit var binding: FragmentBookingBinding
    private lateinit var ctx: Context
    private lateinit var shared: PrefrenceShared
    private var bookingList: ArrayList<BookingArtistDetailModel> = ArrayList()
    private var adapter: BookingListAdapter? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var isLastPage = false
    private var isLoading = false
    private var currentPage: Int = 1
    private var totalPage = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context

    }

    override fun onStart() {
        super.onStart()
        bookingListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false)
        val view = binding.root
        shared = PrefrenceShared(ctx)

        init(view)
        layoutManager = LinearLayoutManager(ctx)
        binding.bookingRecycler.layoutManager = layoutManager
        binding.bookingRecycler.setHasFixedSize(true)

        adapter = BookingListAdapter(ctx, ArrayList(), this@BookingFragment)
        binding.bookingRecycler.adapter = adapter
        binding.bookingRecycler.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                bookingListApi()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        return view
    }

    private fun init(view: View) {

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText?.text  = Utility.randomString(ctx)
        ((ctx as MainActivity)).showBottomNavigation()
        // Initializing onclick listener to views....

    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    private fun bookingListApi() {

        if (currentPage == 1)
            binding.loaderLayout.visibility = View.VISIBLE

        RetrofitClient.api.customerBookingListApi(
            shared.getString(Constants.DataKey.AUTH_VALUE),
            Constants.DataKey.CONTENT_TYPE_VALUE, "20", currentPage.toString()
        )
            .enqueue(object : Callback<CustomerBookingListModel> {
                override fun onResponse(
                    call: Call<CustomerBookingListModel>,
                    response: Response<CustomerBookingListModel>
                ) {
                    binding.loaderLayout.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            bookingList.clear()
                            bookingList = response.body()?.data?.data!!
                            totalPage = response.body()?.data?.last_page!!
                            if (bookingList.size > 0) {

                                Handler().postDelayed({
                                    run {
                                        /**
                                         * manage progress view
                                         */
                                        /**
                                         * manage progress view
                                         */
                                        if (currentPage != PaginationScrollListener.PAGE_START)
                                            adapter?.removeLoading()
                                        adapter?.addItems(bookingList)
                                        if (currentPage < totalPage) {
                                            adapter?.addLoading()
                                        } else {
                                            isLastPage = true
                                        }
                                        isLoading = false
                                    }
                                }, 1500)

                                binding.noDataFoundLayout.visibility = View.GONE

                            } else {
                                binding.youMayEditTxt.text = ctx.resources.getString(R.string.manage_your_bookings_here)
                                binding.noDataFoundLayout.visibility = View.VISIBLE
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
                                Toast.makeText(ctx, "" + e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CustomerBookingListModel>, t: Throwable) {
                    binding.loaderLayout.visibility = View.GONE
                    Toast.makeText(ctx, "" + t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    // override method from BookingListAdapter, See Full Detail button click , send data to BookingDetail Fragment....
    override fun fullDetail(bookingID: String) {
        Constants.BOOKING = true
        Constants.NOTIFICATION = false
        Constants.IS_BOOKING_DONE = false
        val intent = Intent(ctx, BookingDetailFragment::class.java)
        Constants.BOOKING_ID = bookingID
        startActivity(intent)

    }


}