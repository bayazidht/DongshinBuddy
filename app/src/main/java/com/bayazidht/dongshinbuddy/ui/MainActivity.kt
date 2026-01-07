package com.bayazidht.dongshinbuddy.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.dongshinbuddy.BuildConfig
import com.bayazidht.dongshinbuddy.model.ChatMessage
import com.bayazidht.dongshinbuddy.model.GroqMessage
import com.bayazidht.dongshinbuddy.model.GroqRequest
import com.bayazidht.dongshinbuddy.adapter.ChatAdapter
import com.bayazidht.dongshinbuddy.api.GroqService
import com.bayazidht.dongshinbuddy.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private val db = FirebaseFirestore.getInstance()
    private var finalContext: String = "Please wait, initializing assistant..."

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

        fetchFirestoreData()
        setupChatRecyclerView()
        setupClickListeners()
    }

    private fun fetchFirestoreData() {
        val sharedPrefs = getSharedPreferences("DSU_PREFS", MODE_PRIVATE)
        val localVersion = sharedPrefs.getInt("context_version", 0)

        db.collection("dongshin_buddy")
            .document("university_info")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val remoteVersion = document.getLong("version")?.toInt() ?: 0
                    val remoteContext = document.getString("context") ?: ""

                    Log.d("FirestoreData", "Local: $localVersion, Remote: $remoteVersion")
                    if (remoteVersion > localVersion) {
                        finalContext = remoteContext

                        sharedPrefs.edit().apply {
                            putString("cached_context", remoteContext)
                            putInt("context_version", remoteVersion)
                            apply()
                        }
                        Log.d("FirestoreData", "New Version $remoteVersion Found. Data Updated!")
                    } else {
                        finalContext = sharedPrefs.getString("cached_context", "") ?: ""
                        Log.d("FirestoreData", "Version is up to date ($localVersion). Using Cache.")
                    }
                }
            }
            .addOnFailureListener {
                finalContext = sharedPrefs.getString("cached_context", "University info is currently unavailable. Please check your internet.") ?: ""
            }
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

                sendMessageToGroq()
            }
        }
    }

    private fun sendMessageToGroq() {
        val apiKey = "bearer ${BuildConfig.GROQ_API_KEY}"

        val thinkingMessage = ChatMessage("Thinking...", false)
        addMessage(thinkingMessage)
        val thinkingPosition = messages.size - 1

        val groqMessages = mutableListOf<GroqMessage>()
        groqMessages.add(GroqMessage(role = "system", content = finalContext))

        val chatHistory = if (messages.size > 5) messages.takeLast(4) else messages
        for (i in 0 until chatHistory.size - 1) {
            val role = if (chatHistory[i].isUser) "user" else "assistant"
            groqMessages.add(GroqMessage(role = role, content = chatHistory[i].message))
        }

        val request = GroqRequest(
            model = "llama-3.1-8b-instant",
            messages = groqMessages
        )

        lifecycleScope.launch {
            try {
                val response = groqService.getCompletion(apiKey, request)
                val aiResponse = response.choices[0].message.content

                runOnUiThread {
                    messages[thinkingPosition] = ChatMessage(aiResponse, false)
                    chatAdapter.notifyItemChanged(thinkingPosition)
                    binding.chatRecyclerView.smoothScrollToPosition(messages.size - 1)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    messages[thinkingPosition] = ChatMessage("Error: ${e.message}", false)
                    chatAdapter.notifyItemChanged(thinkingPosition)
                }
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