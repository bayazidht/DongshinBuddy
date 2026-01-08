package com.bayazidht.dongshinbuddy.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class CustomTabHelper {
    companion object {
        fun openCustomTab(context: Context, url: String) {
            val builder = CustomTabsIntent.Builder()
            builder.setShowTitle(false)
            builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

            val customTabsIntent = builder.build()
            try {
                customTabsIntent.launchUrl(context, Uri.parse(url))
            } catch (_: Exception) {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }
    }
}