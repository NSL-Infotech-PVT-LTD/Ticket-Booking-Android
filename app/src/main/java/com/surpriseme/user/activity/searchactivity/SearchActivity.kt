package com.surpriseme.user.activity.searchactivity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.mainactivity.MainActivity
import com.surpriseme.user.databinding.ActivitySearchBinding
import com.surpriseme.user.fragments.homefragment.ArtistListAdapter
import com.surpriseme.user.fragments.homefragment.ArtistModel
import com.surpriseme.user.fragments.homefragment.DataUserArtistList
import com.surpriseme.user.retrofit.RetrofitClient
import com.surpriseme.user.util.*
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_booking_detail.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity(), ArtistListAdapter.ArtistListFace,
    ArtistListAdapter.BookBtnClick, View.OnClickListener, CategoryAdapter.CheckList {

    private var seekbarRatingProgress: Float? = null
    private var binding: ActivitySearchBinding? = null
    private var shared: PrefrenceShared? = null
    private var artistList: ArrayList<DataUserArtistList> = ArrayList()
    private var search = ""
    private var isSearching = false
    private var latitude = ""
    private var longitude = ""
    private var hideKeyboard: HideKeyBoard? = null
    private var artistListAdapter: ArtistListAdapter? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var isLastPage = false
    private var isLoading = false
    private var currentPage: Int = 1
    private var totalPage = 0
    private var backpress: MaterialTextView? = null

    //var for search bottom sheet....
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheet: View
    private var closeBtn: MaterialButton? = null
    private var chooseCategory: MaterialTextView? = null
    private var applySearch: MaterialButton? = null
    private var fromDateTxt: MaterialTextView? = null
    private var toDateTxt: MaterialTextView? = null
    private var datepickerdialog: DatePickerDialog? = null
    private var from_Date = ""
    private var to_Date = ""
    private var radioGroup: RadioGroup? = null
    private var sortBy = Constants.ASENDING
    private var showType = ""
    private var isCategoryClicked = false
    private var seekbarRating: IndicatorSeekBar? = null
    private var byDistanceTv: MaterialTextView? = null
    private var seekbarDistance: IndicatorSeekBar? = null
    private var kilometerLayout: ConstraintLayout? = null
    private var byRating = ""
    private var byDistance = ""
    private var radioLowToHigh: RadioButton? = null
    private var radioHighToLow: RadioButton? = null

    // var for category bottom sheet....
    private lateinit var bottomSheetBehaviorCategory: BottomSheetBehavior<View>
    private lateinit var bottomSheetCategory: View
    private var categoryList: ArrayList<CategoryDataList> = ArrayList()
    private var categoryRecycler: RecyclerView? = null
    private var doneButton: MaterialButton? = null
    private var categoryNameList: ArrayList<CategoryDataList> = ArrayList()
    private var categoryIdList: ArrayList<Int> = ArrayList()
    private var arrow: ImageView? = null

    private var categoryIdLst: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)

        binding = DataBindingUtil.setContentView(this@SearchActivity, R.layout.activity_search)

        shared = PrefrenceShared(this@SearchActivity)
        Constants.IS_SEARCH_ACTIVITY = true
        Constants.IS_COMING_FROM_BOOKING_DETAIL = false
        Constants.IS_CHAT_SESSION = false

        latitude = shared?.getString(Constants.LATITUDE)!!
        longitude = shared?.getString(Constants.LONGITUDE)!!

        showType = Constants.SHOW_TYPE
        if (showType == getString(R.string.digital)) {
            byDistanceTv?.visibility = View.GONE
            seekbarDistance?.visibility = View.GONE
        } else {
            byDistanceTv?.visibility = View.VISIBLE
            seekbarDistance?.visibility = View.VISIBLE
        }


        if (shared?.getInt("byRating") != null) {
            val r: Int = shared?.getInt("byRating")!!
            seekbarRating?.setProgress(shared?.getInt("byRating")!!.toFloat())
        }
        init()


    }

    private fun init() {

        // search bottom sheet view initialization....
        bottomSheet = findViewById<ConstraintLayout>(R.id.boottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // category bottom sheet view initialization....
        bottomSheetCategory = findViewById<ConstraintLayout>(R.id.bottomSheetCategory)
        bottomSheetBehaviorCategory = BottomSheetBehavior.from(bottomSheetCategory)

        backpress = findViewById(R.id.backpress)
        backpress?.setOnClickListener(this)

        layoutManager = LinearLayoutManager(this@SearchActivity)
        binding?.searchRecycler?.layoutManager = layoutManager
        binding?.searchRecycler?.setHasFixedSize(true)
        artistListAdapter =
            ArtistListAdapter(
                applicationContext,
                ArrayList(),
                this@SearchActivity,
                this@SearchActivity
            )
        binding?.searchRecycler?.adapter = artistListAdapter

        binding?.searchEdt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                artistList.clear()
                artistListAdapter?.clear()
                artistListAdapter?.notifyDataSetChanged()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                artistList.clear()
                artistListAdapter?.clear()
                artistListAdapter?.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {
                artistList.clear()
                artistListAdapter?.clear()
                artistListAdapter?.notifyDataSetChanged()
                search = s.toString()
                if (search.length <= 2) {
                    Toast.makeText(
                        this@SearchActivity,
                        "Enter atleast three character",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    artistListApiWithoutLatlng(search)

//                    if (Constants.SHOW_TYPE == getString(R.string.digital)) {
//                    } else {
//                        artistListApi(
//                            shared?.getString(Constants.LATITUDE)!!,
//                            shared?.getString(Constants.LONGITUDE)!!,
//                            search
//                        )
//                    }
                }
            }


        })

        binding?.searchRecycler?.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                if (showType == getString(R.string.digital)) {
                    artistListApiWithoutLatlng(search)
                } else {
                    artistListApi(
                        shared?.getString(Constants.LATITUDE)!!,
                        shared?.getString(Constants.LONGITUDE)!!,
                        search
                    )
                }

            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        radioLowToHigh = findViewById(R.id.radioLowToHigh)
        radioHighToLow = findViewById(R.id.radioHighToLow)
        radioLowToHigh?.setOnClickListener {
            radioLowToHigh?.isChecked = true
            radioHighToLow?.isChecked = false
        }
        radioHighToLow?.setOnClickListener {
            radioHighToLow?.isChecked = true
            radioLowToHigh?.isChecked = false
        }


        seekbarRating = findViewById(R.id.seekbarRating)
        val tickArray: Array<String> = arrayOf("5", "4.5", "4.0", "3.5", "Any")
        seekbarRating?.customTickTexts(tickArray)
        byDistanceTv = findViewById(R.id.byDistanceTv)
        seekbarDistance = findViewById(R.id.seekbarDistance)



        if (showType == getString(R.string.digital)) {
            showType = getString(R.string.digital)
            byDistanceTv?.visibility = View.GONE
            seekbarDistance?.visibility = View.GONE
            kilometerLayout?.visibility = View.GONE
        } else {
            showType = getString(R.string.live)
            byDistanceTv?.visibility = View.VISIBLE
            seekbarDistance?.visibility = View.VISIBLE
            kilometerLayout?.visibility = View.VISIBLE
        }

        seekbarRating?.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {

                seekbarRatingProgress = seekParams?.progress?.toFloat()
                byRating = seekParams?.tickText!!
                if (byRating == "Any") {
                    byRating = "0"
//                    shared?.setInt(Constants.DataKey.BY_RATING,showByRating!!)
                    shared?.setInt("byRating", seekbarRatingProgress?.toInt())
                }

            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

        }

        seekbarDistance?.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                byDistance = seekParams?.tickText!!
                Toast.makeText(this@SearchActivity, "" + byDistance.toString(), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {

            }

        }


        //Search bottom sheet callback....
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding?.whiteBgLayout?.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding?.whiteBgLayout?.visibility = View.VISIBLE
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
        // Search Bottom Sheet view initialization....
        closeBtn = findViewById(R.id.closeBtn)
        chooseCategory = findViewById(R.id.chooseCategoryTxt)
        applySearch = findViewById(R.id.applySearchBtn)
        fromDateTxt = findViewById(R.id.fromDateTxt)
        fromDateTxt?.setOnClickListener(this)
        toDateTxt = findViewById(R.id.toDateTxt)
        toDateTxt?.setOnClickListener(this)
        radioGroup = findViewById(R.id.radioGroup)

        // Getting value from radio buttons....
        radioGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                val radioId = group?.findViewById<RadioButton>(checkedId)
                sortBy = radioId?.text.toString()
                if (sortBy == Constants.PRICE_LOW_TO_HIGH) {
                    sortBy = Constants.ASENDING
                } else {
                    sortBy = Constants.DESENDING
                }
            }
        })
        //Category Bottom Sheet View Initialization....
        categoryRecycler = findViewById(R.id.categoryRecycler)
        doneButton = findViewById(R.id.doneButton)
        arrow = findViewById(R.id.arrow)
        arrow?.setOnClickListener(this)


        hideKeyboard = HideKeyBoard()

        binding?.filterIcon?.setOnClickListener(this)
        closeBtn?.setOnClickListener(this)
        chooseCategory?.setOnClickListener(this)
        doneButton?.setOnClickListener(this)
        applySearch?.setOnClickListener(this)

        // Category Bottom Sheet Behavior callback....
        bottomSheetBehaviorCategory.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        // search implement by clicking on keyboard done button....
        binding?.searchEdt?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search = binding?.searchEdt?.text.toString().trim()
                hideKeyboard?.hideKeyboard(this@SearchActivity)
                categoryIdList.clear()
                latitude = shared?.getString(Constants.LATITUDE)!!
                longitude = shared?.getString(Constants.LONGITUDE)!!
