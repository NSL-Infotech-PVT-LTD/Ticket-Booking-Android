package com.surpriseme.user.fragments.viewprofile

data class ViewProfileModel(
    val code: Int,
    val `data`: DataViewProfile,
    val status: Boolean
)

data class DataViewProfile(
    val user: UserViewProfile
)

data class UserViewProfile(
    val category_id: List<Any>,
    val category_id_details: List<Any>,
    var currency: Any,
    val description: Any,
    val digital_price_per_hr: Any,
    val email: String,
    val id: Int,
    val image: Any,
    val latitude: Any,
    val live_price_per_hr: Any,
    val location: Any,
    val longitude: Any,
    val mobile: Any,
    val name: String,
    val role: RoleViewProfile,
    val shows_image: List<Any>,
    val shows_image_1: Any,
    val shows_image_2: Any,
    val shows_image_3: Any,
    val shows_image_4: Any,
    val shows_video: Any,
    val social_link_insta: Any,
    val social_link_youtube: Any,
    val status: String
)

data class RoleViewProfile(
    val id: Int,
    val name: String,
    val permission: List<Any>
)