package com.surpriseme.user.fragments.selectdateofbookingfragment

data class CalendarDateListModel(
    val code: Int,
    val `data`: ArrayList<CalendarDateList>,
    val status: Boolean
)

data class CalendarDateList(
    val avail: Int,
    val date: String
)