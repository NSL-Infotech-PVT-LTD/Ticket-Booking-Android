package com.surpriseme.user.util

import java.util.*

object Utility {

    fun randomString(): String {
        val arraystring = arrayOf(
            "Musicians want to be the loud voice for so many quiet hearts",
            "Music is the universal language of mankind",
            "If Music is a Place — then Jazz is the City",
            "Rock is the Road, Classical is a Temple",
            "Music is the shorthand of emotion.",
            "Music is the only language in which you cannot say a mean or sarcastic thing",
            "Music is the art which is most nigh to tears and memory.",
            "One good thing about music, when it hits you, you feel no pain",
            "Where words fail, music speaks"
        )
        val min = 0
        val max = 8
        val random = Random().nextInt(max - min + 1) + min
        return arraystring[random]
    }
}