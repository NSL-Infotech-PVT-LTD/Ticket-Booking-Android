package com.surpriseme.user.util

import android.content.Context
import com.surpriseme.user.R
import java.util.*

object Utility {

    fun randomString(ctx: Context): String {
        val arraystring = arrayOf(
            ctx.getString(R.string.loader1),
            ctx.getString(R.string.loader2),
            ctx.getString(R.string.loader3),
            ctx.getString(R.string.loader4),
            ctx.getString(R.string.loader5),
            ctx.getString(R.string.loader6),
            ctx.getString(R.string.loader7),
            ctx.getString(R.string.loader8),
            ctx.getString(R.string.loader9))
        val min = 0
        val max = 8
        val random = Random().nextInt(max - min + 1) + min
        return arraystring[random]
    }
}