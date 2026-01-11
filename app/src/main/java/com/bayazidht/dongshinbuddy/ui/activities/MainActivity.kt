package com.bayazidht.dongshinbuddy.ui.activities

import SettingsPrefs
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bayazidht.dongshinbuddy.BuildConfig
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.api.RetrofitClient
import com.bayazidht.dongshinbuddy.data.repository.AppRepository
import com.bayazidht.dongshinbuddy.databinding.ActivityMainBinding
import com.bayazidht.dongshinbuddy.utils.CustomTabHelper
import com.bayazidht.dongshinbuddy.utils.DSUPrefs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        setupTheme()
    }

    private fun setupTheme() {
        val settingsPrefs = SettingsPrefs(this)
        when (settingsPrefs.themeMode) {
            SettingsPrefs.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            SettingsPrefs.THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun syncFirebaseData() {
        repository.fetchAIConfig { config ->
            dsuPrefs.saveAIConfig(config)
            Log.d("Sync", "AI Config Synced: ${config.primaryAi}")
        }

        repository.fetchVersions(
            onSuccess = { versions ->
                val cloudAiV = versions["ai_context_v"] as? Int ?: 0
                val cloudChipsV = versions["chips_data_v"] as? Int ?: 0
                val cloudLinksV = versions["quick_links_v"] as? Int ?: 0
                val cloudAppVersion = versions["app_version"] as? String ?: ""
                val isForceUpdate = versions["force_update"] as? Boolean ?: false

                if (cloudAppVersion.isNotEmpty() && cloudAppVersion != BuildConfig.VERSION_NAME) {
                    showUpdateDialog(cloudAppVersion, isForceUpdate)
                    if (isForceUpdate) return@fetchVersions
                }

                if (cloudAiV > dsuPrefs.getContextVersion()) {
                    repository.fetchAiContext(
                        onSuccess = { data ->
                            dsuPrefs.saveContext(data, cloudAiV)
                            Log.d("Sync", "Context updated to v$cloudAiV")
                        },
                        onFailure = { Log.e("Sync", "Context fetch failed") }
                    )
                }

                if (cloudChipsV > dsuPrefs.getChipsVersion()) {
                    repository.fetchChipsInfo(
                        onSuccess = { q, s ->
                            dsuPrefs.saveChipsData(q, s, cloudChipsV)
                            Log.d("Sync", "Chips updated to v$cloudChipsV")
                        },
                        onFailure = { Log.e("Sync", "Chips fetch failed") }
                    )
                }

                if (cloudLinksV > dsuPrefs.getLinksVersion()) {
                    repository.fetchQuickLinks(
                        onSuccess = { links ->
                            dsuPrefs.saveQuickLinks(links, cloudLinksV)
                            Log.d("Sync", "Links updated to v$cloudLinksV")
                        },
                        onFailure = { Log.e("Sync", "Links fetch failed") }
                    )
                }
            },
            onFailure = { e ->
                Log.e("Sync", "Version fetch failed: ${e.message}")
            }
        )
    }

    private fun showUpdateDialog(version: String, isForce: Boolean) {
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle("New Update Available")
            .setMessage("A newer version $version is available. Please update to enjoy the latest features and improvements.")
            .setPositiveButton("Update Now") { _, _ ->
                CustomTabHelper.openCustomTab(this, "https://play.google.com/store/apps/details?id=$packageName")
            }
        if (isForce) {
            builder.setCancelable(false)
        } else {
            builder.setNegativeButton("Later", null)
        }
        builder.show()
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