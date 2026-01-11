package com.bayazidht.dongshinbuddy.utils

import android.content.Context
import android.content.SharedPreferences
import com.bayazidht.dongshinbuddy.data.repository.AIConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class DSUPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("DSU_PREFS", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_AI_CONFIG = "ai_config"

        private const val KEY_CONTEXT_VERSION = "ai_context_v"
        private const val KEY_CHIPS_VERSION = "chips_data_v"
        private const val KEY_LINKS_VERSION = "quick_links_v"

        private const val KEY_AI_CONTEXT = "ai_context"
        private const val KEY_CHIPS_QUESTIONS = "chips_questions"
        private const val KEY_CHIPS_SUGGESTIONS = "chips_suggestions"
        private const val KEY_QUICK_LINKS = "quick_links_data"
    }

    fun saveAIConfig(config: AIConfig) {
        val json = gson.toJson(config)
        prefs.edit { putString(KEY_AI_CONFIG, json) }
    }
    fun getAIConfig(): AIConfig {
        val json = prefs.getString(KEY_AI_CONFIG, null) ?: return AIConfig()
        return try {
            gson.fromJson(json, AIConfig::class.java)
        } catch (_: Exception) {
            AIConfig()
        }
    }

    fun saveContext(contextData: String, version: Int) {
        prefs.edit {
            putString(KEY_AI_CONTEXT, contextData)
            putInt(KEY_CONTEXT_VERSION, version)
        }
    }
    fun getAiContext(): String = prefs.getString(KEY_AI_CONTEXT, "") ?: ""
    fun getContextVersion(): Int = prefs.getInt(KEY_CONTEXT_VERSION, 0)

    fun saveChipsData(questions: List<String>, suggestions: List<String>, version: Int) {
        prefs.edit {
            putString(KEY_CHIPS_QUESTIONS, gson.toJson(questions))
            putString(KEY_CHIPS_SUGGESTIONS, gson.toJson(suggestions))
            putInt(KEY_CHIPS_VERSION, version)
        }
    }
    fun getSavedQuestions(): List<String> = getListFromJson(KEY_CHIPS_QUESTIONS)
    fun getSavedSuggestions(): List<String> = getListFromJson(KEY_CHIPS_SUGGESTIONS)
    fun getChipsVersion(): Int = prefs.getInt(KEY_CHIPS_VERSION, 0)

    fun saveQuickLinks(links: List<Map<String, String>>, version: Int) {
        val json = gson.toJson(links)
        prefs.edit {
            putString(KEY_QUICK_LINKS, json)
            putInt(KEY_LINKS_VERSION, version)
        }
    }
    fun getSavedQuickLinks(): List<Map<String, String>> {
        val json = prefs.getString(KEY_QUICK_LINKS, null) ?: return emptyList()
        val type = object : TypeToken<List<Map<String, String>>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyList()
        }
    }
    fun getLinksVersion(): Int = prefs.getInt(KEY_LINKS_VERSION, 0)


    private fun getListFromJson(key: String): List<String> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyList()
        }
    }
}