package com.surpriseme.user.fragments.bookingdetailfragment

data class RateReviewModel(
    val code: Int,
    val `data`: ReviewDataModel,
    val status: Boolean
)

data class ReviewDataModel(
    val message: String
)