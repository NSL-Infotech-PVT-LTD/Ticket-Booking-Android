package com.surpriseme.user.fragments.googlemapfragment

data class CreateLocationModel( // used to create address....
    val code: Int,
    val `data`: DataCreateLocation,
    val status: Boolean
)

data class DataCreateLocation(
    val address: AddressCreateLocation,
    val message: String
)

data class AddressCreateLocation(
    val city: String,
    val country: String,
    val created_at: String,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val state: String,
    val street_address: String,
    val updated_at: String,
    val user_id: Int,
    val zip: String
)