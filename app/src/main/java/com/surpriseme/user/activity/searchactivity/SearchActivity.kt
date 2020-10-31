package com.surpriseme.user.activity.searchactivity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
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
import com.surpriseme.user.util.Constants
import com.surpriseme.user.util.HideKeyBoard
import com.surpriseme.user.util.PrefrenceShared
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

    private var binding: ActivitySearchBinding? = null
    private var shared: PrefrenceShared? = null
    private var artistList: ArrayList<DataUserArtistList> = ArrayList()
    private var search = ""
    private var latitude = ""
    private var longitude = ""
    private var hideKeyboard: HideKeyBoard? = null

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
    private var sortBy = ""
    private var showType = "live"
    private var isCategoryClicked = false


    // var for category bottom sheet....
    private lateinit var bottomSheetBehaviorCategory: BottomSheetBehavior<View>
    private lateinit var bottomSheetCategory: View
    private var categoryList: ArrayList<CategoryDataList> = ArrayList()
    private var categoryRecycler: RecyclerView? = null
    private var doneButton: MaterialButton? = null
    private var categoryNameList: ArrayList<CategoryDataList> = ArrayList()
    private var categoryIdList: ArrayList<Int> = ArrayList()
    private var arrow:ImageView?=null

    private var categoryIdLst: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)

        binding = DataBindingUtil.setContentView(this@SearchActivity, R.layout.activity_search)

        shared = PrefrenceShared(this@SearchActivity)
        latitude = shared?.getString(Constants.LATITUDE)!!
        longitude = shared?.getString(Constants.LONGITUDE)!!
        init()
    }

    private fun init() {
        // search bottom sheet view initialization....
        bottomSheet = findViewById<ConstraintLayout>(R.id.boottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // category bottom sheet view initialization....
        bottomSheetCategory = findViewById<ConstraintLayout>(R.id.bottomSheetCategory)
        bottomSheetBehaviorCategory = BottomSheetBehavior.from(bottomSheetCategory)


        //Search bottom sheet callback....
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
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
                if (sortBy == getString(R.string.price_low_to_high)) {
                    sortBy = Constants.ASENDING
                } else {
                    sortBy = Constants.ASENDING
                }
            }
        })
        //Category Bottom Sheet View Initialization....
        categoryRecycler = findViewById(R.id.categoryRecycler)
        doneButton = findViewById(R.id.doneButton)
        arrow = findViewById(R.id.arrow)
        arrow?.setOnClickListener(this)


        hideKeyboard = HideKeyBoard()
        binding?.refresh?.setOnClickListener(this)
        binding?.filterIcon?.setOnClickListener(this)
        closeBtn?.setOnClickListener(this)
        chooseCategory?.setOnClickListener(this)
        doneButton?.setOnClickListener(this)
        applySearch?.setOnClickListener(this)
        binding?.backpress?.setOnClickListener(this)

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
                artistListApi(latitude, longitude, search, "", "")
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
            R.id.refresh -> {
                binding?.searchEdt?.setText("")
                hideKeyboard?.hideKeyboard(this@SearchActivity)
                search = ""
                categoryIdList.clear()
                latitude = shared?.getString(Constants.LATITUDE)!!
                longitude = shared?.getString(Constants.LONGITUDE)!!
                artistListApi(latitude, longitude, search, "", "")
                binding?.noDataFound?.visibility = View.GONE
                binding?.refresh?.visibility = View.GONE
            }
            R.id.filterIcon -> {
                bottomSheetUpDownCategory()
            }
            R.id.closeBtn -> {
                //bottomSheetUpDownCategory()
                bottomSheetUpDownCategory()
                binding?.refresh?.visibility = View.GONE
                binding?.noDataFound?.visibility = View.GONE
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
                latitude = shared?.getString(Constants.LATITUDE)!!
                longitude = shared?.getString(Constants.LONGITUDE)!!
                from_Date = fromDateTxt?.text.toString().trim()
                to_Date = toDateTxt?.text.toString().trim()
                if (from_Date == "From" && to_Date == "To") {
                    from_Date = ""
                    to_Date = ""
                }
                artistListApi(latitude, longitude, search, from_Date, to_Date)
            }
            R.id.backpress -> {
                finish()
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
    private fun artistListApi(
        lat: String,
        lng: String,
        search: String,
        from_Date: String,
        to_Date: String
    ) {

        binding?.loaderLayout?.visibility = View.VISIBLE
        RetrofitClient.api.artistListApi(
            shared?.getString(Constants.DataKey.AUTH_VALUE)!!,
            Constants.DataKey.CONTENT_TYPE_VALUE,
            "",
            lat,
            lng,
            search,
            categoryIdList,
            from_Date,
            to_Date,
            sortBy,
            showType
        )
            .enqueue(object : Callback<ArtistModel> {
                override fun onResponse(call: Call<ArtistModel>, response: Response<ArtistModel>) {
                    binding?.loaderLayout?.visibility = View.GONE
                    if (response.body() != null) {
                        if (response.isSuccessful) {
                            // after api successfull do your stuff....
                            artistList.clear()
                            artistList = response.body()?.data?.data!!
                            if (artistList.isNotEmpty()) {
                                binding?.noDataFound?.visibility = View.GONE
                                binding?.refresh?.visibility = View.GONE
                                val adapter = ArtistListAdapter(
                                    this@SearchActivity,
                                    artistList,
                                    this@SearchActivity,
                                    this@SearchActivity
                                )
                                binding?.searchRecycler?.adapter = adapter
                                binding?.searchLayout?.visibility = View.VISIBLE
                            } else {
                                binding?.noDataFound?.visibility = View.VISIBLE
                                binding?.refresh?.visibility = View.VISIBLE
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
                                    this@SearchActivity,
                                    "" + errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@SearchActivity,
                                    "" + e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ArtistModel>, t: Throwable) {
                    binding?.loaderLayout?.visibility = View.GONE
                    Toast.makeText(
                        this@SearchActivity,
                        "" + t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    override fun artistDetailLink(artistID: String) {
// Calling interface from Artist List adapter to send data from home fragment to ArtistDetail fragment with list.
        shared?.setString(Constants.ARTIST_ID, artistID)
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
        }else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            finish()
        }
    }

}