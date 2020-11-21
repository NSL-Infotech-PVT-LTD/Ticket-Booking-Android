package com.surpriseme.user.data.model

data class DeleteCard(
    val code: Int,
    val `data`: DeleteData,
    val status: Boolean
)

data class DeleteData(
    val `data`: List<Any>,
    val message: String
)