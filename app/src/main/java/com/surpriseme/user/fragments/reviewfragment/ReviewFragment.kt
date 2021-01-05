package com.surpriseme.user.fragments.reviewfragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.databinding.FragmentReviewBinding
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.PrefrenceShared
import com.surpriseme.user.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ReviewFragment : Fragment(), View.OnClickListener {

    private var binding:FragmentReviewBinding?=null
    private var shared:PrefrenceShared?=null
    private var ctx:Context?=null
    private var layoutManager:RecyclerView.LayoutManager?=null
    private var reviewList:ArrayList<ReviewDataModel> = ArrayList()
    private var artistID:String = ""
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

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_review, container, false)
        val view = binding?.root

        shared = PrefrenceShared(ctx!!)

        init(view!!)


        return view
    }

    private fun init(view:View) {

        val loadingText = view.findViewById<TextView>(R.id.loadingtext)
        loadingText.text  = Utility.randomString(ctx!!)
        // initializing onclick listener to views....
        backpress = view.findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)
        artistID = shared?.getString(Constants.ARTIST_ID)!!

        reviewListApi()

        initializeReviewRecycler()

    }

    private fun initializeReviewRecycler() {

        layoutManager = LinearLayoutManager(ctx!!)
        binding?.reviewRecycler?.layoutManager = layoutManager
        binding?.reviewRecycler?.setHasFixedSize(true)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.backpress -> {
                fragmentManager?.popBackStack()
            }

        }
    }

    private fun reviewListApi() {

        binding?.loaderLayout?.visibility = View.VISIBLE

        RetrofitClient.api.customerReviewApi(shared?.getString(Constants.DataKey.AUTH_VALUE)!!,artistID)
            .enqueue(object :Callback<ReviewModel> {
                override fun onResponse(call: Call<ReviewModel>, response: Response<ReviewModel>) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() !=null) {
                        if (response.isSuccessful) {

                            reviewList.clear()
                            reviewList = response.body()?.data!!

                            if (reviewList.isNotEmpty()) {

                                val arrayCount = reviewList.size
                                val reviewAdapter = ReviewAdapter(ctx!!,reviewList)
                                binding?.reviewRecycler?.adapter = reviewAdapter

                            }else {

                            }
                        }
                    }else if (response.code() == 401) {

                    } else {
                        val jsonObject:JSONObject
                        if (response.errorBody() !=null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Snackbar.make(binding?.reviewContainer!!,"" + errorMessage,BaseTransientBottomBar.LENGTH_SHORT).show()
                            }catch (e:JSONException) {
                                Snackbar.make(binding?.reviewContainer!!,"" + e.message.toString(),BaseTransientBottomBar.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ReviewModel>, t: Throwable) {

                    binding?.loaderLayout?.visibility = View.GONE
                    Snackbar.make(binding?.reviewContainer!!,"" + t.message.toString(),BaseTransientBottomBar.LENGTH_SHORT).show()
                }


            })

    }

}