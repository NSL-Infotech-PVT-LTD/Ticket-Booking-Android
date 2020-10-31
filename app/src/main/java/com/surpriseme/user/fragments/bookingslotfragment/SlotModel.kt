package com.surpriseme.user.fragments.bookingslotfragment

data class SlotModel(
    val code: Int,
    val `data`: ArrayList<SlotDataModel>,
    val status: Boolean
)

data class SlotDataModel(
    var booked: String = "",
    var date: String,
    var hour: String,
    var id: Int,
    var isBooked:Boolean
)