package com.surpriseme.user.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PrefManger(context: Context) {

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var _context: Context? = null

    // shared pref mode
    var PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME = "welcome"
    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

   init {
        _context = context
        pref = _context?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref?.edit()
        editor?.apply()
    }

    fun setString1(key: String?, value: String?) {

        val editor = pref?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun getString1(key: String?): String? {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(_context)
        return pref?.getString(key, "")
    }
    fun setBoolean1(key: String?, value: Boolean) {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(_context)
        val editor = pref?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun getBoolean1(key: String?): Boolean {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(_context)
        return pref?.getBoolean(key, false)!!
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor?.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
        editor?.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref?.getBoolean(IS_FIRST_TIME_LAUNCH, true)!!
    }
}