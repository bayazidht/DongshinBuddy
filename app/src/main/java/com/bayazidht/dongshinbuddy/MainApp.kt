package com.bayazidht.dongshinbuddy

import android.app.Application
import androidx.core.graphics.toColorInt
import com.bayazidht.dongshinbuddy.utils.AppConstants
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val seedColor = AppConstants.THEME_COLOR.toColorInt()
        DynamicColors.applyToActivitiesIfAvailable(
            this,
            DynamicColorsOptions.Builder()
                .setContentBasedSource(seedColor)
                .build()
        )
    }
}