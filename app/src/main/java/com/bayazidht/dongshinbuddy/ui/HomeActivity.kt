package com.bayazidht.dongshinbuddy.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.data.local.ChipsData
import com.bayazidht.dongshinbuddy.databinding.ActivityHomeBinding
import com.google.android.material.chip.Chip

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnStartChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        setupQuestionsChips()

        binding.btnCampusMap.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("PREFILLED_QUERY", "Show me all campus buildings and locations with links")
            startActivity(intent)
        }
    }

    private fun setupQuestionsChips() {
        val chipGroup = binding.questionsChipGroup
        ChipsData.questions.forEach { query ->
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = query
            chip.setOnClickListener {
                openChat(query)
            }
            chipGroup.addView(chip)
        }
    }

    private fun openChat(query: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("PREFILLED_QUERY", query)
        startActivity(intent)
    }
}