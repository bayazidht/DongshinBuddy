package com.bayazidht.dongshinbuddy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bayazidht.dongshinbuddy.R
import com.bayazidht.dongshinbuddy.data.local.ChipsData
import com.bayazidht.dongshinbuddy.data.local.LinksData
import com.bayazidht.dongshinbuddy.databinding.FragmentHomeBinding
import com.bayazidht.dongshinbuddy.ui.activities.ChatActivity
import com.bayazidht.dongshinbuddy.utils.AppConstants
import com.bayazidht.dongshinbuddy.utils.CustomTabHelper
import com.bayazidht.dongshinbuddy.utils.DSUPrefs
import com.google.android.material.chip.Chip
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dsuPrefs: DSUPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAppBar()
        setupClickListeners()

        dsuPrefs = DSUPrefs(requireContext())
        setupQuestionsChips()
        setupClickListeners()
        setupQuickLinks()
    }

    private fun setupAppBar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val fraction = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
            if (fraction > 0.8f) {
                binding.collapsedContent.visibility = View.VISIBLE
                binding.collapsedContent.alpha = (fraction - 0.8f) * 5f
            } else {
                binding.collapsedContent.alpha = 0f
                binding.collapsedContent.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnStartChat.setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }

        binding.btnCampusMap.setOnClickListener {
            openChat(AppConstants.CAMPUS_MAP_PROMPT)
        }

        binding.btnHelpdesk.setOnClickListener {
            openChat(AppConstants.HELP_DESK_PROMPT)
        }
    }

    private fun setupQuestionsChips() {
        val chipGroup = binding.questionsChipGroup
        val questionsList = dsuPrefs.getSavedQuestions().ifEmpty { ChipsData.questions }
        chipGroup.removeAllViews()
        questionsList.forEach { query ->
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = query
            chip.setOnClickListener {
                openChat(query)
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupQuickLinks() {
        val container = binding.quickLinksContainer

        val savedLinks = dsuPrefs.getSavedQuickLinks()
        val linksList = savedLinks.ifEmpty { LinksData.defaultLinks }

        activity?.runOnUiThread {
            container.removeAllViews()

            if (linksList.isEmpty()) {
                return@runOnUiThread
            }

            linksList.forEach { link ->
                val itemView = layoutInflater.inflate(R.layout.item_settings, container, false)

                val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
                val iconView = itemView.findViewById<ImageView>(R.id.itemIcon)

                titleView.text = link["title"] ?: "Untitled"

                val iconName = link["icon"] ?: "ic_link"
                val resourceId = resources.getIdentifier(iconName, "drawable", requireContext().packageName)

                if (resourceId != 0) {
                    iconView.setImageResource(resourceId)
                } else {
                    iconView.setImageResource(R.drawable.ic_link)
                }

                itemView.setOnClickListener {
                    val url = link["url"] ?: ""
                    if (url.isNotEmpty()) {
                        CustomTabHelper.openCustomTab(requireContext(), url)
                    }
                }

                container.addView(itemView)
            }
        }
    }

    private fun openChat(query: String) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("PREFILLED_QUERY", query)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        setupQuestionsChips()
        setupQuickLinks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}