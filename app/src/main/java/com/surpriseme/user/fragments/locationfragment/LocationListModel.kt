package com.surpriseme.user.fragments.locationfragment

import java.io.Serializable

data class LocationListModel(
    val code: Int,
    val `data`: ArrayList<LocationDataList>,
    val status: Boolean
)

data class LocationDataList(
    val city: String,
    val country: String,
    val default: String,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val state: String,
    val street_address: String,
    val user_id: Int,
    val zip: String
) : Serializable