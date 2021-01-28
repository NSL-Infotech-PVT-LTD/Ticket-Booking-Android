package com.surpriseme.user.activity.termprivacyhelp

data class TermPrivacyHelpModel(
    val code: Int,
    val `data`: TermPrivacyHelpDataModel,
    val status: Boolean
)

data class TermPrivacyHelpDataModel(
    val config: String
)