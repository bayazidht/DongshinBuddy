package com.bayazidht.dongshinbuddy.data.repository

data class AIConfig(
    val primaryAi: String = "Gemini",
    val geminiModel: String = "gemini-2.5-flash",
    val groqModel: String = "llama-3.1-8b-instant",
    val underMaintenance: Boolean = false
)