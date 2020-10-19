package com.surpriseme.user.fragments.changepasswordfragment

data class ChangePasswordModel(
    val code: Int,
    val `data`: ChangePasswordMsgModel,
    val status: Boolean
)

data class ChangePasswordMsgModel(
    val message: String
)