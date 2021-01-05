package com.surpriseme.user.activity.chooselanguage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.util.PrefManger

class ChooseLanguageAdapter(val prefManager:PrefManger,val context: Context
                            , val languageList:ArrayList<LanguageModel>,
val lang:ChangeLocale) : RecyclerView.Adapter<ChooseLanguageAdapter.LanguageViewHolder>(){
    var adpPosition = prefManager.getInt("position")

    class LanguageViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val languageName = itemView.findViewById<MaterialTextView>(R.id.languageNametxt)
        val radioBtn = itemView.findViewById<RadioButton>(R.id.radioBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.choose_language_layout,parent,false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {

        holder.languageName.setText(languageList[position].lang_name)
        holder.radioBtn.isChecked = adpPosition == position

        holder.radioBtn.setOnClickListener {

            adpPosition = holder.adapterPosition
            prefManager.setInt("position",adpPosition)
            notifyDataSetChanged()
            lang.language(languageList[position].lang_code)
        }

    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    interface ChangeLocale{
        fun language(language:String)
    }
}