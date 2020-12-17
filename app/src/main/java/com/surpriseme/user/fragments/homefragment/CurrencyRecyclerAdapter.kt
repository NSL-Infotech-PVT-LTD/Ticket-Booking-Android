package com.surpriseme.user.fragments.homefragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.surpriseme.user.R
import com.surpriseme.user.util.PrefrenceShared

class CurrencyAdapter(val shared:PrefrenceShared,val context: Context, val currencyList:ArrayList<CurrencyList>,
val currencyAdpClick: CurrencyAdpClick) :RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var adpPosition = shared.getInt("myCurrencyAdp")
    inner class CurrencyViewHolder(itemview:View) :RecyclerView.ViewHolder(itemview) {

        val currencyName = itemview.findViewById<TextView>(R.id.currencyNameTv)
        val currencyRadio = itemview.findViewById<RadioButton>(R.id.radioBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.currency_recycler_layout,parent,false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {

        val model = currencyList[position]
        holder.currencyName.text = model.currency

        if (adpPosition == holder.adapterPosition) {
            holder.currencyRadio.isChecked = true
        }

        holder.currencyRadio.isChecked = adpPosition == position

        holder.itemView.setOnClickListener {

            holder.currencyRadio.isChecked = true
            adpPosition = holder.adapterPosition
            shared.setInt("myCurrencyAdp",adpPosition)
            notifyDataSetChanged()
            currencyAdpClick.currencyClick(model.currency)

        }

    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    interface CurrencyAdpClick {
        fun currencyClick(currency:String)
    }
}