//                artistListApi(latitude, longitude, search)
                //do here your stuff f
                true
            } else false
        })
    }

    // Search Bottom Sheet Method....
    private fun bottomSheetUpDownCategory() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            categoryListApi()

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }

    // Category Bottom Sheet Method....
    private fun bottomSheetUpDownCategoryForCategory() {
        if (bottomSheetBehaviorCategory.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorCategory.setState(BottomSheetBehavior.STATE_EXPANDED)

        } else {
            bottomSheetBehaviorCategory.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.filterIcon -> {
                binding?.whiteBgLayout?.visibility = View.VISIBLE
                bottomSheetUpDownCategory()
            }
            R.id.closeBtn -> {
                //bottomSheetUpDownCategory()
                binding?.whiteBgLayout?.visibility = View.GONE
                bottomSheetUpDownCategory()
                binding?.noDataFoundLayout?.visibility = View.GONE
            }
            R.id.chooseCategoryTxt -> {
                isCategoryClicked = true
                bottomSheetUpDownCategoryForCategory()

            }
            R.id.doneButton -> {

                if (categoryNameList.size > 0) {
                    val builder = StringBuilder()
                    for (details in categoryNameList) {
                        builder.append(details.name)
                        categoryIdLst.add(details.id)
                    }
                    chooseCategory?.text = builder.toString()
                }
                bottomSheetUpDownCategoryForCategory()

            }
            R.id.applySearchBtn -> {
                bottomSheetUpDownCategory()
                artistListAdapter?.clear()

                if (showType == getString(R.string.digital)) {
                    from_Date = fromDateTxt?.text.toString().trim()
                    to_Date = toDateTxt?.text.toString().trim()
                    if (from_Date == getString(R.string.from) && to_Date == getString(R.string.to)) {
                        from_Date = ""
                        to_Date = ""
                    }

                    artistListApiWithoutLatlng(search)
                } else {
                    latitude = shared?.getString(Constants.LATITUDE)!!
                    longitude = shared?.getString(Constants.LONGITUDE)!!
                    from_Date = fromDateTxt?.text.toString().trim()
                    to_Date = toDateTxt?.text.toString().trim()
                    if (from_Date == getString(R.string.from) && to_Date == getString(R.string.to)) {
                        from_Date = ""
                        to_Date = ""
                    }
                    artistListApi(latitude, longitude, search)
                }

            }
            R.id.backpress -> {
                finish()
                Constants.IS_SEARCH_ACTIVITY = false
            }
            R.id.fromDateTxt -> {
                openCalendar(fromDateTxt!!)
            }
            R.id.toDateTxt -> {
                openCalendar(toDateTxt!!)
            }
            R.id.arrow -> {
                bottomSheetUpDownCategoryForCategory()
            }
        }
    }

    // open Date Picker Dialog with minumum date of one day after from today....
    private fun openCalendar(textView: MaterialTextView) {
        val cal = Calendar.getInstance()

        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        datepickerdialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                val month = monthOfYear + 1
                val date = "" + year + "-" + month + "-" + dayOfMonth

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val dateToFormat = sdf.parse(date)
                val finalDate = sdf.format(dateToFormat)
                textView.textSize = 10f
//                    fromDateTxt?.setText("" + dayOfMonth + " " + month + ", " + year)
                textView.text = finalDate

            },
            y,
            m,
            d
        )
        datepickerdialog?.show()
        // display calender minimum date as tomorrow date....
        val oneDayAfter = cal.clone() as Calendar
        oneDayAfter.add(Calendar.DATE, 1)
        datepickerdialog?.datePicker?.minDate = oneDayAfter.timeInMillis
        // display calender minimum date as today date...
