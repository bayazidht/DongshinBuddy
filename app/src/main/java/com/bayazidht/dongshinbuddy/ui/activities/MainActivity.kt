package com.bayazidht.dongshinbuddy.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.nav_settings)
                    true
                }
                R.id.nav_chat -> {
                    openChatActivity()
                    false
                }
                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.menu.findItem(destination.id)?.let {
                it.isChecked = true
            }
        }

        binding.fabChat.setOnClickListener {
            openChatActivity()
        }

        setUpBackAction(navController)
    }

    private fun setUpBackAction( navController: NavController) {
        onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id == R.id.nav_home) {
                finish()
            } else {
                binding.bottomNavigation.selectedItemId = R.id.nav_home
            }
        }
    }

    private fun openChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}