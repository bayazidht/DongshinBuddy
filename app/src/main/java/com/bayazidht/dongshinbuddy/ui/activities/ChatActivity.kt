package com.bayazidht.dongshinbuddy.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.isVisible
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.adapter.ChatAdapter
import com.bayazidht.dongshinbuddy.api.RetrofitClient
import com.bayazidht.dongshinbuddy.utils.DSUPrefs
import com.bayazidht.dongshinbuddy.data.repository.AppRepository
import com.bayazidht.dongshinbuddy.model.ChatMessage
import com.bayazidht.dongshinbuddy.viewmodel.ChatViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import com.bayazidht.dongshinbuddy.data.local.ChipsData
import com.bayazidht.dongshinbuddy.data.local.ContextData
import com.bayazidht.dongshinbuddy.databinding.ActivityChatBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        val localConfig = dsuPrefs.getAIConfig()
        viewModel.aiConfig = localConfig

        setupFinalContext()
        setupChatRecyclerView()
        setupClickListeners()
        setupSuggestionChips()

        if (localConfig.underMaintenance) {
            showMaintenanceUI()
        } else {
            openAutoQuery()
        }
    }

    private fun showMaintenanceUI() {
        runOnUiThread {
            binding.messageInput.isEnabled = false
            binding.fabSend.isEnabled = false
            binding.suggestionChipGroup.visibility = View.GONE

            binding.messageInput.hint = "Under maintenance..."

            MaterialAlertDialogBuilder(this)
                .setTitle("Maintenance Mode")
                .setMessage("We are updating our AI systems. Please try again later.")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }

    private fun openAutoQuery() {
        val autoQuery = intent.getStringExtra("PREFILLED_QUERY")?:""
        if (autoQuery.isNotEmpty()) {
            sendMessage(autoQuery)
        } else {
            binding.messageInput.requestFocus()
        }
    }

    private fun setupFinalContext() {
        val savedContext = dsuPrefs.getAiContext()
        viewModel.finalContext = savedContext.ifEmpty {
            ContextData.DEFAULT_CONTEXT
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
        updateScroll(userPos, false)

        viewModel.sendMessage { position, isAnimating ->
            runOnUiThread {
                chatAdapter.notifyItemChanged(position)

                if (!isAnimating) {
                    updateScroll(position, true)
                } else {
                    updateScroll(position, false)
                }
            }
        }
    }

    private fun updateScroll(position: Int, isSmooth: Boolean) {
        binding.chatRecyclerView.post {
            if (isSmooth) {
                binding.chatRecyclerView.smoothScrollToPosition(position)
            } else {
                binding.chatRecyclerView.scrollToPosition(position)
            }
        }
    }
}