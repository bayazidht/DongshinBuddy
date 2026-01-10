package com.bayazidht.dongshinbuddy.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.adapter.ChatAdapter
import com.bayazidht.dongshinbuddy.api.RetrofitClient
import com.bayazidht.dongshinbuddy.utils.DSUPrefs
import com.bayazidht.dongshinbuddy.data.repository.AppRepository
import com.bayazidht.dongshinbuddy.model.ChatMessage
import com.bayazidht.dongshinbuddy.viewmodel.ChatViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.view.isVisible
import com.bayazidht.dongshinbuddy.data.local.ChipsData
import com.bayazidht.dongshinbuddy.data.local.ContextData
import com.bayazidht.dongshinbuddy.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel
    private lateinit var dsuPrefs: DSUPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        dsuPrefs = DSUPrefs(this)
        val repository = AppRepository(RetrofitClient.groqService, FirebaseFirestore.getInstance())
        viewModel = ChatViewModel(repository)

        setupFinalContext()
        setupChatRecyclerView()
        setupClickListeners()
        setupSuggestionChips()

        val autoQuery = intent.getStringExtra("PREFILLED_QUERY")
        if (autoQuery != null) {
            sendMessage(autoQuery)
        } else {
            binding.messageInput.requestFocus()
        }
    }

    private fun setupFinalContext() {
        val savedContext = dsuPrefs.getCachedContext()
        if (savedContext.isNotEmpty()) {
            viewModel.finalContext = savedContext
        } else {
            viewModel.finalContext = ContextData.DEFAULT_CONTEXT
        }
    }

    private fun setupSuggestionChips() {
        val chipGroup = binding.suggestionChipGroup
        val suggestionsList = dsuPrefs.getSavedSuggestions().ifEmpty { ChipsData.suggestions }
        chipGroup.removeAllViews()
        suggestionsList.forEach { query ->
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = query
            chip.setOnClickListener {
                sendMessage(query)
            }
            chipGroup.addView(chip)
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
        binding.fabSend.setOnClickListener {
            val userText = binding.messageInput.text.toString().trim()
            if (userText.isNotBlank()) {
                sendMessage(userText)
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun sendMessage(query: String) {
        if (binding.suggestionChipGroup.isVisible) {
            binding.suggestionChipGroup.visibility = View.GONE
        }

        val userMsg = ChatMessage(query, true)
        viewModel.messages.add(userMsg)

        val userPos = viewModel.messages.size - 1
        chatAdapter.notifyItemInserted(userPos)
        binding.messageInput.text.clear()
        updateScroll(userPos)

        viewModel.sendMessage { position ->
            runOnUiThread {
                val targetPos =viewModel.messages.size
                if (position == -1) {
                    chatAdapter.notifyItemInserted(targetPos)
                } else {
                    chatAdapter.notifyItemChanged(position)
                }
                updateScroll(targetPos)
            }
        }
    }
    private fun updateScroll(position: Int) {
        binding.chatRecyclerView.post {
            binding.chatRecyclerView.smoothScrollToPosition(position)
        }
    }
}