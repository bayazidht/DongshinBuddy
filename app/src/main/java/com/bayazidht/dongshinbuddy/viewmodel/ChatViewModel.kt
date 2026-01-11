package com.bayazidht.dongshinbuddy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayazidht.dongshinbuddy.BuildConfig
import com.bayazidht.dongshinbuddy.data.repository.AIConfig
import com.bayazidht.dongshinbuddy.data.repository.AppRepository
import com.bayazidht.dongshinbuddy.model.*
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: AppRepository) : ViewModel() {

    val messages = mutableListOf<ChatMessage>()
    var finalContext: String = "Please wait, initializing assistant..."
    var aiConfig: AIConfig = AIConfig()
    private val animationDelay = 10L

    fun sendMessage(onResponse: (Int, Boolean) -> Unit) {
        val userQuery = messages.last().message
        val thinkingMessage = ChatMessage("Thinking...", false)
        messages.add(thinkingMessage)
        val aiPos = messages.size - 1
        onResponse(aiPos, false)

        viewModelScope.launch {
            var responseText: String
            try {
                responseText = if (aiConfig.primaryAi == "Gemini") {
                    callGemini(userQuery)
                } else {
                    callGroq(userQuery)
                }

            } catch (e: Exception) {
                Log.e("ChatError", "Primary AI failed: ${e.message}")
                try {
                    responseText = if (aiConfig.primaryAi == "Gemini") {
                        Log.d("ChatFallback", "Switching to Groq...")
                        callGroq(userQuery)
                    } else {
                        Log.d("ChatFallback", "Switching to Gemini...")
                        callGemini(userQuery)
                    }
                } catch (e: Exception) {
                    Log.e("ChatError", "Both AI models failed: ${e.message}")
                    responseText = "Error: AI services are currently unavailable. Please check your connection."
                }
            }
            animateText(responseText, aiPos, onResponse)
        }
    }

    private suspend fun callGemini(query: String): String {
        val geminiModel = GenerativeModel(
            modelName = aiConfig.geminiModel,
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content { text(finalContext) }
        )

        val historyList = mutableListOf<com.google.ai.client.generativeai.type.Content>()
        val chatHistory = if (messages.size > 10) messages.takeLast(11) else messages
        chatHistory.forEach { msg ->
            if (msg.message != "Thinking..." && msg.message != query) {
                val roleName = if (msg.isUser) "user" else "model"
                historyList.add(content(roleName) { text(msg.message) })
            }
        }

        val chatSession = geminiModel.startChat(history = historyList)
        val response = chatSession.sendMessage(query)
        return response.text ?: "No response from Gemini"
    }

    private suspend fun callGroq(query: String): String {
        val apiKey = "bearer ${BuildConfig.GROQ_API_KEY}"
        val groqMessages = mutableListOf<GroqMessage>().apply {
            add(GroqMessage(role = "system", content = finalContext))

            val historyCount = if (messages.size > 10) 10 else messages.size
            val chatHistory = messages.takeLast(historyCount)
            chatHistory.forEach {
                if (it.message != "Thinking..." && it.message != query) {
                    add(GroqMessage(role = if (it.isUser) "user" else "assistant", content = it.message))
                }
            }
            add(GroqMessage(role = "user", content = query))
        }

        val request = GroqRequest(model = aiConfig.groqModel, messages = groqMessages)
        val response = repository.getGroqResponse(apiKey, request)
        return response.choices[0].message.content
    }

    private suspend fun animateText(fullText: String, position: Int, onResponse: (Int, Boolean) -> Unit) {
        val sb = StringBuilder()
        if (position < 0 || position >= messages.size) return
        fullText.forEach { char ->
            sb.append(char)
            messages[position] = ChatMessage(sb.toString(), false)
            onResponse(position, true)
            delay(animationDelay)
        }
    }
}