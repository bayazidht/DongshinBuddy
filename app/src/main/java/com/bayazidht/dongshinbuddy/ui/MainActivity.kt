package com.bayazidht.dongshinbuddy.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.adapter.ChatAdapter
import com.bayazidht.dongshinbuddy.api.RetrofitClient
import com.bayazidht.dongshinbuddy.data.local.DSUPrefs
import com.bayazidht.dongshinbuddy.data.repository.ChatRepository
import com.bayazidht.dongshinbuddy.databinding.ActivityMainBinding
import com.bayazidht.dongshinbuddy.model.ChatMessage
import com.bayazidht.dongshinbuddy.viewmodel.ChatViewModel
import com.google.android.material.chip.Chip
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
            binding.suggestionChipGroup.visibility = View.GONE
            sendMessage(autoQuery)
        }

        setupSuggestionChips()
    }

    private fun setupSuggestionChips() {
        val suggestions = listOf(
            "Campus Map",
            "Dormitory Rules",
            "Today's Menu",
            "Daiso Location",
            "Library Hours",
            "International Building",
            "How to pay tuition fee?",
            "Bus Schedule",
            "Nearby Halal Food",
            "Scholarship Info",
            "Office of International Affairs",
            "Gym and Fitness Center",
            "Internet/Wi-Fi Setup",
            "Student ID card",
            "Central Library"
        )

        val chipGroup = binding.suggestionChipGroup

        for (text in suggestions) {
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = text
            chip.setOnClickListener {
                sendMessage(text)
                binding.suggestionChipGroup.visibility = View.GONE
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
        binding.sendButton.setOnClickListener {
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