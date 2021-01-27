package com.surpriseme.user.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.surpriseme.user.R
import com.surpriseme.user.data.model.CardGetDetailModel
import com.surpriseme.user.util.Constants
import java.util.*
import kotlin.collections.ArrayList

class CardAdapter (val context: Context
                   , val list:ArrayList<CardGetDetailModel>,
                   val lang:ChangeLocale) : RecyclerView.Adapter<CardAdapter.ViewHolder>(){

    var checkposition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardname = itemView.findViewById<TextView>(R.id.cardname)
        val checkboxcard = itemView.findViewById<CheckBox>(R.id.checkboxcard)
        val cardholder = itemView.findViewById<TextView>(R.id.cardholder)
        val expirydate = itemView.findViewById<TextView>(R.id.expirydate)
        val cvvEdt = itemView.findViewById<EditText>(R.id.cvvEdt)
        val deletebtn = itemView.findViewById<ImageView>(R.id.deletebtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_banklist,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.cardname.setText("****-****-****-"+list[position].last4)
        holder.cardholder.text = list[position].name.capitalize(Locale.ROOT)
        holder.expirydate.text = context.getString(R.string.expiry) + " " +list[position].exp_month.toString()+"/"+list[position].exp_year

//        holder.checkboxcard.isChecked = checkposition == position

        holder.checkboxcard.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                checkposition = position
                Constants.IS_CARD_SELECTED = true
            } else {
                checkposition = -1
                Constants.IS_CARD_SELECTED = false
            }
//            checkposition = position
//            if (checkposition != -1) {
//                Constants.IS_CARD_SELECTED = true
//            }

            lang.language(list[position].id)
        }


        holder.deletebtn.setOnClickListener {
            lang.deletcard(list[position].id)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ChangeLocale{
        fun language(language:String)

        fun deletcard(id:String)
    }
}