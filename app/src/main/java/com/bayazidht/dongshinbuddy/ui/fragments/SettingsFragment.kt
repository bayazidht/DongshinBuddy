package com.bayazidht.dongshinbuddy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bayazidht.dongshinbuddy.databinding.FragmentSettingsBinding
import androidx.core.net.toUri
import com.bayazidht.dongshinbuddy.utils.SettingsPrefs

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsPrefs: SettingsPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsPrefs = SettingsPrefs(requireContext())

        setupThemeSwitch()
        setupClickListeners()
        displayAppVersion()
    }

    private fun setupThemeSwitch() {
        binding.switchDarkMode.isChecked = settingsPrefs.isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsPrefs.isDarkMode = isChecked
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAboutUs.setOnClickListener {

        }

        binding.btnContactUs.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:bayazidht@gmail.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "Dongshin Buddy Support")
            }
            startActivity(intent)
        }

        binding.btnShareApp.setOnClickListener {
            shareApp()
        }
    }

    private fun shareApp() {
        val shareMessage = """
            Hi! I'm using Dongshin Buddy, an amazing AI companion for our university. 
            Download it now: https://play.google.com/store/apps/details?id=${requireContext().packageName}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out Dongshin Buddy")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun displayAppVersion() {
        val versionName = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName
        binding.tvVersion.text = "App Version $versionName"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}