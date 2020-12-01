package com.surpriseme.user.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PrefrenceShared(val context: Context) {


    fun setString(key1: String?, value1: String?) {
        val prefs1 = context?.getSharedPreferences("abc", Context.MODE_PRIVATE)
//        val prefs1 = PreferenceManager.getDefaultSharedPreferences(context)
        val editor1 = prefs1?.edit()
        editor1?.putString(key1, value1)
        editor1?.apply()
    }

    fun getString(key1: String): String {
        val prefs1 = context?.getSharedPreferences("abc", Context.MODE_PRIVATE)
        return prefs1?.getString(key1, "")!!
    }


    fun setBoolean(key1: String?, value1: Boolean) {
        val prefs1 = context?.getSharedPreferences("abc", Context.MODE_PRIVATE)
        val editor1 = prefs1?.edit()
        editor1?.putBoolean(key1, value1)
        editor1?.apply()
    }

    fun getBoolean(key1: String?): Boolean {
        val prefs1 = context?.getSharedPreferences("abc", Context.MODE_PRIVATE)
        return prefs1?.getBoolean(key1, false)!!
    }

    fun clearShared() {
        val prefs1 = context?.getSharedPreferences("abc", Context.MODE_PRIVATE)

        val editor1 = prefs1?.edit()
        editor1?.clear()?.apply()
    }
}