package com.surpriseme.user.util

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
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

    // popup will display to select currency.
    fun alertErrorMessage(ctx: Context, message:String) {

        val layoutInflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.popup_error_alert_layout, null)
        val errorAlertWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        errorAlertWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            errorAlertWindow.elevation = 10f
        }
        errorAlertWindow.isTouchable = false
        errorAlertWindow.isOutsideTouchable = false

        val messageDispText = popUp.findViewById<MaterialTextView>(R.id.messageDispText)
        val doneTv = popUp.findViewById<MaterialTextView>(R.id.doneTv)
        messageDispText.text = message
        doneTv.setOnClickListener{
            errorAlertWindow.dismiss()
        }

    }
}