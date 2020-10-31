package com.surpriseme.user.fragments.homefragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants
import java.lang.StringBuilder

class ArtistListAdapter(val context: Context, val artistList: ArrayList<DataUserArtistList>,
val artistListFace: ArtistListFace,val bookBtnClick: BookBtnClick) : RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>() {

    private var categoryList:ArrayList<String> = ArrayList()


    inner class ArtistViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.image)

        val name = itemView.findViewById<MaterialTextView>(R.id.name)
        val description = itemView.findViewById<MaterialTextView>(R.id.description)
        val bookBtn = itemView.findViewById<MaterialButton>(R.id.bookBtn)
        val ratingbar = itemView.findViewById<RatingBar>(R.id.ratingbar)
        val seeArtistProfile = itemView.findViewById<MaterialTextView>(R.id.seeArtistProfile)
        val categories = itemView.findViewById<MaterialTextView>(R.id.categoryTxt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.artist_recycler_layout,parent,false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {

        val artistModel = artistList[position]
        categoryList = artistList[position].category_id_details


        Picasso.get().load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image)
            .placeholder(R.drawable.app_logo)
            .resize(4000,1500)
            .onlyScaleDown()
            .into(holder.image)

        holder.name.text = artistModel.name
        holder.description.text = artistModel.description

        if (artistModel.rating == null) {
            holder.ratingbar.visibility = View.GONE
        }else
        holder.ratingbar.rating = artistModel.rating



                var builder = StringBuilder()
                for (i in 0 until  categoryList.size){
                    builder.append(categoryList[i]+ ",")
                }
                holder.categories.text = builder.toString()


//            if (categoryList.size >0) {
//                for (i in 0 until categoryList.size)
//                    addChip(categoryList[i].toString(),holder.categories)
//            }

        holder.itemView.setOnClickListener {
            artistListFace.artistDetailLink(artistModel.id.toString())
        }
        holder.bookBtn.setOnClickListener{
            bookBtnClick.btnClick(artistModel.id.toString())
        }

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

    override fun getItemCount(): Int {
        return artistList.size
    }

    interface ArtistListFace{
        fun artistDetailLink(artistID: String)
    }
    interface BookBtnClick{
        fun btnClick(artistID: String)
    }

}