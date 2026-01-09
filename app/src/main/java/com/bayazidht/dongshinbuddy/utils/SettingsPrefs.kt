package com.bayazidht.dongshinbuddy.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsPrefs (context: Context){
    private val preferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DARK_MODE = "DarkMode"
    }

    var isDarkMode: Boolean
        get() = preferences.getBoolean(KEY_DARK_MODE, false)
        set(value) = preferences.edit().putBoolean(KEY_DARK_MODE, value).apply()
}