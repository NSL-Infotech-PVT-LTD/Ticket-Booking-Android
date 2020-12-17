package com.surpriseme.user.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textview.MaterialTextView
import com.surpriseme.user.R
import com.surpriseme.user.activity.login.LoginActivity

class InvalidAuth {

    fun invalidAuth(activity: Activity, responseCode: Int, isInvalidAuth: Boolean) {

        if (responseCode == 401) {

            val layoutInflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popup: View = layoutInflater.inflate(R.layout.alert_popup_layout, null)
            val invalidAuthWindow = PopupWindow(
                popup,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                true
            )
            invalidAuthWindow.showAtLocation(popup, Gravity.CENTER, 0, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                invalidAuthWindow.elevation = 10f
            }
            invalidAuthWindow.isTouchable = false
            invalidAuthWindow.isOutsideTouchable = false

            val ok: MaterialTextView = popup.findViewById(R.id.okTv)
            val congratsMsgTv: MaterialTextView = popup.findViewById(R.id.congratsMsgTv)
            congratsMsgTv.text = Constants.SOMETHING_WENT_WRONG

            ok.setOnClickListener {
                if (isInvalidAuth) {
                    invalidAuthWindow.dismiss()
                    val intent = Intent(activity, LoginActivity::class.java)
                    activity.startActivity(intent)
                    activity.finishAffinity()
                } else {
                    invalidAuthWindow.dismiss()
                }
            }

        }
    }

}