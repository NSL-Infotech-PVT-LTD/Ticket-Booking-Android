package com.surpriseme.user.fragments.reviewfragment

data class ReviewModel(
    val code: Int,
    val `data`: ArrayList<ReviewDataModel>,
    val status: Boolean
)

data class ReviewDataModel(
    val artist_id: Int,
    val avg_rate: Int,
    val booking_id: Int,
    val created_at: String,
    val created_by: Int,
    val customer_detail: ReviewCustomerDetail,
    val deleted_at: Any,
    val id: Int,
    val params: Any,
    val rate: String,
    val review: String,
    val status: String,
    val updated_at: String,
    val updated_by: Int
)

data class ReviewCustomerDetail(
    val category_id_details: List<Any>,
    val currency: Any,
    val id: Int,
    val image: String,
    val name: String,
    val role: ReviewRole,
    val shows_image: List<Any>
)

data class ReviewRole(
    val id: Int,
    val name: String,
    val permission: List<Any>
)