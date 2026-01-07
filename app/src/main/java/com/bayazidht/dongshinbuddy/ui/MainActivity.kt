package com.bayazidht.dongshinbuddy.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.dongshinbuddy.BuildConfig
import com.bayazidht.dongshinbuddy.ChatMessage
import com.bayazidht.dongshinbuddy.GroqMessage
import com.bayazidht.dongshinbuddy.GroqRequest
import com.bayazidht.dongshinbuddy.adapter.ChatAdapter
import com.bayazidht.dongshinbuddy.api.GroqService
import com.bayazidht.dongshinbuddy.data.UniversityData
import com.bayazidht.dongshinbuddy.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private val groqService: GroqService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupChatRecyclerView()
        setupClickListeners()
    }

    private fun setupChatRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.chatRecyclerView.layoutManager = layoutManager
        binding.chatRecyclerView.adapter = chatAdapter
    }

    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            val userText = binding.messageInput.text.toString().trim()
            if (userText.isNotBlank()) {
                addMessage(ChatMessage(userText, true))
                binding.messageInput.text.clear()

                sendMessageToGroq(userText)
            }
        }
    }

    private fun sendMessageToGroq(userQuery: String) {
        val apiKey = "bearer ${BuildConfig.GROQ_API_KEY}"

        val systemPrompt = "You are DongshinBuddy, an assistant for Dongshin University. Use this context to answer: ${UniversityData.dsuContext}"

        val request = GroqRequest(
            model = "llama-3.3-70b-versatile",
            messages = listOf(
                GroqMessage(role = "system", content = systemPrompt),
                GroqMessage(role = "user", content = userQuery)
            )
        )

        lifecycleScope.launch {
            try {
                val response = groqService.getCompletion(apiKey, request)
                val aiResponse = response.choices[0].message.content
                addMessage(ChatMessage(aiResponse, false))
            } catch (e: Exception) {
                addMessage(ChatMessage("Error: ${e.message}", false))
                Log.e("GroqAPI", "Error: ${e.message}")
            }
        }
    }

    private fun addMessage(chatMessage: ChatMessage) {
        runOnUiThread {
            messages.add(chatMessage)
            chatAdapter.notifyItemInserted(messages.size - 1)
            binding.chatRecyclerView.smoothScrollToPosition(messages.size - 1)
        }
    }
}