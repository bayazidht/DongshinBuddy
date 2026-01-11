package com.bayazidht.dongshinbuddy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.api.RetrofitClient
import com.bayazidht.dongshinbuddy.data.repository.AppRepository
import com.bayazidht.dongshinbuddy.databinding.ActivityMainBinding
import com.bayazidht.dongshinbuddy.utils.DSUPrefs
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dsuPrefs: DSUPrefs
    private lateinit var repository: AppRepository

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

        dsuPrefs = DSUPrefs(this)
        repository = AppRepository(RetrofitClient.groqService, FirebaseFirestore.getInstance())
        syncFirebaseData()

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

    private fun syncFirebaseData() {
        repository.fetchAIConfig { config ->
            dsuPrefs.saveAIConfig(config)
            Log.d("Sync", "AI Config: primaryAi=${config.primaryAi}, groqModel=${config.groqModel}, geminiModel=${config.geminiModel},underMaintenance=${config.underMaintenance}")
        }

        repository.fetchUniversityInfo(
            onSuccess = { context, version ->
                if (version > dsuPrefs.getLocalVersion()) {
                    dsuPrefs.saveContext(context, version)
                    Log.d("Sync", "University context updated to v$version")
                }
            },
            onFailure = { Log.e("Sync", "Context sync failed: ${it.message}") }
        )

        repository.fetchChipsInfo(
            onSuccess = { questions, suggestions, version ->
                if (version > dsuPrefs.getChipsVersion()) {
                    dsuPrefs.saveChipsData(questions, suggestions, version)
                    Log.d("Sync", "Chips data updated to v$version")
                }
            },
            onFailure = { Log.e("Sync", "Chips sync failed: ${it.message}") }
        )

        repository.fetchQuickLinks(
            onSuccess = { links, version ->
                if (version > dsuPrefs.getLinksVersion()) {
                    dsuPrefs.saveQuickLinks(links, version)
                    Log.d("Sync", "Quick Links updated to v$version")
                }
            },
            onFailure = { Log.e("Sync", "Links sync failed: ${it.message}") }
        )
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