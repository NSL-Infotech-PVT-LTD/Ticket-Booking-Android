package com.surpriseme.user.fragments.locationfragment

data class DeleteAddressModel(
    val code: Int,
    val `data`: DeleteAddressData,
    val status: Boolean
)

data class DeleteAddressData(
    val message: String
)