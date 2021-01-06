package com.surpriseme.user.fragments.homefragment

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.BaseViewHolder
import com.surpriseme.user.util.Constants
import java.lang.StringBuilder
import java.text.DecimalFormat
import kotlin.math.roundToLong

class ArtistListAdapter(
    val context: Context,
    val artistList: ArrayList<DataUserArtistList>,
    val artistListFace: ArtistListFace,
    val bookBtnClick: BookBtnClick
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var categoryList: ArrayList<String> = ArrayList()

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var price:Double = 0.0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

//        val view = LayoutInflater.from(context).inflate(R.layout.artist_recycler_layout,parent,false)
//        return ArtistViewHolder(view)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(

                LayoutInflater.from(context).inflate(R.layout.artist_recycler_layout, parent, false)
            )
            VIEW_TYPE_LOADING -> ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
            )
            else -> null!!
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        if (artistList.size == 1) {
            return VIEW_TYPE_NORMAL
        }
        return if (isLoaderVisible) {
            if (position == artistList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return artistList.size ?: 0
    }

    fun addItems(postItems: ArrayList<DataUserArtistList>?) {

        artistList.addAll(postItems!!)
        var myList = artistList.distinctBy { it.id } as ArrayList
        artistList.clear()
        artistList.addAll(myList)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        artistList.add(DataUserArtistList())
        notifyItemInserted(artistList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = artistList.size - 1
        val item: DataUserArtistList = getItem(position)
        if (item != null) {
            artistList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        artistList.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): DataUserArtistList {
        return artistList[position]
    }


//    private fun addChip(pItem: String, textView: TextView: MaterialTextView) {
//        val lChip = Chip(context)
//        lChip.text = pItem
//        lChip.isClickable = false
//        lChip.textSize = 13F
//// lChip.chipStrokeColor = resources.getColorStateList(R.color.colorAccent)
//// lChip.chipStrokeWidth = 1F
//        lChip.setEnsureMinTouchTargetSize(false)
//        lChip.setTextColor(ContextCompat.getColor(context, R.color.white))
//        lChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.grey_color)
//// following lines are for the demo
//        pChipGroup.addView(lChip as View)
//
//    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.image)

        val name = itemView.findViewById<TextView>(R.id.name)
        val description = itemView.findViewById<TextView>(R.id.description)
        val bookBtn = itemView.findViewById<MaterialTextView>(R.id.bookBtn)
        val ratingbar = itemView.findViewById<RatingBar>(R.id.ratingbar)
        val seeArtistProfile = itemView.findViewById<TextView>(R.id.seeArtistProfile)
        val categories = itemView.findViewById<TextView>(R.id.categoryTxt)
        val price = itemView.findViewById<TextView>(R.id.priceTv)
        val brandNewArtistTv = itemView.findViewById<MaterialTextView>(R.id.brandNewArtistTv)

        override fun clear() {}

        override fun onBind(position: Int) {
            super.onBind(position)

            val artistModel = artistList[position]
//            if (artistList[position].category_id_details !=null || artistList[position].category_id_details?.isNotEmpty()!!)
//            categoryList = artistList[position].category_id_details!!

            Picasso.get()
                .load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image)
                .placeholder(R.drawable.artist_pholder)
                .into(image)

            name.text = artistModel.name
            description.text = artistModel.description

            if (artistModel.rating == 0f) {
                ratingbar.visibility = View.GONE
                brandNewArtistTv.visibility = View.VISIBLE
            } else {
                ratingbar.rating = artistModel.rating
                brandNewArtistTv.visibility = View.GONE
            }



            categoryList.clear()
            if (artistModel.category_id_details != null) {
                for (i in 0 until artistModel.category_id_details.size) {
                    categoryList.add(artistModel.category_id_details.get(i).category_name)
                }
            }


            var s = ""

            if (artistModel.category_id_details != null) {
                val builder = StringBuilder()
                for (detail in categoryList) {
                    builder.append(detail)
                    s = TextUtils.join(", ", categoryList)
                }
                categories.text = s
            }

            if (Constants.SHOW_TYPE == "digital") {
                price.text = artistModel.converted_currency + " " +
                        DecimalFormat("#.##").format(artistModel.converted_digital_price)+ "/" + context.resources.getString(
                        R.string.hr)
            } else {
                price.text = artistModel.converted_currency + " " + DecimalFormat("#.##").format(artistModel.converted_live_price)+ "/" + context.resources.getString(
                        R.string.hr
                    )
            }



            itemView.setOnClickListener {
                artistListFace.artistDetailLink(artistModel.id.toString())
            }
            bookBtn.setOnClickListener {
                bookBtnClick.btnClick(artistModel.id.toString())
            }
        }
    }

    class ProgressHolder internal constructor(itemView: View?) :
        BaseViewHolder(itemView) {
        override fun clear() {}
    }

    interface ArtistListFace {
        fun artistDetailLink(artistID: String)
    }

    interface BookBtnClick {
        fun btnClick(artistID: String)
    }


}