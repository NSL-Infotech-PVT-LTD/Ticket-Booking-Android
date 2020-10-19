package com.surpriseme.user.fragments.homefragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants

class ArtistListAdapter(val context: Context, val artistList: ArrayList<DataUserArtistList>,
val artistListFace: ArtistListFace,val bookBtnClick: BookBtnClick) : RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>() {

    inner class ArtistViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.image)

        val name = itemView.findViewById<MaterialTextView>(R.id.name)
        val description = itemView.findViewById<MaterialTextView>(R.id.description)
        val bookBtn = itemView.findViewById<MaterialButton>(R.id.bookBtn)
        val ratingbar = itemView.findViewById<RatingBar>(R.id.ratingbar)
        val seeArtistProfile = itemView.findViewById<MaterialTextView>(R.id.seeArtistProfile)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.artist_recycler_layout,parent,false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {

        val artistModel = artistList[position]

        Picasso.get().load(Constants.ImageUrl.BASE_URL + Constants.ImageUrl.ARTIST_IMAGE_URL + artistModel.image)
            .placeholder(R.drawable.app_logo)
            .resize(4000,1500)
            .onlyScaleDown()
            .into(holder.image)

        holder.name.text = artistModel.name
        holder.description.text = artistModel.description
        holder.ratingbar.rating = artistModel.rating.toFloat()

        holder.itemView.setOnClickListener {
            artistListFace.artistDetailLink(artistModel.id.toString())
        }
        holder.bookBtn.setOnClickListener{
            bookBtnClick.btnClick(artistModel.id.toString())
        }

    }

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