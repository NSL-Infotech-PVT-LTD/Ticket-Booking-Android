package com.surpriseme.user.data.model

data class UpdateProfileModel(
    val code: Int,
    val `data`: DataUpdateProfile,
    val status: Boolean
)

data class DataUpdateProfile(
    val message: String,
    val user: UserUpdateProfile
)

data class UserUpdateProfile(
    val category_id_details: List<Any>,
    val email: String,
    val image: Any,
    val name: String,
    val role: List<Any>,
    val shows_image: List<Any>,
    val status: String
)