//        datepickerdialog?.getDatePicker()?.setMinDate(System.currentTimeMillis())

    }

    private fun artistListApi(lat: String, lng: String, search: String) {

//        var array = arrayOf(1)
        if (currentPage == 1)
            binding?.progressBar?.visibility = View.VISIBLE

        RetrofitClient.api.artistListApi(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            Constants.DataKey.CONTENT_TYPE_VALUE,
            "",
            currentPage.toString(),
            lat,
            lng,
            search,
            categoryIdList.toString(),
            from_Date,
            to_Date,
            sortBy,
            showType,
            byRating,
            byDistance
        )
            .enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    binding?.progressBar?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {

                            // after api successfull do your stuff....
                            artistList.clear()
                            artistList = response.body()?.data?.data!!
                            totalPage = response.body()?.data?.last_page!!
                            if (artistList.size > 0) {

                                Handler().postDelayed({
                                    run {
                                        /**
                                         * manage progress view
                                         */
                                        /**
                                         * manage progress view
                                         */
                                        if (currentPage != PaginationScrollListener.PAGE_START)
                                            artistListAdapter?.removeLoading()
                                        artistListAdapter?.addItems(artistList)
                                        if (currentPage < totalPage) {
                                            artistListAdapter?.addLoading()
                                        } else {
                                            isLastPage = true
                                        }
                                        isLoading = false
                                    }
                                }, 1500)

                                binding?.searchLayout?.visibility = View.VISIBLE
                                binding?.noDataFoundLayout?.visibility = View.GONE

                            } else {
                                binding?.noDataFoundLayout?.visibility = View.VISIBLE
                                binding?.searchLayout?.visibility = View.GONE

                            }
                        }
                    } else {

                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERRORS)
                                Toast.makeText(
                                    applicationContext,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    applicationContext,
                                    "" + Constants.SOMETHING_WENT_WRONG,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(
                        this@SearchActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun artistListApiWithoutLatlng(search: String) {


        binding?.progressBar?.visibility = View.VISIBLE
        RetrofitClient.api.artistListApi(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            Constants.DataKey.CONTENT_TYPE_VALUE, "", search, currentPage.toString(),
            categoryIdList.toString(), from_Date, to_Date, sortBy, showType, byRating, byDistance
        )
            .enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    binding?.progressBar?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            // after api successfull do your stuff....
                            artistList.clear()
                            artistList = response.body()?.data?.data!!
//                            totalPage = response.body()?.data?.last_page!!
                            if (artistList.size > 0) {

                                Handler().postDelayed({
                                    run {
                                        /**
                                         * manage progress view
                                         */
                                        /**
                                         * manage progress view
                                         */

                                        if (currentPage != PaginationScrollListener.PAGE_START){
                                            artistListAdapter?.removeLoading()
                                        }
                                        artistListAdapter?.addItems(artistList)
                                        if (currentPage < totalPage) {
                                            artistListAdapter?.addLoading()
                                        } else {
                                            isLastPage = true
                                        }
                                        isLoading = false

                                    }
                                }, 1500)



                                binding?.noDataFoundLayout?.visibility = View.GONE
                                binding?.searchLayout?.visibility = View.VISIBLE

                                artistListAdapter?.notifyDataSetChanged()


                            } else {
                                binding?.noDataFoundLayout?.visibility = View.VISIBLE
                                binding?.searchLayout?.visibility = View.GONE
                            }
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    applicationContext,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    applicationContext,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
    }

    override fun artistDetailLink(artistID: String) {
// Calling interface from Artist List adapter to send data from home fragment to ArtistDetail fragment with list.
        shared?.setString(Constants.ARTIST_ID, artistID)
        Constants.IS_ADDED_TO_BACKSTACK = false
        val mainActIntent = Intent(this@SearchActivity, MainActivity::class.java)
        mainActIntent.putExtra("artistID", artistID)
        startActivity(mainActIntent)
    }

    override fun btnClick(artistID: String) {
        shared?.setString(Constants.ARTIST_ID, artistID)
        val mainActIntent = Intent(this@SearchActivity, MainActivity::class.java)
        mainActIntent.putExtra("artistBook", artistID)
        startActivity(mainActIntent)
    }

    private fun categoryListApi() {

        RetrofitClient.api.categoryListApi(Constants.DataKey.CONTENT_TYPE_VALUE)
            .enqueue(object : Callback<CategoryModel> {
                override fun onResponse(
                    call: Call<CategoryModel>,
                    response: Response<CategoryModel>
                ) {
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            categoryList.clear()
                            categoryList = response.body()?.data!!
                            val categoryAdapter = CategoryAdapter(
                                this@SearchActivity,
                                categoryList,
                                this@SearchActivity
                            )
                            categoryRecycler?.adapter = categoryAdapter
                        }
                    } else {
                        val jsonObject: JSONObject
                        if (response.errorBody() != null) {
                            try {
                                jsonObject = JSONObject(response.errorBody()?.string()!!)
                                val errorMessage = jsonObject.getString(Constants.ERROR)
                                Toast.makeText(
                                    this@SearchActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SearchActivity,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                    Toast.makeText(
                        this@SearchActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
    }

    // override method from Category Adapter to get category from category bottom sheet and display in search bottom sheet.
    override fun getCheckList(
        categoryName: ArrayList<CategoryDataList>,
        professionId: ArrayList<Int>
    ) {
        categoryNameList = categoryName
        categoryIdList = professionId
    }

    override fun onBackPressed() {
        if (bottomSheetBehaviorCategory.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorCategory.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            finish()
        }
    }

}