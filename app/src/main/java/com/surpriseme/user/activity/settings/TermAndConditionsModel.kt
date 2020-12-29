package com.surpriseme.user.activity.settings

data class TermAndConditionsModel(
    val code: Int,
    val `data`: Data,
    val status: Boolean
)

data class Data(
    val config: String
)