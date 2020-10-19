package com.surpriseme.user.fragments.locationfragment

data class UpdateAddressModel(
    val code: Int,
    val `data`: UpdateMessageModel,
    val status: Boolean
)

data class UpdateMessageModel(
    val Message: String,
    val address: UpdateAddressDataModel
)

data class UpdateAddressDataModel(
    val city: String,
    val country: String,
    val created_at: String,
    val default: String,
    val deleted_at: Any,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val params: Any,
    val state: String,
    val status: String,
    val street_address: String,
    val updated_at: String,
    val user_id: Int,
    val zip: String
)