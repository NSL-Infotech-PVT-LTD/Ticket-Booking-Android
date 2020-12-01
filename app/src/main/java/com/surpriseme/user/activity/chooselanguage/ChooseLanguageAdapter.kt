package com.surpriseme.user.activity.chooselanguage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R

class ChooseLanguageAdapter(val context: Context
                            , val languageList:ArrayList<LanguageModel>,
val lang:ChangeLocale) : RecyclerView.Adapter<ChooseLanguageAdapter.LanguageViewHolder>(){

    class LanguageViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val languageName = itemView.findViewById<MaterialTextView>(R.id.languageNametxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.choose_language_layout,parent,false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {

        holder.languageName.setText(languageList[position].lang_name)

        holder.itemView.setOnClickListener {
//            lang.language(languageList[position].lang_code)
        }

    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    interface ChangeLocale{
        fun language(language:String)
    }
}