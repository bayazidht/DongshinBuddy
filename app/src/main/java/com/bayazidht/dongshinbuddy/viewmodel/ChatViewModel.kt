package com.bayazidht.dongshinbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayazidht.dongshinbuddy.BuildConfig
import com.bayazidht.dongshinbuddy.data.repository.AppRepository
import com.bayazidht.dongshinbuddy.model.*
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: AppRepository) : ViewModel() {

    val messages = mutableListOf<ChatMessage>()
    var finalContext: String = "Please wait, initializing assistant..."

    private val geminiModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content { text(finalContext) }
        )
    }

    private var geminiChatSession = geminiModel.startChat()

    fun sendMessage(onResponse: (Int) -> Unit) {
        val userQuery = messages.last().message
        val thinkingMessage = ChatMessage("Thinking...", false)
        messages.add(thinkingMessage)
        val thinkingPosition = messages.size - 1
        onResponse(-1)

        viewModelScope.launch {
            try {
                val response = geminiChatSession.sendMessage(userQuery)
                val aiResponse = response.text ?: "No response"

                messages[thinkingPosition] = ChatMessage(aiResponse, false)
                onResponse(thinkingPosition)
            } catch (_: Exception) {
                callGroqFallback(userQuery, thinkingPosition, onResponse)
            }
        }
    }

    private suspend fun callGroqFallback(query: String, position: Int, onResponse: (Int) -> Unit) {
        val apiKey = "bearer ${BuildConfig.GROQ_API_KEY}"
        val groqMessages = mutableListOf<GroqMessage>()
        groqMessages.add(GroqMessage(role = "system", content = finalContext))

        val chatHistory = if (messages.size > 10) messages.takeLast(10) else messages

        for (i in 0 until chatHistory.size - 2) {
            val role = if (chatHistory[i].isUser) "user" else "assistant"
            if (chatHistory[i].message != "Thinking...") {
                groqMessages.add(GroqMessage(role = role, content = chatHistory[i].message))
            }
        }
        groqMessages.add(GroqMessage(role = "user", content = query))

        val request = GroqRequest(model = "llama-3.1-8b-instant", messages = groqMessages)

        try {
            val response = repository.getGroqResponse(apiKey, request)
            val aiResponse = response.choices[0].message.content
            messages[position] = ChatMessage(aiResponse, false)
            onResponse(position)
        } catch (_: Exception) {
            messages[position] = ChatMessage("Error: Please check your connection.", false)
            onResponse(position)
        }
    }
}