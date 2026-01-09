package com.bayazidht.dongshinbuddy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.data.local.ChipsData
import com.bayazidht.dongshinbuddy.databinding.FragmentHomeBinding
import com.bayazidht.dongshinbuddy.ui.activities.ChatActivity
import com.bayazidht.dongshinbuddy.utils.AppConstants
import com.bayazidht.dongshinbuddy.utils.CustomTabHelper
import com.google.android.material.chip.Chip

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupQuestionsChips()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnStartChat.setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }

        binding.btnCampusMap.setOnClickListener {
            openChat("Show me all campus buildings and locations with links")
        }

        binding.btnHelpdesk.setOnClickListener {
            openChat("Show me all contact details of university")
        }

        binding.cardLogo.setOnClickListener {
            CustomTabHelper.openCustomTab(requireContext(), AppConstants.UNIVERSITY_URL)
        }
    }

    private fun setupQuestionsChips() {
        val chipGroup = binding.questionsChipGroup
        chipGroup.removeAllViews()

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
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("PREFILLED_QUERY", query)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}