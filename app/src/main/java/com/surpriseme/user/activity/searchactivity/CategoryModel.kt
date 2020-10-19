package com.surpriseme.user.activity.searchactivity

data class CategoryModel(
    val code: Int,
    val `data`: ArrayList<CategoryDataList>,
    val status: Boolean
)

data class CategoryDataList(
    val id: Int,
    val name: String,
    val status: String,
    var isCheck: Boolean
)