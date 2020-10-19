package com.surpriseme.user.activity.forgotpassword

data class ResetPasswordModel (


    val status: String,
    val code: Int,
    val data: DataResetPassword
)
data class DataResetPassword(
    val message: String
)