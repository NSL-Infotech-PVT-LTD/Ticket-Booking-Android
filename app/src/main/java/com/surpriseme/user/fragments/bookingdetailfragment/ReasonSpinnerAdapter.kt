package com.surpriseme.user.fragments.bookingdetailfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.surpriseme.user.R
import kotlinx.android.synthetic.main.reason_spinner_layout.view.*

class ReasonSpinnerAdapter(val ctx: Context, val list: ArrayList<String>) : ArrayAdapter<String>(
    ctx,
    R.layout.reason_spinner_layout,
    list
){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return this.createView(position, convertView, parent)
    }
    fun setItems(myList: ArrayList<String>) {
        list.clear()
        list.addAll(myList)
        notifyDataSetChanged()
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return this.createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView?: LayoutInflater.from(ctx).inflate(R.layout.reason_spinner_layout, parent, false)
        view.spinnerTxt.setText(list.get(position))
        return view
    }
}
