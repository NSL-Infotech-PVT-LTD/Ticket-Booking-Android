package com.surpriseme.user.fragments.notificationfragment

data class NotificationStatusModel(
    val code: Int,
    val `data`: NotificationUserModel,
    val status: Boolean
)

data class NotificationUserModel(
    val message: String,
    val user: User
)

data class User(
    val category_id_details: List<Any>,
    val email: String,
    val id: Int,
    val is_notify: String,
    val name: String,
    val role: Role,
    val shows_image: List<Any>
)

data class Role(
    val id: Int,
    val name: String,
    val permission: List<Any>
)