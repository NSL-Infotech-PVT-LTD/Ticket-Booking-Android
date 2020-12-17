package com.surpriseme.user.fragments.homefragment

data class CurrencyListModel(
    val code: Int,
    val `data`: CurrencyDataModel,
    val status: Boolean
)

data class CurrencyDataModel(
    val default: String,
    val list: ArrayList<CurrencyList>
)

data class CurrencyList (
    val currency: String,
    val msp: String
)