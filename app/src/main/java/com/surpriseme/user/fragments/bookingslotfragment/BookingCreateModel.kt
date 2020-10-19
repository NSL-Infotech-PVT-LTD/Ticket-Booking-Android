package com.surpriseme.user.fragments.bookingslotfragment

data class BookingCreateModel(
    val code: Int,
    val `data`: BookingCreateMsgModel,
    val status: Boolean
)

data class BookingCreateMsgModel(
    val address: BookingCreateDataModel,
    val message: String
)

data class BookingCreateDataModel(
    val artist_id: String,
    val created_at: String,
    val created_by: Int,
    val date: String,
    val from_time: String,
    val id: Int,
    val to_time: String,
    val type: String,
    val updated_at: String
)