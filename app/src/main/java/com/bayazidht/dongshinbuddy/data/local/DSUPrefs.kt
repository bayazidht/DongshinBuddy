package com.bayazidht.dongshinbuddy.data.local

import android.content.Context
import android.content.SharedPreferences

class DSUPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DSU_PREFS", Context.MODE_PRIVATE)

    fun saveContext(contextData: String, version: Int) {
        prefs.edit().apply {
            putString("cached_context", contextData)
            putInt("context_version", version)
            apply()
        }
    }

    fun getCachedContext(): String = prefs.getString("cached_context", "") ?: ""
    fun getLocalVersion(): Int = prefs.getInt("context_version", 0)
}