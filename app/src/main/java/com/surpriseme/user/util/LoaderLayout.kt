package com.surpriseme.user.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.surpriseme.user.R

class LoaderLayout {
    var loaderWindow: PopupWindow?=null
    fun loaderPopup(context: Context) {

        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popUp: View = layoutInflater.inflate(R.layout.loader_layout, null)
        loaderWindow = PopupWindow(
            popUp, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT, true
        )
        loaderWindow?.showAtLocation(popUp, Gravity.CENTER, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            loaderWindow?.elevation = 10f
        }
        loaderWindow?.isTouchable = false
        loaderWindow?.isOutsideTouchable = false

    }


}