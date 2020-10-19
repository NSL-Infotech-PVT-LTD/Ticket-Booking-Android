package com.surpriseme.user.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PrefrenceShared(val context: Context) {

    fun setString(key: String, value: String) {

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String {

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        return prefs.getString(key, "").toString()

    }

    fun clearShared() {

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear().apply()
    }

}