package com.bayazidht.dongshinbuddy.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DSUPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("DSU_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CACHED_CONTEXT = "cached_context"
        private const val KEY_CONTEXT_VERSION = "context_version"

        private const val KEY_CHIPS_QUESTIONS = "chips_questions"
        private const val KEY_CHIPS_SUGGESTIONS = "chips_suggestions"
        private const val KEY_CHIPS_VERSION = "chips_version"

        private const val KEY_QUICK_LINKS = "quick_links_data"
        private const val KEY_LINKS_VERSION = "links_version"
    }
    fun saveContext(contextData: String, version: Int) {
        prefs.edit().apply {
            putString(KEY_CACHED_CONTEXT, contextData)
            putInt(KEY_CONTEXT_VERSION, version)
            apply()
        }
    }
    fun saveChipsData(questions: List<String>, suggestions: List<String>, version: Int) {
        prefs.edit().apply {
            putStringSet(KEY_CHIPS_QUESTIONS, questions.toSet())
            putStringSet(KEY_CHIPS_SUGGESTIONS, suggestions.toSet())
            putInt(KEY_CHIPS_VERSION, version)
            apply()
        }
    }
    fun saveQuickLinks(links: List<Map<String, String>>, version: Int) {
        val gson = Gson()
        val json = gson.toJson(links)
        prefs.edit().apply {
            putString(KEY_QUICK_LINKS, json)
            putInt(KEY_LINKS_VERSION, version)
            apply()
        }
    }

    fun getCachedContext(): String = prefs.getString(KEY_CACHED_CONTEXT, "") ?: ""
    fun getLocalVersion(): Int = prefs.getInt(KEY_CONTEXT_VERSION, 0)

    fun getSavedQuestions(): List<String> = prefs.getStringSet(KEY_CHIPS_QUESTIONS, emptySet())?.toList() ?: emptyList()
    fun getSavedSuggestions(): List<String> = prefs.getStringSet(KEY_CHIPS_SUGGESTIONS, emptySet())?.toList() ?: emptyList()
    fun getChipsVersion(): Int = prefs.getInt(KEY_CHIPS_VERSION, 0)

    fun getSavedQuickLinks(): List<Map<String, String>> {
        val json = prefs.getString(KEY_QUICK_LINKS, null) ?: return emptyList()
        val type = object : TypeToken<List<Map<String, String>>>() {}.type
        return try {
            Gson().fromJson(json, type)
        } catch (_: Exception) {
            emptyList()
        }
    }
    fun getLinksVersion(): Int = prefs.getInt(KEY_LINKS_VERSION, 0)
}