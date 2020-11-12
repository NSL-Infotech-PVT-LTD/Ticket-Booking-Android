package com.surpriseme.user.fragments.locationfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.util.Constants

class LocationListAdapter(private val context: Context, private val locationList: ArrayList<LocationDataList>,private val dispAddToDashboard: DisplayAddToDashboard,
private val deleteAddress: DeleteAddress,private val editLocation: EditAddress) :RecyclerView.Adapter<LocationListAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val addressIcon = itemView.findViewById<ImageView>(R.id.addressIcon)!!
        val addressTypeTxt = itemView.findViewById<MaterialTextView>(R.id.addressTypeTxt)!!
        val editAddress = itemView.findViewById<ImageView>(R.id.addressEditIcon)!!
        val deleteAddress = itemView.findViewById<ImageView>(R.id.addressDeleteIcon)!!
        val address = itemView.findViewById<MaterialTextView>(R.id.addressTxt)!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.location_list_layout,parent,false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {

        val model = locationList[position]
        if (model !=null) {
            when (model.name) {
                Constants.HOME_ADDRESS -> {
                    holder.addressIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.home_icon_updated
                        )
                    )
                    holder.addressTypeTxt.text = model.name
                }
                Constants.WORK_ADDRESS -> {

                    holder.addressIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.work_icon
                        )
                    )
                    holder.addressTypeTxt.text = model.name
                }
                else -> {
                holder.addressIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.other_icon
                    )
                )
                holder.addressTypeTxt.text = model.name
            }
            }
            holder.address.text = model.street_address

            holder.editAddress.setOnClickListener {

                editLocation.updateAddress(locationList[position])

            }
            holder.deleteAddress.setOnClickListener {
                deleteAddress.deleteAdd(model.id.toString())
            }

            holder.itemView.setOnClickListener {
                // Item view click to send address to dashboard, Click implemented on LocationFragment....
                if (model.street_address!=null)
                dispAddToDashboard.dispAddressDashboard(model.street_address, model.latitude,model.longitude)
            }
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    interface DisplayAddToDashboard{
        fun dispAddressDashboard(address:String, lat:String,lng:String)
    }
    interface DeleteAddress {
        fun deleteAdd(id:String)
    }
    interface EditAddress {
        fun updateAddress(locationDataList: LocationDataList)
    }

}