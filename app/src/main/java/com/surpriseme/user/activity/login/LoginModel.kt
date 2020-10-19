package com.surpriseme.user.activity.login

data class Data(
    val message: String,
    val token: String,
    val user: User
)

data class User(
    val category_id: List<Any>,
    val category_id_details: List<Any>,
    val created_at: String,
    val currency: String,
    val description: String,
    val digital_price_per_hr: Any,
    val email: String,
    val id: Int,
    val image: String,
    val latitude: String,
    val live_price_per_hr: String,
    val location: String,
    val longitude: String,
    val mobile: String,
    val name: String,
    val role: Role,
    val shows_image: List<Any>,
    val shows_image_1: Any,
    val shows_image_2: Any,
    val shows_image_3: Any,
    val shows_image_4: Any,
    val shows_video: Any,
    val social_link_insta: String,
    val social_link_youtube: String,
    val status: String,
    val updated_at: String
)

data class Role(
    val id: Int,
    val name: String,
    val permission: List<Any>
)


