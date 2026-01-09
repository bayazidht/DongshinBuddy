package com.bayazidht.dongshinbuddy.utils

import android.content.Context
import android.content.SharedPreferences

class DSUPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("DSU_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CACHED_CONTEXT = "cached_context"
        private const val KEY_CONTEXT_VERSION = "context_version"
    }
    fun saveContext(contextData: String, version: Int) {
        prefs.edit().apply {
            putString(KEY_CACHED_CONTEXT, contextData)
            putInt(KEY_CONTEXT_VERSION, version)
            apply()
        }
    }

    fun getCachedContext(): String = prefs.getString(KEY_CACHED_CONTEXT, "") ?: ""
    fun getLocalVersion(): Int = prefs.getInt(KEY_CONTEXT_VERSION, 0)
}