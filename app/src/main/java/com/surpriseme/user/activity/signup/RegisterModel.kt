package com.surpriseme.user.activity.signup

data class RegisterModel(
    val code: Int,
    val `data`: Data,
    val status: Boolean
)

data class Data(
    val message: String,
    val token: String,
    val user: User
)

data class User(
    val category_id_details: List<Any>,
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val role: Role,
    val shows_image: List<Any>,
    val updated_at: String
)

data class Role(
    val id: Int,
    val name: String,
    val permission: List<Any>
)