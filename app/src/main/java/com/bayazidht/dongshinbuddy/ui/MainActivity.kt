package com.bayazidht.dongshinbuddy.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.dongshinbuddy.adapter.ChatAdapter
import com.bayazidht.dongshinbuddy.api.RetrofitClient
import com.bayazidht.dongshinbuddy.data.local.DSUPrefs
import com.bayazidht.dongshinbuddy.data.repository.ChatRepository
import com.bayazidht.dongshinbuddy.databinding.ActivityMainBinding
import com.bayazidht.dongshinbuddy.model.ChatMessage
import com.bayazidht.dongshinbuddy.viewmodel.ChatViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel
    private lateinit var dsuPrefs: DSUPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemBars()

        dsuPrefs = DSUPrefs(this)
        val repository = ChatRepository(RetrofitClient.groqService, FirebaseFirestore.getInstance())
        viewModel = ChatViewModel(repository)

        viewModel.finalContext = dsuPrefs.getCachedContext()

        setupChatRecyclerView()
        setupClickListeners()

        checkForDataUpdates(repository)

        val autoQuery = intent.getStringExtra("PREFILLED_QUERY")
        if (autoQuery != null) {
            sendMessage(autoQuery)
        }
    }

    private fun setupChatRecyclerView() {
        chatAdapter = ChatAdapter(viewModel.messages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.chatRecyclerView.adapter = chatAdapter
    }

    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            val userText = binding.messageInput.text.toString().trim()
            if (userText.isNotBlank()) {
                sendMessage(userText)
            }
        }
    }

    private fun sendMessage(query: String) {
        val userMsg = ChatMessage(query, true)
        viewModel.messages.add(userMsg)
        chatAdapter.notifyItemInserted(viewModel.messages.size - 1)
        binding.messageInput.text.clear()
        binding.chatRecyclerView.smoothScrollToPosition(viewModel.messages.size - 1)

        viewModel.sendMessage { position ->
            runOnUiThread {
                if (position == -1) {
                    chatAdapter.notifyItemInserted(viewModel.messages.size - 1)
                } else {
                    chatAdapter.notifyItemChanged(position)
                }
                binding.chatRecyclerView.smoothScrollToPosition(viewModel.messages.size - 1)
            }
        }
    }

    private fun checkForDataUpdates(repository: ChatRepository) {
        repository.fetchUniversityInfo(
            onSuccess = { context, version ->
                if (version > dsuPrefs.getLocalVersion()) {
                    viewModel.finalContext = context
                    dsuPrefs.saveContext(context, version)
                    Log.d("UpdateCheck", "New version $version applied.")
                }
            },
            onFailure = { error ->
                Log.e("UpdateCheck", "Failed: ${error.message}")
            }
        )
    }

    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}