package com.bayazidht.dongshinbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayazidht.dongshinbuddy.BuildConfig
import com.bayazidht.dongshinbuddy.data.repository.ChatRepository
import com.bayazidht.dongshinbuddy.model.*
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    val messages = mutableListOf<ChatMessage>()
    var finalContext: String = "Please wait, initializing assistant..."

    fun sendMessage(onResponse: (Int) -> Unit) {
        val apiKey = "bearer ${BuildConfig.GROQ_API_KEY}"

        val thinkingMessage = ChatMessage("Thinking...", false)
        messages.add(thinkingMessage)
        val thinkingPosition = messages.size - 1
        onResponse(-1)

        val groqMessages = mutableListOf<GroqMessage>()
        groqMessages.add(GroqMessage(role = "system", content = finalContext))

        val chatHistory = if (messages.size > 5) messages.takeLast(5) else messages
        for (i in 0 until chatHistory.size - 1) {
            val role = if (chatHistory[i].isUser) "user" else "assistant"
            groqMessages.add(GroqMessage(role = role, content = chatHistory[i].message))
        }

        val request = GroqRequest(model = "llama-3.1-8b-instant", messages = groqMessages)

        viewModelScope.launch {
            try {
                val response = repository.getGroqResponse(apiKey, request)
                val aiResponse = response.choices[0].message.content
                messages[thinkingPosition] = ChatMessage(aiResponse, false)
                onResponse(thinkingPosition)
            } catch (e: Exception) {
                messages[thinkingPosition] = ChatMessage("Error: ${e.message}", false)
                onResponse(thinkingPosition)
            }
        }
    }
}