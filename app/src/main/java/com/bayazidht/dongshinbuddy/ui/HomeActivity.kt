package com.bayazidht.dongshinbuddy.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.databinding.ActivityHomeBinding

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
            startActivity(Intent(this, MainActivity::class.java))
        }

        for (i in 0 until binding.chipGroupQuestions.childCount) {
            val chip = binding.chipGroupQuestions.getChildAt(i) as? com.google.android.material.chip.Chip
            chip?.setOnClickListener {
                openChat(chip.text.toString())
            }
        }
    }

    private fun openChat(query: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("PREFILLED_QUERY", query)
        startActivity(intent)
    }
}