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
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.utils.AppConstants
import com.bayazidht.dongshinbuddy.utils.CustomTabHelper
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
        setupSettingsItems()
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

    private fun setupSettingsItems() {
        binding.itemAbout.apply {
            itemTitle.text = "About Us"
            itemIcon.setImageResource(R.drawable.ic_info)
            rootLayout.setOnClickListener {
                CustomTabHelper.openCustomTab(requireContext(), AppConstants.ABOUT_US_URL)
            }
        }
        binding.itemFeedback.apply {
            itemTitle.text = "Feedback"
            itemIcon.setImageResource(R.drawable.ic_email)
            rootLayout.setOnClickListener { sendFeedback() }
        }
        binding.itemShare.apply {
            itemTitle.text = "Share App"
            itemIcon.setImageResource(R.drawable.ic_share)
            rootLayout.setOnClickListener { shareApp() }
        }
        binding.itemPrivacy.apply {
            itemTitle.text = "Privacy Policy"
            itemIcon.setImageResource(R.drawable.ic_privacy)
            rootLayout.setOnClickListener {
                CustomTabHelper.openCustomTab(requireContext(), AppConstants.PRIVACY_POLICY_URL)
            }
        }
    }

    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:${AppConstants.FEEDBACK_MAIL}".toUri()
            putExtra(Intent.EXTRA_SUBJECT, "DongshinBuddy Feedback")
        }
        startActivity(intent)
    }

    private fun shareApp() {
        val shareMessage = """
            Hi! I'm using DongshinBuddy, an amazing AI companion for our university. 
            Download it now: https://play.google.com/store/apps/details?id=${requireContext().packageName}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out DongshinBuddy")
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