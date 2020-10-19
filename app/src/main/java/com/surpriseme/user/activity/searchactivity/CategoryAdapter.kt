package com.surpriseme.user.activity.searchactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R

class CategoryAdapter(val context: Context, val categoryList:ArrayList<CategoryDataList>,val checklist: CheckList): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>()  {

    var category = ArrayList<CategoryDataList>()
    var professionId = ArrayList<Int>()

    inner class CategoryViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val categoryNameTxt = itemview.findViewById<MaterialTextView>(R.id.categoryNameTxt)
        val categoryCheckbox = itemview.findViewById<CheckBox>(R.id.categoryCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.category_list_layout,parent,false)
        return CategoryViewHolder(view)

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.categoryNameTxt.text = categoryList[position].name
        holder.categoryCheckbox.isChecked = categoryList[position].isCheck
        holder.categoryCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (categoryList[position].id !=null) {
                if (!isChecked) {
                    if (category.size >0) {
                        for (i in 0 until category.size) {
                            if (category[i] == categoryList[position]) {
                                category[i].isCheck == false
                                category.removeAt(i)
                                professionId.removeAt(i)
                                break
                            }
                        }

                    }

                }else {
                    categoryList[position].isCheck = true
                    category.add(categoryList[position])

                    professionId.add(categoryList[position].id)
                }
            }
            checklist.getCheckList(category,professionId)
        }


    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    interface CheckList {
        fun getCheckList(
            categoryName: ArrayList<CategoryDataList>,
            professionId: ArrayList<Int>
        )
    }
}